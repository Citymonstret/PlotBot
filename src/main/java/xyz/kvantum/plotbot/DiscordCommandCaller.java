package xyz.kvantum.plotbot;

import com.intellectualsites.commands.Command;
import com.intellectualsites.commands.CommandManager;
import com.intellectualsites.commands.callers.CommandCaller;
import com.intellectualsites.commands.parser.Parserable;
import java.util.Collection;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

@RequiredArgsConstructor public class DiscordCommandCaller implements CommandCaller<Member>
{

	@Getter private final TextChannel channel;
	@Getter private final Message message;
	private final Member user;

	@Override public void message(@NonNull final String s)
	{
		channel.sendMessageFormat( "%s - %s", getSuperCaller(), s ).queue();
	}

	@Override public Member getSuperCaller()
	{
		return this.user;
	}

	@Override public boolean hasAttachment(@NonNull final String s)
	{
		return s.isEmpty() || user.hasPermission( Permission.ADMINISTRATOR );
	}

	@Override public void sendRequiredArgumentsList(CommandManager commandManager, Command command,
			Collection<Parserable> collection, String s)
	{
		final StringBuilder message = new StringBuilder( "Required arguments: [");
		for ( final Parserable parserable : collection )
		{
			message.append( " { " ).append( parserable.getName() ).append( " - " ).append( parserable.getDesc() ).append( " } " );
		}
		message.append( "]" );
		message( message.toString() );
	}
}
