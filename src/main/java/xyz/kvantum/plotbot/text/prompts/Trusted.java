package xyz.kvantum.plotbot.text.prompts;

import xyz.kvantum.plotbot.DiscordCommandCaller;
import xyz.kvantum.plotbot.text.TextPrompt;

public class Trusted extends TextPrompt {

  public Trusted() {
    super("trusted");
  }

  @Override
  public void handle(final DiscordCommandCaller caller) {
    caller.getMessage().delete().complete();
  }
}
