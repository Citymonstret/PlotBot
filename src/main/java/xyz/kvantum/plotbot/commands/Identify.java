package xyz.kvantum.plotbot.commands;

import com.intellectualsites.commands.Command;
import com.intellectualsites.commands.CommandDeclaration;
import com.intellectualsites.commands.CommandInstance;
import com.intellectualsites.commands.parser.impl.StringParser;
import lombok.var;
import net.dv8tion.jda.core.entities.Guild;
import xyz.kvantum.plotbot.DiscordCommandCaller;

@CommandDeclaration(usage = "identify <type> <thing>", command = "identify")
public class Identify extends Command {

  public Identify() {
    withArgument("type", new StringParser(), "emoji | user");
    withArgument("thing", new StringParser(), "thing to identify");
  }

  @Override
  public boolean onCommand(final CommandInstance instance) {
    final DiscordCommandCaller discordCommandCaller = (DiscordCommandCaller) instance.getCaller();
    final String thing =  instance.getString("thing");
    final Guild guild = discordCommandCaller.getChannel().getGuild();
    switch(instance.getString("type").toLowerCase()) {
      case "emoji": {
        final var list = guild.getEmotesByName(thing, true);
        if (list.isEmpty()) {
          discordCommandCaller.message("There's no such emoji in this guild");
        } else {
          discordCommandCaller.message(String.format("The emoji has ID %s", list.get(0).getId()));
        }
      } break;
      case "user": {
        discordCommandCaller.message("This isn't implemented yet. Chill plz.");
      } break;
      default: {
        discordCommandCaller.message("I don't know what " + instance.getString("type") + " is");
      } break;
    }
    return true;
  }
}
