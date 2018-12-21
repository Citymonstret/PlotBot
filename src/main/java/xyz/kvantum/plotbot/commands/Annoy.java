package xyz.kvantum.plotbot.commands;

import com.intellectualsites.commands.Command;
import com.intellectualsites.commands.CommandDeclaration;
import com.intellectualsites.commands.CommandInstance;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import xyz.kvantum.plotbot.Annoyer;
import xyz.kvantum.plotbot.DiscordCommandCaller;

@CommandDeclaration(usage = "annoy @user", description = "Annoy someone ;)", command = "annoy", permission = "annoy")
public class Annoy extends Command {

  @Override public boolean onCommand(final CommandInstance instance) {
    final DiscordCommandCaller discordCommandCaller = (DiscordCommandCaller) instance.getCaller();
    final Message message = discordCommandCaller.getMessage();
    final Member member;
    if (discordCommandCaller.getMessage().getMentionedMembers().isEmpty()) {
      member = null;
    } else {
      member = discordCommandCaller.getMessage().getMentionedMembers().get(0);
    }

    message.delete().queue();
    if (member == null) {
      discordCommandCaller.message("You need to specify a name!");
    } else {
      Annoyer.getInstance().toggleAnnoy(member);
      discordCommandCaller.message("Toggled ;)");
    }
    return true;
  }

}
