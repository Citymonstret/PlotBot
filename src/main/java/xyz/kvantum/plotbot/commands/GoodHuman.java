package xyz.kvantum.plotbot.commands;

import com.intellectualsites.commands.Command;
import com.intellectualsites.commands.CommandDeclaration;
import com.intellectualsites.commands.CommandInstance;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import xyz.kvantum.plotbot.DiscordCommandCaller;

@CommandDeclaration(command = "goodhuman", description = "Who's a good human?", usage = "goodhuman @human -m(ention)", aliases = "gh", permission = "goodhuman")
public class GoodHuman extends Command {

    @Override public boolean onCommand(final CommandInstance instance) {
        final DiscordCommandCaller discordCommandCaller = (DiscordCommandCaller) instance.getCaller();
        final Message message = discordCommandCaller.getMessage();
        final Member member;
        if (discordCommandCaller.getMessage().getMentionedMembers().isEmpty()) {
            member = null;
        } else {
            member = discordCommandCaller.getMessage().getMentionedMembers().get(0);
        }

        boolean mention = false;
        for (final String argument : instance.getArguments()) {
            if (argument.equalsIgnoreCase("-m") || argument.equalsIgnoreCase("-mention")) {
                mention = true;
                break;
            }
        }

        message.delete().queue();
        if (member == null) {
            discordCommandCaller.message("You need to specify a name!");
        } else {
            if (mention) {
                discordCommandCaller.getChannel().sendMessageFormat("%s thinks %s is a good human!", discordCommandCaller.getSuperCaller(), member).queue();
            } else {
                discordCommandCaller.getChannel().sendMessageFormat("%s is a good human!", member).queue();
            }
        }
        return true;
    }
}
