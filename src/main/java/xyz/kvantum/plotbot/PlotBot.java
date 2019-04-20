package xyz.kvantum.plotbot;

import com.intellectualsites.configurable.ConfigurationFactory;
import lombok.Getter;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.kvantum.plotbot.github.GithubManager;
import xyz.kvantum.plotbot.listener.Listener;

import javax.security.auth.login.LoginException;
import java.io.File;

public class PlotBot {

    @Getter private static PlotBot instance;
    @Getter private final Logger logger;
    @Getter private final JDA jda;
    @Getter private final BotCommandManager commandManager;
    private final GithubManager githubManager;
    private final SQLiteManager sqLiteManager;
    @Getter private final HistoryManager historyManager;
    @Getter private Listener listener;

    private PlotBot() {
        instance = this;

        this.logger = LoggerFactory.getLogger("PlotBot");
        this.githubManager = new GithubManager(logger);
        this.sqLiteManager = new SQLiteManager();
        this.historyManager = new HistoryManager(this.sqLiteManager);

        ConfigurationFactory.load(BotConfig.class, new File("./"));

        this.commandManager = new BotCommandManager(githubManager);

        this.logger.info("Connecting to Github...");

        // this.githubManager.connect();

        this.logger.info("Connecting to Discord...");

        // To avoid having the compiler screaming at us
        JDA temporary = null;
        try {
            temporary = new JDABuilder(AccountType.BOT).setToken(BotConfig.token)
                .setStatus(OnlineStatus.DO_NOT_DISTURB)
                .setGame(Game.listening(BotConfig.listeningTo))
                .addEventListener((this.listener = new Listener(this.commandManager, logger))).build().awaitReady();
        } catch (final LoginException | InterruptedException e) {
            this.logger.error("Failed to create JDA instance :(", e);
            System.exit(-1);
        }
        this.jda = temporary;
    }

    public static void main(final String[] args) {
        new PlotBot();
    }

}
