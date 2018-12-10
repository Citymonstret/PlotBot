package xyz.kvantum.plotbot.text.prompts;

import xyz.kvantum.plotbot.DiscordCommandCaller;
import xyz.kvantum.plotbot.text.TextPrompt;

public class NotMyFaultIsRude extends TextPrompt {

    private static final String[] MESSAGES = { "I agree", "Everything is his fault", "Yes.", "He smells like onions",
        "His bot is lame", "Ban him plz", "I agree" };

    public NotMyFaultIsRude() {
        super("notmyfault is rude");
    }

    @Override public void handle(DiscordCommandCaller caller) {
        caller.message(MESSAGES[(int) (Math.random() * (MESSAGES.length - 1))]);
    }
}
