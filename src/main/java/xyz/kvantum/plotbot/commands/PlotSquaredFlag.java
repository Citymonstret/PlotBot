package xyz.kvantum.plotbot.commands;

import com.github.intellectualsites.plotsquared.plot.flag.Flag;
import com.github.intellectualsites.plotsquared.plot.flag.Flags;
import com.github.intellectualsites.plotsquared.plot.util.StringComparison;
import com.intellectualsites.commands.Command;
import com.intellectualsites.commands.CommandDeclaration;
import com.intellectualsites.commands.CommandInstance;
import java.awt.Color;
import java.time.Instant;
import java.util.Collection;
import java.util.stream.Collectors;
import net.dv8tion.jda.core.EmbedBuilder;
import org.apache.commons.lang.StringUtils;
import xyz.kvantum.plotbot.DiscordCommandCaller;

@CommandDeclaration(command = "plotflag", permission = "all", usage = "!flag <flag>", aliases = {"pf", "flag", "pflag"} )
public class PlotSquaredFlag extends Command {

  private static final Collection<Flag<?>> flags = Flags.getFlags();

  private static final Collection<String> getFlagNames() {
    return flags.stream().map(Flag::getName).collect(Collectors.toList());
  }

  @Override
  public boolean onCommand(CommandInstance instance) {
    final DiscordCommandCaller discordCommandCaller = (DiscordCommandCaller) instance.getCaller();

    if (instance.getArguments().length < 1) {
      return false;
    }

    if (instance.getArguments()[0].equalsIgnoreCase("list")) {
      discordCommandCaller.message(String.format("Available flags: %s", StringUtils.join(getFlagNames(), ", ")));
      return true;
    }

    final String flag = instance.getArguments()[0].toLowerCase();
    final Flag flg = Flags.getFlag(flag);
    if (flg == null) {
      discordCommandCaller.message("That is not a valid flag");
      try {
        final String rflag = new StringComparison<>(flag, getFlagNames())
            .getMatchObject();
        if (rflag != null) {
          discordCommandCaller.message(String.format("Did you mean: %s", rflag));
        }
      } catch (final Exception e) {
        e.printStackTrace();
      }
    } else {
      Object value = null;
      if (instance.getArguments().length > 1) {
        try {
          final StringBuilder arg = new StringBuilder();
          for (int i = 1; i < instance.getArguments().length; i++) {
            arg.append(instance.getArguments()[i]);
            if ((i + 1) < instance.getArguments().length) {
              arg.append(" ");
            }
          }
          value = flg.parseValue(arg.toString());
        } catch (final Exception e) {
          value = String.format("Failed to parse: %s", e.getMessage());
          e.printStackTrace();
        } finally {
          if (value == null) {
            value = "Invalid value";
          }
        }
      }

      final EmbedBuilder embed = new EmbedBuilder().setTitle("Flag Information", "https://github.com/IntellectualSites/PlotSquared/blob/breaking/Core/src/main/java/com/github/intellectualsites/plotsquared/plot/flag/Flags.java")
          .addField("Flag", "/plot flag set " + flg.getName() + " <value>", true)
          .addField("Usage", flg.getValueDescription(), true)
          .addField("Type", flg.getClass().getSimpleName(), true);

      if (value != null) {
          embed.addField("Flag response when parsing value \"" + instance.getArguments()[1] + "\"", value.toString(), false);
      }

      embed.setColor(Color.red)
          .setFooter("Requested by: " + discordCommandCaller.getMessage().getMember().getEffectiveName(), discordCommandCaller.getMessage().getMember().getUser().getAvatarUrl())
          .setTimestamp(Instant.now())
          .build();
      discordCommandCaller.getChannel().sendMessage(embed.build()).queue();
    }
    return true;
  }
}
