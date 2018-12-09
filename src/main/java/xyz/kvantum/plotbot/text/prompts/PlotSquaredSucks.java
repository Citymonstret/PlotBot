package xyz.kvantum.plotbot.text.prompts;

import xyz.kvantum.plotbot.DiscordCommandCaller;
import xyz.kvantum.plotbot.text.TextPrompt;

public class PlotSquaredSucks extends TextPrompt {

    public PlotSquaredSucks() {
        super("plotsquared sucks");
    }

    @Override public void handle(DiscordCommandCaller caller) {
        caller.message("no u");
    }
}
