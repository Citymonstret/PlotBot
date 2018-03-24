package xyz.kvantum.plotbot.commands.spigot;

import com.intellectualsites.commands.Command;
import com.intellectualsites.commands.CommandDeclaration;
import com.intellectualsites.commands.CommandInstance;
import lombok.NonNull;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;
import xyz.kvantum.plotbot.BotConfig;
import xyz.kvantum.plotbot.DiscordCommandCaller;

import java.io.IOException;

@CommandDeclaration(
        command = "spigot"
)
public class Spigot extends Command
{

    private final OkHttpClient client = new OkHttpClient();

    private MessageEmbed cachedEmbed;
    private long cachedEmbedTime;

    @Override
    public boolean onCommand(@NonNull final CommandInstance instance)
    {
        final DiscordCommandCaller discordCommandCaller = (DiscordCommandCaller) instance.getCaller();
        final TextChannel channel = discordCommandCaller.getChannel();
        channel.sendTyping();
        if ( cachedEmbed != null )
        {
            if ( System.currentTimeMillis() - cachedEmbedTime >= BotConfig.CmdSpigot.cacheTime )
            {
                cachedEmbed = null;
            } else
            {
                channel.sendMessage( cachedEmbed ).queue();
                return true;
            }
        }
        final Request request = new Request.Builder().url( "https://api.spiget.org/v2/resources/" + BotConfig
                .CmdSpigot.resourceId ).get().build();
        try
        {
            final Response response = client.newCall( request ).execute();
            final String jsonString = response.body().string();
            final JSONObject object = new JSONObject( jsonString );
            final EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle( "Spigot Status", BotConfig.CmdSpigot.url );
            final JSONObject rating = object.getJSONObject( "rating" );
            embedBuilder.addField( "Downloads", object.getInt( "downloads" ) + "", true );
            embedBuilder.addField( "Rating", rating.getDouble( "average" ) + "" , true );
            final MessageEmbed messageEmbed = embedBuilder.build();
            this.cachedEmbedTime = System.currentTimeMillis();
            this.cachedEmbed = messageEmbed;
            channel.sendMessage( messageEmbed ).queue();
        } catch ( final IOException e )
        {
            channel.sendMessage( "Something went wrong when reading the spigot information..." ).queue();
            e.printStackTrace();
        }
        return true;
    }
}
