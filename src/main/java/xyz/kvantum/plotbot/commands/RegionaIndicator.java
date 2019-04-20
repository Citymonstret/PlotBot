package xyz.kvantum.plotbot.commands;

import com.intellectualsites.commands.Command;
import com.intellectualsites.commands.CommandDeclaration;
import com.intellectualsites.commands.CommandInstance;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;
import net.dv8tion.jda.core.entities.Message;
import xyz.kvantum.plotbot.DiscordCommandCaller;

@CommandDeclaration(command = "ri", usage = "!ri <word>", permission = "ri",
    description = "Spell out stuff with regional indicator emotes")
public class RegionaIndicator extends Command {

  private static final Map<Character, String> NUMBERS = new HashMap<>();
  static {
    NUMBERS.put('0', ":zero:");
    NUMBERS.put('1', ":one:");
    NUMBERS.put('2', ":two:");
    NUMBERS.put('3', ":three:");
    NUMBERS.put('4', ":four:");
    NUMBERS.put('5', ":five:");
    NUMBERS.put('6', ":six:");
    NUMBERS.put('7', ":seven:");
    NUMBERS.put('8', ":eight:");
    NUMBERS.put('9', ":nine:");
  }

  private String joinArgs(final String[] args) {
    final StringBuilder builder = new StringBuilder();
    for (int i = 0; i < args.length; i++) {
      builder.append(args[i]);
      if ((i + 1) < args.length) {
        builder.append(" ");
      }
    }
    return builder.toString();
  }

  @Override
  public boolean onCommand(final CommandInstance instance) {
    final DiscordCommandCaller discordCommandCaller = (DiscordCommandCaller) instance.getCaller();
    final StringBuilder builder = new StringBuilder();
    for (final char c : joinArgs(instance.getArguments()).toLowerCase(Locale.ENGLISH).toCharArray()) {
      if (!Character.isAlphabetic(c)) {
        if (!Character.isDigit(c)) {
          if (c == '!') {
            builder.append(":exclamation: ");
          } else if (c == ' ') {
            builder.append(":stop_button: ");
          } else if (c == '?') {
            builder.append(":question: ");
          } else {
            discordCommandCaller.message("Unknown character '" + c + "'");
            return true;
          }
        } else {
          builder.append(NUMBERS.get(c)).append(" ");
        }
      } else {
        builder.append(":regional_indicator_").append(c).append(": ");
      }
    }
    final String message = builder.toString();
    final String[] parts = message.split(" ");
    final Timer timer = new Timer();
    final AtomicInteger integer = new AtomicInteger(0);
    final Message msg = discordCommandCaller.getChannel().sendMessageFormat(parts[integer.getAndIncrement()]).complete();
    if (parts.length > 1) {
      discordCommandCaller.getChannel().sendTyping().complete();
      timer.scheduleAtFixedRate(new TimerTask() {
        @Override
        public void run() {
          final StringBuilder newMessage = new StringBuilder();
          for (int i = 0; i <= integer.get(); i++) {
            newMessage.append(parts[i]).append(" ");
          }
          msg.editMessageFormat(newMessage.toString()).complete();
          if (integer.incrementAndGet() >= parts.length) {
            this.cancel();
          } else {
            discordCommandCaller.getChannel().sendTyping().complete();
          }
        }
      }, 1000L, 1500L);
    }
    discordCommandCaller.getMessage().delete().queue();
    // discordCommandCaller.getChannel().sendMessageFormat(builder.toString()).queue();
    return true;
  }
}
