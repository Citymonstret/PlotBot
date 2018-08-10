package xyz.kvantum.plotbot.commands;

import com.intellectualsites.commands.Command;
import com.intellectualsites.commands.CommandDeclaration;
import com.intellectualsites.commands.CommandInstance;
import com.intellectualsites.commands.parser.impl.StringParser;
import java.util.List;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import xyz.kvantum.plotbot.BotConfig.Guild;
import xyz.kvantum.plotbot.DiscordCommandCaller;

@CommandDeclaration(
		command = "trust",
		usage = "!nick [newnick]",
		permission = "trust",
		description = "Show that you trust someone :)"
)
public class Trust extends Command
{

	public Trust()
	{
		withArgument( "trusted", new StringParser(), "Member to trust!" );
	}

	@Override public boolean onCommand(CommandInstance instance)
	{
		final DiscordCommandCaller discordCommandCaller = (DiscordCommandCaller) instance.getCaller();
		final Member member = discordCommandCaller.getMessage().getMentionedMembers().get( 0 );
		if ( member == null )
		{
			discordCommandCaller.message( "There's no user with ID " + instance.getString( "trusted" ) );
		} else
		{
			final List<Role> roles = discordCommandCaller.getChannel().getGuild().getRolesByName( Guild.trustedRank, true );
			if ( roles.isEmpty() )
			{
				discordCommandCaller.message( "Misconfigured trusted rank!" );
			} else
			{
				discordCommandCaller.getMessage().delete().complete();
				member.getGuild().getController().addRolesToMember( member, roles ).complete();
				discordCommandCaller.getChannel().sendMessageFormat( "Congratulations, %s, someone trusts you!", member ).queue();
			}
		}
		return true;
	}
}
