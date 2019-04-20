package xyz.kvantum.plotbot.commands;

import com.intellectualsites.commands.Command;
import com.intellectualsites.commands.CommandDeclaration;
import com.intellectualsites.commands.CommandInstance;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.var;
import net.dv8tion.jda.core.Permission;
import xyz.kvantum.plotbot.DiscordCommandCaller;
import xyz.kvantum.plotbot.LinkObject;
import xyz.kvantum.plotbot.PlotBot;
import xyz.kvantum.plotbot.configuration.ConfigurationSection;
import xyz.kvantum.plotbot.configuration.MemorySection;
import xyz.kvantum.plotbot.configuration.file.FileConfiguration;
import xyz.kvantum.plotbot.configuration.file.YamlConfiguration;
import xyz.kvantum.plotbot.configuration.serialization.ConfigurationSerialization;

@CommandDeclaration(command = "macro", usage = "!macro [name] <@user | #channel>",
    description = "Execute a macro (replaces $user, $channel and $sender)",
  permission = "all", aliases = {"m", "execute", "run"})
public class Macro extends Command {

  @Getter
  @Setter
  private static Macro instance;

  private final Map<String, LinkObject> macros = new HashMap<>();
  private final File macroFile;
  private FileConfiguration fileConfiguration;

  public Macro() {
    this.macroFile = new File("./macros.yml");
    if (!this.macroFile.exists()) {
      try {
        if (!this.macroFile.createNewFile()) {
          throw new IllegalStateException("Failed to create macros.yml");
        }
      } catch (final Exception e) {
        throw new RuntimeException("Failed to initialize the macro command", e);
      }
    }
    ConfigurationSerialization.registerClass(LinkObject.class);
    try {
      this.fileConfiguration = YamlConfiguration.loadConfiguration(this.macroFile);
      if (!this.fileConfiguration.contains("macros")) {
        this.save();
      } else {
        final ConfigurationSection links = this.fileConfiguration.getConfigurationSection("macros");
        final List<String> keys = new ArrayList<>(links.getKeys(false));
        keys.forEach(key -> {
          final Object raw = links.get(key);
          if (raw instanceof LinkObject) {
            this.macros.put(key, (LinkObject) raw);
          } else {
            final MemorySection section = (MemorySection) raw;
            PlotBot.getInstance().getLogger().info(section.toString());
          }
        });
      }
    } catch (final Exception e) {
      PlotBot.getInstance().getLogger().error("Failed to load macros", e);
    }
  }

  private void save() {
    try {
      this.fileConfiguration.set("macros", this.macros);
      this.fileConfiguration.save(this.macroFile);
    } catch (final Exception e) {
      PlotBot.getInstance().getLogger().error("Failed to save macros", e);
    }
  }

  @Override
  public boolean onCommand(CommandInstance instance) {
    final DiscordCommandCaller discordCommandCaller =
        (DiscordCommandCaller) instance.getCaller();
    final String[] args = instance.getArguments();
    if (args.length == 0 || args[0].equalsIgnoreCase("list")) {
      final StringBuilder builder = new StringBuilder();
      final Iterator<Entry<String, LinkObject>> iterator = this.macros.entrySet().iterator();
      while (iterator.hasNext()) {
        final Map.Entry<String, LinkObject> entry = iterator.next();
        builder.append(entry.getKey());
        if (iterator.hasNext()) {
          builder.append(", ");
        }
      }
      discordCommandCaller.message("Available macros: " + builder.toString());
    } else if (args[0].equalsIgnoreCase("add")) {
      if (!discordCommandCaller.getMessage().getMember().hasPermission(Permission.ADMINISTRATOR)) {
        discordCommandCaller.message("You do not have permissions to add new macros!");
      } else {
        if (args.length > 2) {
          final String key = args[1].toLowerCase(Locale.ENGLISH);
          // final String url = args[2];
          final StringBuilder description = new StringBuilder();
          for (int i = 2; i < args.length; i++) {
            description.append(args[i]).append(" ");
          }
          this.addMacro(key, "", description.toString());
          discordCommandCaller.message("Added macro with name: " + key);
        } else {
          discordCommandCaller.message("Usage: !macro add <name> <desc...>");
        }
      }
    } else {
      String name = args[0].toLowerCase();
      boolean ignoreError = name.startsWith("!");
      if (ignoreError) {
        name = name.substring(1);
      }

      if (!this.macros.containsKey(name)) {
        if (!ignoreError) {
          discordCommandCaller.message("There is no such macro stored! "
              + "Use `!macro list` to get a list of all available macros");
        }
      } else {
        String message = this.macros.get(name).getDesc();
        final var mentioned = discordCommandCaller.getMessage().getMentionedMembers();
        if (message.contains("$user")) {
          if (mentioned.isEmpty()) {
            message = message.replace("$user", "");
          } else {
            message = message.replace("$user", mentioned.get(0).getAsMention());
          }
        } else if (!mentioned.isEmpty()) {
          message = String.format("%s - %s", mentioned.get(0), message);
        }
        if (message.contains("$channel")) {
          final var channel = discordCommandCaller.getMessage().getMentionedChannels();
          if (channel.isEmpty()) {
            message = message.replace("$channel", "");
          } else {
            message = message.replace("$channel", channel.get(0).getAsMention());
          }
        }
        if (message.contains("$sender")) {
          message = message.replace("$sender", discordCommandCaller.getSuperCaller().getAsMention());
        }
        discordCommandCaller.getMessage().delete().complete();
        discordCommandCaller.getChannel().sendMessageFormat(message).queue();
      }
    }
    return true;
  }

  private void addMacro(@NonNull final String key, @NonNull final String macro, @NonNull final String desc) {
    this.macros.put(key, new LinkObject(key, macro, desc));
    this.save();
  }

}
