package xyz.kvantum.plotbot.commands.spigot;

import com.intellectualsites.commands.Command;
import com.intellectualsites.commands.CommandDeclaration;
import com.intellectualsites.commands.CommandInstance;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

@CommandDeclaration(command = "spigot", description = "Get spigot stats") public class Spigot
    extends Command {

    private final OkHttpClient client = new OkHttpClient();

    private Collection<MessageEmbed> cachedEmbeds = new ArrayList<>();
    private long cachedEmbedTime;

    private static final Collection<Resource> RESOURCES = new HashSet<>();

    private static void compileResources() {
        if (!RESOURCES.isEmpty()) {
            return;
        }
        final String[] ids = BotConfig.CmdSpigot.resourceId.split("\\|");
        if (ids.length < 1) {
            return;
        }
        final String[] names = BotConfig.CmdSpigot.resourceName.split("\\|");
        final String[] urls = BotConfig.CmdSpigot.url.split("\\|");
        if (names.length != ids.length || urls.length != ids.length) {
            new IllegalStateException("Mis-matched resource part lengths, expecting " + ids.length).printStackTrace();
            return;
        }
        for (int i = 0; i < ids.length; i++) {
            RESOURCES.add(new Resource(ids[i], names[i], urls[i]));
        }
    }

    @ToString
    @EqualsAndHashCode
    @RequiredArgsConstructor
    private static final class Resource {
        private final String resourceId;
        private final String resourceName;
        private final String resourceUrl;
    }

    @Override public boolean onCommand(@NonNull final CommandInstance instance) {
        final DiscordCommandCaller discordCommandCaller =
            (DiscordCommandCaller) instance.getCaller();
        final TextChannel channel = discordCommandCaller.getChannel();
        if (cachedEmbeds.isEmpty()) {
            if (System.currentTimeMillis() - cachedEmbedTime >= BotConfig.CmdSpigot.cacheTime) {
                cachedEmbeds.clear();
            } else {
                // channel.sendMessage(cachedEmbed).queue();
                cachedEmbeds.forEach(embed -> channel.sendMessage(embed).queue());
                return true;
            }
        }
        compileResources();
        if (RESOURCES.isEmpty()) {
            discordCommandCaller.message("There are no resources configured. Contact an administrator");
        } else {
            for (final Resource resource : RESOURCES) {
                channel.sendTyping().queue();
                final Request request = new Request.Builder()
                    .url("https://api.spiget.org/v2/resources/" + resource.resourceId).get().build();
                try {
                    final Response response = client.newCall(request).execute();
                    final String jsonString = response.body().string();
                    final JSONObject object = new JSONObject(jsonString);
                    final EmbedBuilder embedBuilder = new EmbedBuilder();
                    embedBuilder.setTitle("Spigot Status", resource.resourceUrl);
                    embedBuilder.addField("Resource: ", resource.resourceName, true);
                    final JSONObject rating = object.getJSONObject("rating");
                    embedBuilder.addField("Downloads", object.getInt("downloads") + "", true);
                    embedBuilder.addField("Rating", rating.getDouble("average") + "", true);
                    final MessageEmbed messageEmbed = embedBuilder.build();
                    this.cachedEmbedTime = System.currentTimeMillis();
                    this.cachedEmbeds.add(messageEmbed);
                    channel.sendMessage(messageEmbed).queue();
                } catch (final IOException e) {
                    channel.sendMessage("Something went wrong when reading the spigot information...")
                        .queue();
                    e.printStackTrace();
                }
            }
        }
        return true;
    }
}
