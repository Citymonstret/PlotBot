package xyz.kvantum.plotbot.commands;

import com.intellectualsites.commands.Command;
import com.intellectualsites.commands.CommandDeclaration;
import com.intellectualsites.commands.CommandInstance;
import xyz.kvantum.plotbot.DiscordCommandCaller;

@CommandDeclaration(command = "say", usage = "say <stuff>", permission = "say", description = "Say something I'm giving up on you...")
public class Say extends Command {

    @Override public boolean onCommand(final CommandInstance instance) {
        final DiscordCommandCaller discordCommandCaller = (DiscordCommandCaller) instance.getCaller();
        final StringBuilder message = new StringBuilder();
        for (final String arg : instance.getArguments()) {
            message.append(arg).append(" ");
        }
        discordCommandCaller.getMessage().delete().queue();
        discordCommandCaller.getChannel().sendMessageFormat("%s", message.toString()).queue();
        return true;
    }
}
