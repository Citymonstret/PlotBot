package xyz.kvantum.plotbot.listener;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.intellectualsites.commands.CommandHandlingOutput;
import com.intellectualsites.commands.CommandResult;
import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.var;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.representer.Representer;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import xyz.kvantum.plotbot.Annoyer;
import xyz.kvantum.plotbot.BotCommandManager;
import xyz.kvantum.plotbot.BotConfig;
import xyz.kvantum.plotbot.BotConfig.AutoRank;
import xyz.kvantum.plotbot.BotConfig.Guild;
import xyz.kvantum.plotbot.DiscordCommandCaller;
import xyz.kvantum.plotbot.IncendoPasteManager;
import xyz.kvantum.plotbot.PlotBot;
import xyz.kvantum.plotbot.commands.Link;
import xyz.kvantum.plotbot.commands.Macro;
import xyz.kvantum.plotbot.configuration.ConfigurationSection;
import xyz.kvantum.plotbot.configuration.InvalidConfigurationException;
import xyz.kvantum.plotbot.configuration.file.YamlConfiguration;
import xyz.kvantum.plotbot.configuration.file.YamlConstructor;
import xyz.kvantum.plotbot.configuration.file.YamlRepresenter;
import xyz.kvantum.plotbot.text.TextPrompt;
import xyz.kvantum.plotbot.text.TextPromptManager;

@RequiredArgsConstructor
public class Listener extends ListenerAdapter {

  private static final Pattern CALL_ME_PATTERN =
      Pattern.compile("call me (?<name>[A-Za-z0-9_]+)");
  private static final Pattern INCENDO_PASTE_LINK_PATTERN = Pattern.compile("https://incendo\\.org/paste/view/(?<paste>[A-Za-z0-9]+)");

  private final Retrofit incendoRetrofit = new Retrofit.Builder().baseUrl("https://incendo.org/").build();
  private final IncendoPasteManager incendoPasteManager = incendoRetrofit.create(IncendoPasteManager.class);
  private final DumperOptions yamlOptions = new DumperOptions();
  private final Representer yamlRepresenter = new YamlRepresenter();
  private final Yaml yaml = new Yaml(new YamlConstructor(), yamlRepresenter, yamlOptions);
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

