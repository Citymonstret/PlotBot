package xyz.kvantum.plotbot.commands;

import com.intellectualsites.commands.Command;
import com.intellectualsites.commands.CommandDeclaration;
import com.intellectualsites.commands.CommandInstance;
import com.intellectualsites.commands.parser.impl.IntegerParser;
import com.intellectualsites.commands.parser.impl.StringParser;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;
import xyz.kvantum.plotbot.DiscordCommandCaller;
import xyz.kvantum.plotbot.PlotBot;

@CommandDeclaration(command = "repeat", usage = "repeat <num> <interval> <command>", permission = "repeat")
public class Repeat extends Command {

  public Repeat() {
    withArgument("num", new IntegerParser(1, 1000), "Number of times to repeat the command");
    withArgument("interval", new IntegerParser(1, 3600), "How often it should run");
    withArgument("cmd", new StringParser(), "Command to execute (use % for spaces)");
  }

  @Override
  public boolean onCommand(final CommandInstance instance) {
    final DiscordCommandCaller discordCommandCaller = (DiscordCommandCaller) instance.getCaller();
    final String cmd = instance.getString("cmd").replace("%", " ");

    if (cmd.contains("repeat")) {
      discordCommandCaller.message("Cannot nest repeat commands!");
      return true;
    }

    discordCommandCaller.getMessage().delete().queue();

    final int times = instance.getInteger("num");
    final AtomicInteger count = new AtomicInteger(0);
    new Timer().scheduleAtFixedRate(new TimerTask() {
      @Override
      public void run() {
        final int time = count.incrementAndGet();
        if (time > times) {
          this.cancel();
          return;
        }
        PlotBot.getInstance().getCommandManager().handle(discordCommandCaller, cmd.replace("$num$", String.valueOf(time)).replace("$numz$", String.valueOf(time - 1)));
      }
    }, 0, 1000 * instance.getInteger("interval"));

    return true;
  }
}
