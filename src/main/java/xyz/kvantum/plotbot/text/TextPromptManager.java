package xyz.kvantum.plotbot.text;

import xyz.kvantum.plotbot.text.prompts.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;

public final class TextPromptManager {

    private static final Collection<TextPrompt> textPrompts = new HashSet<>();

    public TextPromptManager() {
        textPrompts.add(new GoodBot());
        textPrompts.add(new PlotMe());
        textPrompts.add(new BadBot());
        textPrompts.add(new PlotSquaredSucks());
        textPrompts.add(new WhatIsLove());
    }

    public Optional<TextPrompt> getPrompt(final String inputText) {
        final String lowerCase = inputText.toLowerCase(Locale.ENGLISH);
        for (final TextPrompt prompt : textPrompts) {
            if (lowerCase.contains(prompt.getPromptText().toLowerCase(Locale.ENGLISH))) {
                return Optional.of(prompt);
            }
        }
        return Optional.empty();
    }

}
