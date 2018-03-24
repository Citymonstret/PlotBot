package xyz.kvantum.plotbot;

import com.intellectualsites.commands.Command;
import com.intellectualsites.commands.CommandManager;
import com.intellectualsites.commands.callers.CommandCaller;
import com.intellectualsites.commands.parser.Parserable;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.Collection;

@RequiredArgsConstructor
public class DiscordCommandCaller implements CommandCaller<Member>
{

    @Getter
    private final TextChannel channel;
    private final Member user;

    @Override
    public void message(@NonNull final String s)
    {
        channel.sendMessageFormat( "%s - %s", getSuperCaller(), s ).queue();
    }

    @Override
    public Member getSuperCaller()
    {
        return this.user;
    }

    @Override
    public boolean hasAttachment(String s)
    {
        // TODO!
        return true;
    }

    @Override
    public void sendRequiredArgumentsList(CommandManager commandManager, Command command, Collection<Parserable> collection, String s)
    {
        // TODO!
    }
}
