package xyz.kvantum.plotbot.commands;

import com.intellectualsites.commands.Command;
import com.intellectualsites.commands.CommandDeclaration;
import com.intellectualsites.commands.CommandInstance;
import com.intellectualsites.commands.parser.impl.StringParser;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Member;
import xyz.kvantum.plotbot.DiscordCommandCaller;
import xyz.kvantum.plotbot.PlotBot;

@CommandDeclaration(
		command = "history",
		usage = "!history [user] [limit=5]",
		description = "See a history of user messages"
)
public class MessageHistory extends Command
{

	public MessageHistory()
	{
		withArgument( "user", new StringParser(), "User" );
	}

	@Override public boolean onCommand(CommandInstance instance)
	{
		final DiscordCommandCaller discordCommandCaller = (DiscordCommandCaller) instance.getCaller();
		final Member historyUser = discordCommandCaller.getMessage().getMentionedMembers().get( 0 );

		final int messageAmount;
		if ( instance.getArguments().length > 1 )
		{
			messageAmount = Integer.parseInt( instance.getArguments()[1] );
		} else
		{
			messageAmount = 5;
		}

		final long channelId;
		final String channelName;
		if ( instance.getArguments().length > 2 )
		{
			final Channel tempChannel = discordCommandCaller.getChannel().getGuild().getTextChannelsByName( instance.getArguments()[2], true ).get( 0 );
			channelId = tempChannel.getIdLong();
			channelName = tempChannel.getName();
		} else
		{
			channelId = discordCommandCaller.getChannel().getIdLong();
			channelName = discordCommandCaller.getChannel().getName();
		}

		discordCommandCaller.getChannel().sendTyping().queue();
		final MessageBuilder messageBuilder = new MessageBuilder( "Last " ).append( messageAmount ).append( " messages from " )
				.append( historyUser ).append( " in #" ).append( channelName );
		for ( final String message : PlotBot.getInstance().getHistoryManager().getMessages( historyUser.getUser().getIdLong(), channelId, messageAmount ) )
		{
			messageBuilder.appendFormat( "\n- %s", message );
		}
		discordCommandCaller.getChannel().sendMessage( messageBuilder.build() ).queue();

		return true;
	}
}
