package xyz.kvantum.plotbot.text.prompts;

import xyz.kvantum.plotbot.DiscordCommandCaller;
import xyz.kvantum.plotbot.text.TextPrompt;

public class WhatIsLove extends TextPrompt {

    public WhatIsLove() {
        super("what is love");
    }

    @Override public void handle(final DiscordCommandCaller caller) {
        caller.message("a basic drive that evolved millions of years ago in order to enable us"
            + " and focus our attention on just one partner and start the mating process");
    }
}
