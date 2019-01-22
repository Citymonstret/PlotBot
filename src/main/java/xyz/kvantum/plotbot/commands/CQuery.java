package xyz.kvantum.plotbot.commands;

import com.github.intellectualsites.plotsquared.plot.config.C;
import com.intellectualsites.commands.Command;
import com.intellectualsites.commands.CommandDeclaration;
import com.intellectualsites.commands.CommandInstance;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.dv8tion.jda.core.EmbedBuilder;
import xyz.kvantum.plotbot.DiscordCommandCaller;

@CommandDeclaration(command = "cquery", aliases = "cq", description = "Query PlotSquared messages",
    usage = "!cq <query>", permission = "all")
public class CQuery extends Command {

  @Override
  public boolean onCommand(final CommandInstance instance) {
    final DiscordCommandCaller discordCommandCaller = (DiscordCommandCaller) instance.getCaller();

    if (instance.getArguments().length < 1) {
      discordCommandCaller.message("Missing query...");
      return false;
    }

    List<C> results = new ArrayList<>(C.values().length);
    outer: for (final C message : C.values()) {
      for (String query : instance.getArguments()) {
        query = query.toLowerCase(Locale.ENGLISH);
        if (!message.name().toLowerCase(Locale.ENGLISH).contains(query) && !message.s().toLowerCase(Locale.ENGLISH).contains(query)) {
          continue outer;
        }
      }
      results.add(message);
    }
    final EmbedBuilder embedBuilder = new EmbedBuilder().setTitle("Query Results");
    if (results.isEmpty()) {
      embedBuilder.addField("Results", "None :(", false);
    } else if (results.size() > 10) {
      embedBuilder.addField("Warning", "Query was too broad. Only 10 results shown", false);
      results = results.subList(0, 10);
    }
    for (final C result : results) {
      embedBuilder.addField(result.name(), result.s(), false);
    }
    discordCommandCaller.getChannel().sendMessage(embedBuilder.setFooter("Requested by: " + discordCommandCaller.getMessage().getMember().getEffectiveName(), discordCommandCaller.getMessage().getMember().getUser().getAvatarUrl())
        .setTimestamp(Instant.now()).build()).queue();
    return true;
  }
}
