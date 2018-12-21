package xyz.kvantum.plotbot.commands;

import com.intellectualsites.commands.Command;
import com.intellectualsites.commands.CommandDeclaration;
import com.intellectualsites.commands.CommandInstance;

@CommandDeclaration(command = "source", description = "Get link to source code", permission = "all")
public class Source extends Command {

    private static final String SOURCE = "https://github.com/Sauilitired/PlotBot";

    @Override public boolean onCommand(final CommandInstance instance) {
        instance.getCaller().message("I am open source at " + SOURCE);
        return true;
    }
}
