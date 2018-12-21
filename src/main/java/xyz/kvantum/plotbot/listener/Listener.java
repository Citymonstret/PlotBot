package xyz.kvantum.plotbot.listener;

import com.intellectualsites.commands.CommandHandlingOutput;
import com.intellectualsites.commands.CommandResult;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.var;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.slf4j.Logger;
import xyz.kvantum.plotbot.Annoyer;
import xyz.kvantum.plotbot.BotCommandManager;
import xyz.kvantum.plotbot.BotConfig;
import xyz.kvantum.plotbot.BotConfig.AutoRank;
import xyz.kvantum.plotbot.BotConfig.Guild;
import xyz.kvantum.plotbot.DiscordCommandCaller;
import xyz.kvantum.plotbot.PlotBot;
import xyz.kvantum.plotbot.text.TextPrompt;
import xyz.kvantum.plotbot.text.TextPromptManager;

@RequiredArgsConstructor
public class Listener extends ListenerAdapter {

  private static final Pattern CALL_ME_PATTERN =
      Pattern.compile("call me (?<name>[A-Za-z0-9_]+)");

  private final BotCommandManager commandManager;
  private final TextPromptManager textPromptManager = new TextPromptManager();
  private final Logger logger;

  @Override
  public void onPrivateMessageReceived(final PrivateMessageReceivedEvent event) {
    if (event.getAuthor().isBot()) {
      return;
    }
    event.getAuthor().openPrivateChannel()
        .queue(consumer -> consumer.sendMessage("You know, this is quite creepy...").queue());
  }

  @Override
  public void onGuildMemberJoin(final GuildMemberJoinEvent event) {
    final int size = event.getGuild().getMembers().size();
    event.getGuild().getTextChannelsByName(Guild.announcementChannel, true)
        .get(0).sendMessage(String.format("Let's party like it's %s. Welcome %s!", size,
        event.getMember().getEffectiveName())).queue();
    sendInfoMsg(size, event.getGuild());
  }

  @Override
  public void onMessageReactionAdd(final MessageReactionAddEvent event) {
    if (!isNotificaitonRankMessage(event)) {
      return;
    }
    final String rank = isValidReaction(event);
    if (rank == null) {
      event.getReaction().removeReaction(event.getUser()).complete();
      return;
    }
    final var roles = event.getGuild().getRolesByName(rank, true);
    if (roles.isEmpty()) {
      event.getChannel().sendMessageFormat(
          "%s - The automatic roles have not been configured correctly. Please contact a staff member", event.getMember())
          .queue();
    } else {
      event.getGuild().getController().addSingleRoleToMember(event.getMember(), roles.get(0))
          .queue();
    }
  }

  @Override
  public void onMessageReactionRemove(MessageReactionRemoveEvent event) {
    if (!isNotificaitonRankMessage(event)) {
      return;
    }
    final String rank = isValidReaction(event);
    if (rank == null) {
      return;
    }
    final var roles = event.getGuild().getRolesByName(rank, true);
    if (roles.isEmpty()) {
      event.getChannel().sendMessageFormat(
          "%s - The automatic roles have not been configured correctly. Please contact a staff member",
          event.getMember()).queue();
    } else {
      event.getGuild().getController().removeSingleRoleFromMember(event.getMember(), roles.get(0))
          .queue();
    }
  }

  private String isValidReaction(final GenericMessageReactionEvent event) {
    if (event.getReactionEmote().getName().equals(AutoRank.emojiOne)) {
      return AutoRank.rankOne;
    } else if (event.getReactionEmote().getName().equals(AutoRank.emojiTwo)) {
      return AutoRank.rankTwo;
    }
    return null;
  }

  private boolean isNotificaitonRankMessage(final GenericMessageReactionEvent event) {
    if (event.getUser().isBot()) {
      return false;
    }
    final MessageChannel channel = event.getChannel();
    final Message message = channel.getMessageById(event.getMessageId()).complete();
    return message.getAuthor().isBot() && message.getContentRaw()
        .contains("Project Notification Roles");
  }

