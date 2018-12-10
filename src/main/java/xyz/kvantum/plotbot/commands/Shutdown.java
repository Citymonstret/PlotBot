package xyz.kvantum.plotbot.commands;

import com.intellectualsites.commands.Command;
import com.intellectualsites.commands.CommandDeclaration;
import com.intellectualsites.commands.CommandInstance;
import xyz.kvantum.plotbot.DiscordCommandCaller;

import java.util.Timer;
import java.util.TimerTask;

@CommandDeclaration(command = "shutdown", usage = "shutdown -k(eep) -q(uiet)", description = "Make me sleep", permission = "shutdown")
public class Shutdown extends Command {

    @Override public boolean onCommand(final CommandInstance instance) {
        final DiscordCommandCaller discordCommandCaller = (DiscordCommandCaller) instance.getCaller();

        boolean quiet = false;
        boolean remove = true;

        for (final String string : instance.getArguments()) {
            if (string.equalsIgnoreCase("-q") || string.equalsIgnoreCase("-quiet")) {
                quiet = true;
            } else if (string.equalsIgnoreCase("-k") || string.equalsIgnoreCase("-keep")) {
                remove = false;
            }
        }
        if (remove) {
            discordCommandCaller.getMessage().delete().queue();
        }
        if (!quiet) {
            discordCommandCaller.getChannel().sendMessageFormat("%s", "I go sleep now. Zzzzz!").queue();
        }
        new Timer().schedule(new TimerTask() {
            @Override public void run() {
                System.exit(0);
            }
        }, 1000L);
        return true;
    }
}
