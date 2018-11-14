package xyz.kvantum.plotbot.text.prompts;

import xyz.kvantum.plotbot.DiscordCommandCaller;
import xyz.kvantum.plotbot.text.TextPrompt;

public class GoodBot extends TextPrompt
{

	private static final String[] MESSAGES = new String[] { "Thank you :smile:", "Keep it in your pants!",
			"Awww! :heart:", "Good human!" };

	public GoodBot()
	{
		super( "good bot" );
	}

	@Override public void handle(final DiscordCommandCaller caller)
	{
		caller.message( MESSAGES[ ( int ) ( Math.random() * ( MESSAGES.length - 1 ) ) ] );
	}
}
