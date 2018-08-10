package xyz.kvantum.plotbot.text;

import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;
import xyz.kvantum.plotbot.text.prompts.BadBot;
import xyz.kvantum.plotbot.text.prompts.GoodBot;
import xyz.kvantum.plotbot.text.prompts.PlotMe;

public final class TextPromptManager
{

	private static final Collection<TextPrompt> textPrompts = new HashSet<>();

	public TextPromptManager()
	{
		textPrompts.add( new GoodBot() );
		textPrompts.add( new PlotMe() );
		textPrompts.add( new BadBot() );
	}

	public Optional<TextPrompt> getPrompt(final String inputText)
	{
		final String lowerCase = inputText.toLowerCase( Locale.ENGLISH );
		for ( final TextPrompt prompt : textPrompts )
		{
			if ( lowerCase.contains( prompt.getPromptText().toLowerCase( Locale.ENGLISH ) ) )
			{
				return Optional.of( prompt );
			}
		}
		return Optional.empty();
	}

}
