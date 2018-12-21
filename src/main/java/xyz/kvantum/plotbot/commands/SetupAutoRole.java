package xyz.kvantum.plotbot.commands;

import com.intellectualsites.commands.Command;
import com.intellectualsites.commands.CommandDeclaration;
import com.intellectualsites.commands.CommandInstance;
import lombok.var;
import net.dv8tion.jda.core.entities.Guild;
import xyz.kvantum.plotbot.BotConfig.AutoRank;
import xyz.kvantum.plotbot.DiscordCommandCaller;

@CommandDeclaration(command = "sar", permission = "sar")
public class SetupAutoRole extends Command  {

  @Override
  public boolean onCommand(CommandInstance instance) {
    final DiscordCommandCaller discordCommandCaller = (DiscordCommandCaller) instance.getCaller();
    discordCommandCaller.getChannel().sendMessageFormat("**Project Notification Roles**\n%s", AutoRank.message).queue(message -> {
      final Guild guild = discordCommandCaller.getChannel().getGuild();
      final var emote1 = guild.getEmotesByName(AutoRank.emojiOne, true);
      final var emote2 = guild.getEmotesByName(AutoRank.emojiTwo, true);
      if (emote1.isEmpty() || emote2.isEmpty()) {
        discordCommandCaller.message("Emojis are not configured correctly.");
      } else {
        message.addReaction(emote1.get(0)).complete();
        message.addReaction(emote2.get(0)).complete();
      }
    });
    return true;
  }
}
