package xyz.kvantum.plotbot.commands;

import com.intellectualsites.commands.Command;
import com.intellectualsites.commands.CommandDeclaration;
import com.intellectualsites.commands.CommandInstance;
import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import org.kohsuke.github.GHIssue;
import xyz.kvantum.plotbot.BotConfig;
import xyz.kvantum.plotbot.BotConfig.CmdIssue;
import xyz.kvantum.plotbot.DiscordCommandCaller;
import xyz.kvantum.plotbot.github.GithubManager;

@CommandDeclaration(command = "issue", usage = "issue [id]", description = "Get a github issue") @RequiredArgsConstructor public class Issue
		extends Command
{

	private final GithubManager githubManager;
	private final SimpleDateFormat dateFormat = new SimpleDateFormat( "dd/MM/yy" );

	@Override public boolean onCommand(CommandInstance instance)
	{
		if ( instance.getArguments().length == 0 )
		{
			instance.getCaller().message( "The issue tracker can be found at: " + BotConfig.Github.issueLink );
		} else
		{
			final String argument = instance.getArguments()[ 0 ];
			try
			{
				final int issueId = Integer.parseInt( argument );
				final DiscordCommandCaller discordCommandCaller = ( DiscordCommandCaller ) instance.getCaller();
				final TextChannel channel = discordCommandCaller.getChannel();
				final Optional<GHIssue> issue = githubManager.getIssue( issueId );
				if ( !issue.isPresent() )
				{
					instance.getCaller().message( "Invalid issue ID: " + issueId );
					return true;
				}
				final EmbedBuilder embedBuilder = new EmbedBuilder();
				embedBuilder.setTitle( "Issue #" + issue.get().getId(), issue.get().getHtmlUrl().toString() );
				embedBuilder.addField( "Title", issue.get().getTitle(), true );
				embedBuilder.addField( "Opened", dateFormat.format( issue.get().getCreatedAt() ), true );
				embedBuilder.addField( "Author", issue.get().getUser().getName(), true );
				embedBuilder.addField( "Status", issue.get().getState().name(), true );
				String content = issue.get().getBody();
				if ( content.length() >= 150 )
				{
					content = content.substring( 0, 150 ) + "...";
				}
				embedBuilder.addField( "Body", content, false );
				embedBuilder.setColor( Color.ORANGE );
				final MessageEmbed messageEmbed = embedBuilder.build();
				// channel.sendMessage( messageEmbed ).queue();
				channel.getGuild().getTextChannelsByName( CmdIssue.channel, true ).get( 0 ).sendMessage( messageEmbed ).queue();
				discordCommandCaller.getMessage().delete().queue();
			} catch ( final Exception e )
			{
				instance.getCaller().message( "Invalid issue ID: " + argument );
			}
		}
		return true;
	}
}
