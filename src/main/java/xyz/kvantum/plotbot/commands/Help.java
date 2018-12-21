package xyz.kvantum.plotbot.commands;

import com.intellectualsites.commands.Command;
import com.intellectualsites.commands.CommandDeclaration;
import com.intellectualsites.commands.CommandInstance;
import net.dv8tion.jda.core.MessageBuilder;
import xyz.kvantum.plotbot.DiscordCommandCaller;
import xyz.kvantum.plotbot.PlotBot;

@CommandDeclaration(command = "help", description = "This command!", permission = "all") public class Help
    extends Command {

    @Override public boolean onCommand(CommandInstance instance) {
        final DiscordCommandCaller discordCommandCaller =
            (DiscordCommandCaller) instance.getCaller();
        MessageBuilder messageBuilder = new MessageBuilder();
        messageBuilder.append("Hello ").append(discordCommandCaller.getSuperCaller())
            .append(", these are the commands that you are permitted to use: ");
        for (final Command command : PlotBot.getInstance().getCommandManager().getCommands()) {
            if (discordCommandCaller.hasAttachment(command.getPermission())) {
                messageBuilder.appendFormat("\n- **!%s** - %s", command.getCommand(),
                    command.getDescription());
                if (!command.getUsage().isEmpty()) {
                    messageBuilder.appendFormat(" (Usage: %s)", command.getUsage());
                }
            }
        }
        discordCommandCaller.getSuperCaller().getUser().openPrivateChannel().queue(channel -> {
            channel.sendMessage(messageBuilder.build()).queue();
        });
        return true;
    }
}
