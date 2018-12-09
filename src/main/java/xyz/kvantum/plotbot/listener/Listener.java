package xyz.kvantum.plotbot.listener;

import com.intellectualsites.commands.CommandHandlingOutput;
import com.intellectualsites.commands.CommandResult;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.slf4j.Logger;
import xyz.kvantum.plotbot.BotCommandManager;
import xyz.kvantum.plotbot.BotConfig;
import xyz.kvantum.plotbot.DiscordCommandCaller;
import xyz.kvantum.plotbot.PlotBot;
import xyz.kvantum.plotbot.text.TextPrompt;
import xyz.kvantum.plotbot.text.TextPromptManager;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor public class Listener extends ListenerAdapter {
    private static final Pattern CALL_ME_PATTERN =
        Pattern.compile("call me (?<name>[A-Za-z0-9_]+)");

    private final BotCommandManager commandManager;
    private final TextPromptManager textPromptManager = new TextPromptManager();
    private final Logger logger;

    @Override public void onPrivateMessageReceived(final PrivateMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }
        event.getAuthor().openPrivateChannel()
            .queue(consumer -> consumer.sendMessage("You know, this is quite creepy...").queue());
    }

    @Override public void onMessageReceived(final MessageReceivedEvent event) {
        // Ignore bot messages
        if (event.getAuthor().isBot()) {
            return;
        }
        PlotBot.getInstance().getHistoryManager().logMessage(event.getMessage());
        this.logger.info("Message sent..: " + event.getMessage().getContentRaw());
        final DiscordCommandCaller commandCaller =
            new DiscordCommandCaller(event.getTextChannel(), event.getMessage(), event.getMember());
        final CommandResult result =
            this.commandManager.handle(commandCaller, event.getMessage().getContentRaw());
        this.logger.info("Status: " + CommandHandlingOutput.nameField(result.getCommandResult()));
        switch (result.getCommandResult()) {
            case CommandHandlingOutput.NOT_PERMITTED:
                commandCaller
                    .message("Command Error: You are not allowed to execute that command!");
                break;
            case CommandHandlingOutput.ERROR:
                commandCaller.message("Something went wrong when executing the command!");
                result.getStacktrace().printStackTrace();
                break;
            case CommandHandlingOutput.NOT_FOUND:
                if (result.getClosestMatch() != null) {
                    commandCaller.message(
                        "Did you mean: " + BotConfig.initialCharacter + result.getClosestMatch()
                            .getCommand());
                } else {
                    commandCaller.message("There is no such command: " + result.getInput());
                }
                break;
            case CommandHandlingOutput.WRONG_USAGE:
                commandCaller.message("Command Usage: " + result.getCommand().getUsage());
                break;
            case CommandHandlingOutput.SUCCESS:
                break;
            case CommandHandlingOutput.NOT_COMMAND:
                if (event.getGuild() == null) {
                    break;
                }
                if (!event.getMessage().getMentionedMembers().isEmpty()) {
                    for (final Member member : event.getMessage().getMentionedMembers()) {
                        if (member.getUser().getIdLong() == PlotBot.getInstance().getJda()
                            .getSelfUser().getIdLong()) {
                            if (event.getMessage().getContentRaw().contains("who") && event
                                .getMessage().getContentStripped().contains("daddy")) {
                                commandCaller.message("Citymonstret is my daddy ;)");
                            } else if (event.getMessage().getContentRaw().contains("call me")) {
                                final Matcher matcher = CALL_ME_PATTERN
                                    .matcher(event.getMessage().getContentStripped());
                                if (!matcher.matches()) {
                                    commandCaller.message(
                                        "Sorry, I didn't understand that. Please make some sense.");
                                } else {

                                }
                            } else {
                                commandCaller.message("Don't @ me, bro! :angry:");
                            }
                            break;
                        }
                    }
                }

                // Then we instead handle the command as a text prompt
                logger.info("Checking for text prompts");
                final Optional<TextPrompt> textPrompt =
                    textPromptManager.getPrompt(result.getInput());
                if (textPrompt.isPresent()) {
                    logger.info("Found prompt: " + textPrompt.get().getPromptText());
                    textPrompt.get().handle(commandCaller);
                } else {
                    logger.info("No prompt found!");
                }
                break;
            default:
                commandCaller.message("Unknown command result: " + CommandHandlingOutput
                    .nameField(result.getCommandResult()));
                break;
        }
    }
}
