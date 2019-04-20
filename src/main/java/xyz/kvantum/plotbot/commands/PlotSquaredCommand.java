package xyz.kvantum.plotbot.commands;

import com.github.intellectualsites.plotsquared.plot.commands.MainCommand;
import com.github.intellectualsites.plotsquared.plot.util.StringComparison;
import com.intellectualsites.commands.Command;
import com.intellectualsites.commands.CommandDeclaration;
import com.intellectualsites.commands.CommandInstance;
import com.intellectualsites.commands.parser.impl.StringParser;
import java.awt.Color;
import java.time.Instant;
import java.util.Collection;
import lombok.NonNull;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import org.apache.commons.lang.StringUtils;
import xyz.kvantum.plotbot.DiscordCommandCaller;

@CommandDeclaration(command = "plotcommand", permission = "all", usage = "!plotcommand <command>", aliases = {"pc", "plot", "plotme", "pcommand", "pcmd"} )
public class PlotSquaredCommand extends Command {

  private static final MainCommand mainCommand = MainCommand.getInstance();

  private static com.github.intellectualsites.plotsquared.commands.Command getCommand(@NonNull final String command) {
    return mainCommand.getCommand(command);
  }

  public PlotSquaredCommand() {
    withArgument("command", new StringParser(), "PlotSquared command");
  }

  @Override
  public boolean onCommand(CommandInstance instance) {
    final DiscordCommandCaller discordCommandCaller = (DiscordCommandCaller) instance.getCaller();
    final String command = instance.getString("command");
    final com.github.intellectualsites.plotsquared.commands.Command cmd = getCommand(command);

    if (command.equalsIgnoreCase("list")) {
      discordCommandCaller.message(String.format("Available commands: %s", StringUtils.join(mainCommand.getCommands(), ", ")));
      return true;
    }

    if (cmd == null) {
      discordCommandCaller.message("That is not a valid subcommand");
      try {
        final com.github.intellectualsites.plotsquared.commands.Command rcmd =
            new StringComparison<>(command, mainCommand.getCommands()).getMatchObject();
        if (rcmd != null) {
          discordCommandCaller.message(String.format("Did you mean: %s", rcmd.getId()));
        }
      } catch (final Exception e) {
        e.printStackTrace();
      }
    } else {
      final MessageEmbed embed = new EmbedBuilder().setTitle("Command Information", "https://github.com/IntellectualSites/PlotSquared/tree/breaking/Core/src/main/java/com/github/intellectualsites/plotsquared/plot/commands/" + cmd.getClass().getSimpleName() + ".java")
          .addField("Command", "/plot " + cmd.getId(), true)
          .addField("Usage", cmd.getUsage(), true)
          .addField("Description", cmd.getDescription(), false)
          .addField("Aliases", formatAliases(cmd.getAliases()), true)
          .addField("Permission", cmd.getPermission(), true)
          .addField("Required Caller", cmd.getRequiredType().name(), true)
          .setColor(Color.pink)
          .setFooter("Requested by: " + discordCommandCaller.getMessage().getMember().getEffectiveName(), discordCommandCaller.getMessage().getMember().getUser().getAvatarUrl())
          .setTimestamp(Instant.now())
          .build();
      discordCommandCaller.getChannel().sendMessage(embed).queue();
    }
    return true;
  }

  private static String formatAliases(@NonNull final Collection<String> aliases) {
    if (aliases.isEmpty()) {
      return "none";
    }
    return StringUtils.join(aliases, ", ");
  }

}
