package xyz.kvantum.plotbot.commands;

import com.intellectualsites.commands.Command;
import com.intellectualsites.commands.CommandDeclaration;
import com.intellectualsites.commands.CommandInstance;
import com.intellectualsites.commands.parser.impl.StringParser;
import xyz.kvantum.plotbot.DiscordCommandCaller;

@CommandDeclaration(command = "nick", permission = "setnick", description = "Set plot nickname") public class Nick
		extends Command
{

	public Nick()
	{
		withArgument( "nick", new StringParser(), "New nick name" );
	}

	@Override public boolean onCommand(final CommandInstance instance)
	{
		final DiscordCommandCaller commandCaller = ( DiscordCommandCaller ) instance.getCaller();
		commandCaller.getSuperCaller().getGuild().getController()
				.setNickname( commandCaller.getSuperCaller().getGuild().getSelfMember(), instance.getString( "nick" ) )
				.complete();
		commandCaller.message( "I've been renamed!" );
		return true;
	}
}
