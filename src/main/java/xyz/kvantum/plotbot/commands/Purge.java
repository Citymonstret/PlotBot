package xyz.kvantum.plotbot.commands;

import com.intellectualsites.commands.Command;
import com.intellectualsites.commands.CommandDeclaration;
import com.intellectualsites.commands.CommandInstance;
import com.intellectualsites.commands.parser.impl.IntegerParser;
import java.util.ArrayList;
import java.util.Collection;
import net.dv8tion.jda.core.entities.Message;
import xyz.kvantum.plotbot.DiscordCommandCaller;

@CommandDeclaration(command = "purge", usage = "!purge [amount]") public class Purge extends Command
{

	public Purge()
	{
		withArgument( "amount", new IntegerParser( 1, 100 ), "Amount of messages to remove" );
	}

	@Override public boolean onCommand(final CommandInstance instance)
	{
		final DiscordCommandCaller discordCommandCaller = ( DiscordCommandCaller ) instance.getCaller();

		final Collection<Message> toDelete = new ArrayList<>();
		{
			toDelete.addAll( discordCommandCaller.getChannel()
					.getHistoryBefore( discordCommandCaller.getMessage(), instance.getInteger( "amount" ) ).complete()
					.getRetrievedHistory() );
		}

		toDelete.add( discordCommandCaller.getMessage() );
		discordCommandCaller.getChannel().deleteMessages( toDelete ).queue();

		return true;
	}
}
