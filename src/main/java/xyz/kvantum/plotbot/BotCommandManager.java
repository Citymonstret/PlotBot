package xyz.kvantum.plotbot;

import com.intellectualsites.commands.CommandManager;
import xyz.kvantum.plotbot.commands.Issue;
import xyz.kvantum.plotbot.commands.spigot.Spigot;
import xyz.kvantum.plotbot.github.GithubManager;

public class BotCommandManager extends CommandManager
{

    BotCommandManager(final GithubManager githubManager)
    {
        if ( BotConfig.CmdIssue.enable )
        {
            this.createCommand( new Issue(githubManager) );
        }
        if ( BotConfig.CmdSpigot.enable )
        {
            this.createCommand( new Spigot() );
        }
        this.setInitialCharacter( BotConfig.initialCharacter.charAt( 0 ) );
    }

}
