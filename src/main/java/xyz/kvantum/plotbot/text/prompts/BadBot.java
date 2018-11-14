package xyz.kvantum.plotbot.text.prompts;

import xyz.kvantum.plotbot.DiscordCommandCaller;
import xyz.kvantum.plotbot.text.TextPrompt;

public class BadBot extends TextPrompt
{

	private static final String[] MESSAGES = new String[] { "Why you gotta be so rude?",
			"What have I ever done to you?", "You are in desperate need of a hug! :hugging:", "Now I feel hurt :cry:",
			"I know where your mailbox lives!" };

	public BadBot()
	{
		super( "bad bot" );
	}

	@Override public void handle(final DiscordCommandCaller caller)
	{
		caller.message( MESSAGES[ ( int ) ( Math.random() * MESSAGES.length ) - 1 ] );
	}

}
