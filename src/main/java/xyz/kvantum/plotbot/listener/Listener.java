package xyz.kvantum.plotbot.listener;

import com.intellectualsites.commands.CommandHandlingOutput;
import com.intellectualsites.commands.CommandResult;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.slf4j.Logger;
import xyz.kvantum.plotbot.BotCommandManager;
import xyz.kvantum.plotbot.BotConfig;
import xyz.kvantum.plotbot.DiscordCommandCaller;

@RequiredArgsConstructor
public class Listener extends ListenerAdapter
{

    private final BotCommandManager commandManager;
    private final Logger logger;

    @Override
    public void onMessageReceived(final MessageReceivedEvent event)
    {
        // Ignore bot messages
        if ( event.getAuthor().isBot() )
        {
            return;
        }
        this.logger.info( "Message sent..: " + event.getMessage().getContentRaw() );
        final DiscordCommandCaller commandCaller = new DiscordCommandCaller( event.getTextChannel(), event.getMember() );
        final CommandResult result = this.commandManager.handle( commandCaller, event.getMessage().getContentRaw() );
        this.logger.info( "Status: " + CommandHandlingOutput.nameField( result.getCommandResult() ) );
        switch ( result.getCommandResult() )
        {
            case CommandHandlingOutput.NOT_PERMITTED:
                commandCaller.message( "Command Error: You are not allowed to execute that command!" );
                break;
            case CommandHandlingOutput.ERROR:
                commandCaller.message( "Something went wrong when executing the command!" );
                result.getStacktrace().printStackTrace();
                break;
            case CommandHandlingOutput.NOT_FOUND:
                if ( result.getClosestMatch() != null )
                {
                    commandCaller.message( "Did you mean: " + BotConfig.initialCharacter + result
                            .getClosestMatch().getCommand() );
                } else
                {
                    commandCaller.message( "There is no such command: " + result
                            .getInput() );
                }
                break;
            case CommandHandlingOutput.WRONG_USAGE:
                commandCaller.message( "Command Usage: " + result.getCommand().getUsage() );
                break;
            case CommandHandlingOutput.SUCCESS:
                break;
            case CommandHandlingOutput.NOT_COMMAND:
                break;
            default:
                commandCaller.message( "Unknown command result: " + CommandHandlingOutput.nameField(
                        result.getCommandResult() ) );
                break;
        }
    }
}
