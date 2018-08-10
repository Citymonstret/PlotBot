package xyz.kvantum.plotbot.text;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import xyz.kvantum.plotbot.DiscordCommandCaller;

@EqualsAndHashCode @RequiredArgsConstructor public abstract class TextPrompt
{

	@Getter private final String promptText;

	public abstract void handle(DiscordCommandCaller caller);

}