  /*
  @Override
  public void onGuildMemberJoin(final GuildMemberJoinEvent event) {
    final int size = event.getGuild().getMembers().size();
    event.getGuild().getTextChannelsByName(Guild.announcementChannel, true)
        .get(0).sendMessage(String.format("Let's party like it's %s. Welcome %s!", size,
        event.getMember().getEffectiveName())).queue();
    sendInfoMsg(size, event.getGuild(), null);
  }
  */

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
      @NonNull final net.dv8tion.jda.core.entities.Guild guild, final TextChannel channel) {
    final int nextThousand = (int) (Math.floor(size / 1000f) + 1) * 1000;
    final int left = nextThousand - size;
    TextChannel channelToSendTo = (channel == null ? guild.getTextChannelsByName(Guild.announcementChannel, true).get(0) : channel);
    channelToSendTo.sendMessage(
        String.format("%d! Only %d members left until we've reached %d.", size, left, nextThousand)).queue();
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

          final Matcher incendoPasteMatcher = INCENDO_PASTE_LINK_PATTERN.matcher(event.getMessage().getContentRaw());
          if (incendoPasteMatcher.matches()) {
            final String pasteId = incendoPasteMatcher.group("paste");
            if (pasteId != null && !pasteId.isEmpty()) {
              event.getChannel().sendMessageFormat("Analyzing paste...").queue(msg -> {
                final Call<ResponseBody> responseBodyCall = incendoPasteManager.getPaste(pasteId, true);
                String newMessage = "";
                boolean hasPlotSquared4 = false;
                boolean hasMinecraft1_1_13 = false;
                boolean hasFawe = false;
                try {
                  final Response<ResponseBody> response = responseBodyCall.execute();
                  if (!response.isSuccessful()) {
                    newMessage = "Could not get response...";
                  } else {
                    final JsonObject jsonObject = new JsonParser().parse(response.body().string()).getAsJsonObject();
                    if (jsonObject == null) {
                      newMessage = "Failed to parse JSON";
                    } else {
                      String applicationId;
                      if (!jsonObject.has("application_id") ||
                          (!(applicationId = jsonObject.get("application_id").getAsString()).equals("plotsquared")) && !applicationId.equals("fastasyncworldedit")) {
                        newMessage = "Unknown application ID";
                      } else {
                        JsonArray fileNames;
                        if (!jsonObject.has("file_names") || (fileNames = jsonObject.get("file_names").getAsJsonArray()).size() == 0) {
                          newMessage = "No known files";
                        } else {
                          if (fileNames.contains(new JsonPrimitive("information"))) {
                            final String settingsContent = jsonObject.get("files").getAsJsonObject()
                                .get("information").getAsString().replaceAll("([.A-Za-z0-9]+): ([().A-Za-z0-9\\-_ :/]+)\n", "$1: '$2'\n")
                                .replace("server.version", "version.server")
                                .replace("server.plugins", "plugins")
                                .replaceAll(" - ([A-Za-z0-9\\-_]+): ([A-z0-9_\\- .]*)", "\n  $1:\n    version: '$2'");
                            final YamlConfiguration configuration = new YamlConfiguration();
                            try {
                              configuration.loadFromString(settingsContent);
                              final ConfigurationSection section = configuration.getConfigurationSection("plugins");
                              final String minecraftVersion = configuration.getString("version.server", "unknown");
                              final StringBuilder messageBuilder = new StringBuilder("Analysed Paste **>** Minecraft Version: ")
                                  .append(minecraftVersion);
                              hasMinecraft1_1_13 = minecraftVersion.contains("1.13.");
                              if (section.contains("PlotSquared")) {
                                final String version = section.getConfigurationSection("PlotSquared").getString("version").replace("'", "");
                                messageBuilder.append(" **|** PlotSquared Version: ")
                                    .append(version);
                                hasPlotSquared4 = version.startsWith("4.");
                              }
                              if (section.contains("FastAsyncWorldEdit")) {
                                messageBuilder.append(" **|** FAWE Version: ")
                                    .append(section.getConfigurationSection("FastAsyncWorldEdit").getString("version").replace("'", ""));
                                hasFawe = true;
                              }
                              if (section.contains("WorldEdit")) {
                                messageBuilder.append(" **|** WorldEdit Version: ")
                                    .append(section.getConfigurationSection("WorldEdit").getString("version").replace("'", ""));
                              }
                              newMessage = messageBuilder.toString();
                            } catch (InvalidConfigurationException e) {
                              e.printStackTrace();
                              newMessage = String
                                  .format("Failed to parse YAML: %s\n%s", e.getMessage(),
                                      settingsContent);
                            }
                          }
                        }
                      }
                    }
                  }
                } catch (final IOException e) {
                  e.printStackTrace();
                  newMessage = String.format("Failed to read paste: %s", e.getMessage());
                }
                msg.editMessageFormat(newMessage).complete();
                if (hasMinecraft1_1_13 && !hasPlotSquared4) {
                  event.getChannel().sendMessageFormat("You're using Minecraft 1.13 with legacy PlotSquared. Update to PlotSquared 4!")
                      .complete();
                }
                if (hasPlotSquared4 && hasFawe) {
                  event.getChannel().sendMessageFormat("You cannot use FAWE and PlotSquared 4 together, as of now. Please uninstall FAWE.")
                      .complete();
                }
              });
              break;
            }
          }

          if (event.getMessage().getContentRaw().startsWith(".")) {
            // This is a link
            String msg = event.getMessage().getContentRaw();
            if (msg.length() > 1 && !(msg = msg.substring(1)).isEmpty()) {
              String[] args = msg.split(" ");
              args[0] = "!" + args[0];
              Link.getInstance().onCommand(commandCaller, args, new HashMap<>());
            }
            break;
          }

          if (event.getMessage().getContentRaw().startsWith("*")) {
            // This is a macro
            String msg = event.getMessage().getContentRaw();
            if (msg.length() > 1 && !(msg = msg.substring(1)).isEmpty()) {
              String[] args = msg.split(" ");
              args[0] = "!" + args[0];
              Macro.getInstance().onCommand(commandCaller, args, new HashMap<>());
            }
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
                      event.getGuild(), event.getMessage().getTextChannel());
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
