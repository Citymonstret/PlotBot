package xyz.kvantum.plotbot.text.prompts;

import xyz.kvantum.plotbot.DiscordCommandCaller;
import xyz.kvantum.plotbot.text.TextPrompt;

public final class PlotMe extends TextPrompt
{

	public PlotMe()
	{
		super( "plotme" );
	}

	@Override public void handle(DiscordCommandCaller caller)
	{
		// caller.getMessage().delete().reason( "Cursing" ).complete();
		caller.message( "Shhh! Don't curse!" );
	}
}
