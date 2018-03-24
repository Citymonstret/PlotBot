package xyz.kvantum.plotbot;

import com.intellectualsites.configurable.ConfigurationFactory;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.kvantum.plotbot.github.GithubManager;
import xyz.kvantum.plotbot.listener.Listener;

import javax.security.auth.login.LoginException;
import java.io.File;

public class PlotBot
{

    private final Logger logger;
    private final JDA jda;
    private final BotCommandManager commandManager;
    private final GithubManager githubManager;

    public static void main(final String[] args)
    {
        new PlotBot();
    }

    private PlotBot()
    {
        this.logger = LoggerFactory.getLogger( "PlotBot" );
        this.githubManager = new GithubManager( logger );
        this.commandManager = new BotCommandManager( githubManager );
        ConfigurationFactory.load( BotConfig.class, new File("./"));

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

}
