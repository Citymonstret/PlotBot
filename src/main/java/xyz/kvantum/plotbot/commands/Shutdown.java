package xyz.kvantum.plotbot.commands;

import com.intellectualsites.commands.Command;
import com.intellectualsites.commands.CommandDeclaration;
import com.intellectualsites.commands.CommandInstance;
import xyz.kvantum.plotbot.DiscordCommandCaller;

import java.util.Timer;
import java.util.TimerTask;

@CommandDeclaration(command = "shutdown", usage = "shutdown", description = "Make me sleep", permission = "shutdown")
public class Shutdown extends Command {

    @Override public boolean onCommand(final CommandInstance instance) {
        final DiscordCommandCaller discordCommandCaller = (DiscordCommandCaller) instance.getCaller();
        discordCommandCaller.getMessage().delete().queue();
        discordCommandCaller.getChannel().sendMessageFormat("%s", "I go sleep now. Zzzzz!").queue();
        new Timer().schedule(new TimerTask() {
            @Override public void run() {
                System.exit(0);
            }
        }, 1000L);
        return true;
    }
}