  private void sendInfoMsg(final int size,
      @NonNull final net.dv8tion.jda.core.entities.Guild guild) {
    final int nextThousand = (int) (Math.floor(size / 1000f) + 1) * 1000;
    final int left = nextThousand - size;
    guild.getTextChannelsByName(Guild.announcementChannel, true)
        .get(0).sendMessage(
        String.format("Only %d members left until we've reached %d!", left, nextThousand)).queue();
  }

  @Override
  public void onMessageReceived(final MessageReceivedEvent event) {
    // Ignore bot messages
    if (event.getAuthor().isBot()) {
      return;
    }

    if (Annoyer.getInstance().shouldAnnoy(event.getMember())) {
      event.getChannel()
          .sendMessageFormat("%s " + Annoyer.getInstance().getRandomMessage(), event.getMember())
          .queue();
    }

    PlotBot.getInstance().getHistoryManager().logMessage(event.getMessage());
    this.logger.info("Message sent..: " + event.getMessage().getContentRaw());
    final DiscordCommandCaller commandCaller =
        new DiscordCommandCaller(event.getTextChannel(), event.getMessage(), event.getMember());

    final String message = event.getMessage().getContentRaw();
    final String[] messages;
    if (message.contains("&&")) {
      messages = message.split(" && ");
    } else {
      messages = new String[]{message};
    }

    for (final String currentMessage : messages) {
      final CommandResult result =
          this.commandManager.handle(commandCaller, currentMessage);
      this.logger.info("Status: " + CommandHandlingOutput.nameField(result.getCommandResult()));
      switch (result.getCommandResult()) {
        case CommandHandlingOutput.NOT_PERMITTED:
          commandCaller.message("Command Error: You are not allowed to execute that command!");
          break;
        case CommandHandlingOutput.ERROR:
          commandCaller.message("Something went wrong when executing the command!");
          result.getStacktrace().printStackTrace();
          break;
        case CommandHandlingOutput.NOT_FOUND:
          if (result.getClosestMatch() != null) {
            commandCaller
                .message("Did you mean: " + BotConfig.initialCharacter + result.getClosestMatch()
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
              if (member.getUser().getIdLong() == PlotBot.getInstance().getJda().getSelfUser()
                  .getIdLong()) {
                if (event.getMessage().getContentRaw().contains("who") && event.getMessage()
                    .getContentStripped().contains("daddy")) {
                  commandCaller.message("Citymonstret is my daddy ;)");
                } else if (
                    (event.getMessage().getContentRaw().toLowerCase().contains("thoughts") || event
                        .getMessage().getContentRaw().toLowerCase().contains("think")) &&
                        (event.getMessage().getContentRaw().toLowerCase().contains("about") || event
                            .getMessage().getContentRaw().toLowerCase().contains("on")) &&
                        event.getMessage().getContentRaw().toLowerCase().contains("minecraft")) {
                  event.getMessage().getChannel().sendMessageFormat("I prefer our craft %s",
                      event.getGuild().getEmotesByName("hammer_and_sickle", false).get(0)).queue();
                } else if (event.getMessage().getContentRaw().contains("how many") && event
                    .getMessage().getContentRaw().contains("members")) {
                  sendInfoMsg(event.getGuild().getMembers().size(),
                      event.getGuild());
                } else if (event.getMessage().getContentRaw().contains("How do I make sense?")) {
                  commandCaller.message(
                      "Mix three parts logic and one part knowledge. Stir it until a it's a homogeneous mixture."
                          + " Put in oven at 200 degrees Celsius for 30 minutes. Put in fridge until it's cool to touch.");
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
          final Optional<TextPrompt> textPrompt = textPromptManager.getPrompt(result.getInput());
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
}
