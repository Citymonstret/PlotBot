package xyz.kvantum.plotbot.commands;

import com.intellectualsites.commands.Command;
import com.intellectualsites.commands.CommandDeclaration;
import com.intellectualsites.commands.CommandInstance;
import java.awt.Color;
import java.io.File;
import java.time.Instant;
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
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.MessageEmbed;
import xyz.kvantum.plotbot.DiscordCommandCaller;
import xyz.kvantum.plotbot.LinkObject;
import xyz.kvantum.plotbot.PlotBot;
import xyz.kvantum.plotbot.configuration.ConfigurationSection;
import xyz.kvantum.plotbot.configuration.file.FileConfiguration;
import xyz.kvantum.plotbot.configuration.file.YamlConfiguration;
import xyz.kvantum.plotbot.configuration.serialization.ConfigurationSerialization;

@CommandDeclaration(command = "link", usage = "!link [name]", description = "Get a link",
    permission = "all", aliases = {"l", "links", "url", "address"})
public class Link extends Command {

  private static final Color COLOR = Color.CYAN;

  @Getter
  @Setter
  private static Link instance;

  private final Map<String, LinkObject> links = new HashMap<>();
  private final File linkFile;
  private FileConfiguration fileConfiguration;

  public Link() {
    this.linkFile = new File("./links.yml");
    if (!this.linkFile.exists()) {
      try {
        if (!this.linkFile.createNewFile()) {
          throw new IllegalStateException("Failed to create links.yml");
        }
      } catch (final Exception e) {
        throw new RuntimeException("Failed to initialize the link command", e);
      }
    }
    ConfigurationSerialization.registerClass(LinkObject.class);
    try {
      this.fileConfiguration = YamlConfiguration.loadConfiguration(this.linkFile);
      if (!this.fileConfiguration.contains("links")) {
        this.save();
      } else {
        final ConfigurationSection links = this.fileConfiguration.getConfigurationSection("links");
        final List<String> keys = new ArrayList<>(links.getKeys(false));
        keys.forEach(key -> this.links.put(key, (LinkObject) links.get(key)));
      }
    } catch (final Exception e) {
      PlotBot.getInstance().getLogger().error("Failed to load links", e);
    }
  }

  private void save() {
    try {
      this.fileConfiguration.set("links", this.links);
      this.fileConfiguration.save(this.linkFile);
    } catch (final Exception e) {
      PlotBot.getInstance().getLogger().error("Failed to save links", e);
    }
  }

  @Override
  public boolean onCommand(CommandInstance instance) {
    final DiscordCommandCaller discordCommandCaller =
        (DiscordCommandCaller) instance.getCaller();
    final String[] args = instance.getArguments();
    if (args.length == 0 || args[0].equalsIgnoreCase("list")) {
      final StringBuilder builder = new StringBuilder();
      final Iterator<Entry<String, LinkObject>> iterator = this.links.entrySet().iterator();
      while (iterator.hasNext()) {
        final Map.Entry<String, LinkObject> entry = iterator.next();
        builder.append(entry.getKey());
        if (iterator.hasNext()) {
          builder.append(", ");
        }
      }
      discordCommandCaller.message("Available links: " + builder.toString());
    } else if (args[0].equalsIgnoreCase("add")) {
      if (!discordCommandCaller.getMessage().getMember().hasPermission(Permission.ADMINISTRATOR)) {
        discordCommandCaller.message("You do not have permissions to add new links!");
      } else {
        if (args.length > 3) {
           final String key = args[1].toLowerCase(Locale.ENGLISH);
           final String url = args[2];
           final StringBuilder description = new StringBuilder();
           for (int i = 3; i < args.length; i++) {
             description.append(args[i]).append(" ");
           }
           this.addLink(key, url, description.toString());
           discordCommandCaller.message("Added link with name: " + key);
        } else {
          discordCommandCaller.message("Usage: !link add <name> <url> <desc...>");
        }
      }
    } else {
      String name = args[0].toLowerCase();
      boolean ignoreError = name.startsWith("!");
      if (ignoreError) {
        name = name.substring(1);
      }

      if (!this.links.containsKey(name)) {
        if (!ignoreError) {
          discordCommandCaller.message("There is no such link stored! "
              + "Use `!link list` to get a list of all available links");
        }
      } else {
        // discordCommandCaller.getMessage().delete().queue();
        final LinkObject object = this.links.get(name);
        final MessageEmbed embed = new EmbedBuilder().setTitle("Link: " + name)
            .addField("URL: ", object.getLink(), false)
            .addField("Description: ", object.getDesc(), false)
            .setColor(COLOR)
            .setFooter("Requested by: " + discordCommandCaller.getMessage().getMember().getEffectiveName(), discordCommandCaller.getMessage().getMember().getUser().getAvatarUrl())
            .setTimestamp(Instant.now())
            .build();
        discordCommandCaller.getChannel().sendMessage(embed).queue();
      }
    }
    return true;
  }

  private void addLink(@NonNull final String key, @NonNull final String link, @NonNull final String desc) {
    this.links.put(key, new LinkObject(key, link, desc));
    this.save();
  }

}
