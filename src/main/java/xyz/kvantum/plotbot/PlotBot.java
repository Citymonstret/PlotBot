package xyz.kvantum.plotbot;

import com.intellectualsites.configurable.ConfigurationFactory;
import java.io.File;
import javax.security.auth.login.LoginException;
import lombok.Getter;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.kvantum.plotbot.github.GithubManager;
import xyz.kvantum.plotbot.listener.Listener;

public class PlotBot
{

	@Getter
	private static PlotBot instance;

	private final Logger logger;
	@Getter
	private final JDA jda;
	@Getter
	private final BotCommandManager commandManager;
	private final GithubManager githubManager;
	private final SQLiteManager sqLiteManager;
	@Getter
	private final HistoryManager historyManager;

	private PlotBot()
	{
		instance = this;

		this.logger = LoggerFactory.getLogger( "PlotBot" );
		this.githubManager = new GithubManager( logger );
		this.commandManager = new BotCommandManager( githubManager );
		this.sqLiteManager = new SQLiteManager();
		this.historyManager = new HistoryManager( this.sqLiteManager );

		ConfigurationFactory.load( BotConfig.class, new File( "./" ) );

		this.githubManager.connect();

		// To avoid having the compiler screaming at us
		JDA temporary = null;
		try
		{
			temporary = new JDABuilder( AccountType.BOT ).setToken( BotConfig.token )
					.addEventListener( new Listener( this.commandManager, logger ) ).buildAsync();
		} catch ( final LoginException e )
		{
			this.logger.error( "Failed to create JDA instance :(", e );
			System.exit( -1 );
		}
		this.jda = temporary;
	}

	public static void main(final String[] args)
	{
		new PlotBot();
	}

}