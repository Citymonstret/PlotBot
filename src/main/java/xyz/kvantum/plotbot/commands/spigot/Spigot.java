package xyz.kvantum.plotbot.commands.spigot;

import com.intellectualsites.commands.Command;
import com.intellectualsites.commands.CommandDeclaration;
import com.intellectualsites.commands.CommandInstance;
import java.awt.Color;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
import org.json.JSONArray;
import org.json.JSONObject;
import xyz.kvantum.plotbot.BotConfig;
import xyz.kvantum.plotbot.DiscordCommandCaller;

@CommandDeclaration(command = "spigot", description = "Get spigot stats", permission = "all", usage = "!spigot <resource>") public class Spigot
    extends Command {

    private static final HashMap var0 = new HashMap();
    private static final HashMap var1 = new HashMap();

    static {

        var0.put(1L, "Mon");
        var0.put(2L, "Tue");
        var0.put(3L, "Wed");
        var0.put(4L, "Thu");
        var0.put(5L, "Fri");
        var0.put(6L, "Sat");
        var0.put(7L, "Sun");
        var1.put(1L, "Jan");
        var1.put(2L, "Feb");
        var1.put(3L, "Mar");
        var1.put(4L, "Apr");
        var1.put(5L, "May");
        var1.put(6L, "Jun");
        var1.put(7L, "Jul");
        var1.put(8L, "Aug");
        var1.put(9L, "Sep");
        var1.put(10L, "Oct");
        var1.put(11L, "Nov");
        var1.put(12L, "Dec");
    }

    private final DateTimeFormatter dateTimeFormatter = (new DateTimeFormatterBuilder()).parseCaseInsensitive().parseLenient().optionalStart().appendText(ChronoField.DAY_OF_WEEK, var0).appendLiteral(", ").optionalEnd().appendValue(ChronoField.DAY_OF_MONTH, 1, 2, SignStyle.NOT_NEGATIVE).appendLiteral(' ').appendText(ChronoField.MONTH_OF_YEAR, var1).appendLiteral(' ').appendValue(ChronoField.YEAR, 4).appendLiteral(' ').appendValue(ChronoField.HOUR_OF_DAY, 2).appendLiteral(':').appendValue(ChronoField.MINUTE_OF_HOUR, 2).optionalStart().appendLiteral(':').appendValue(
        ChronoField.SECOND_OF_MINUTE, 2).optionalEnd().appendLiteral(' ').toFormatter();

    private static final Map<String, Color> SPECIAL_COLORS = new HashMap<>();
    static {
        SPECIAL_COLORS.put("PlotSquared", Color.ORANGE);
        SPECIAL_COLORS.put("FastAsyncWorldEdit", Color.RED);
    }

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
            final String[] args = instance.getArguments();
            if (args.length == 0) {
                final StringBuilder available = new StringBuilder();
                for (final Resource resource : RESOURCES) {
                    final String name = resource.resourceName.toLowerCase();
                    available.append(" (").append(name.charAt(0)).append(")").append(name.substring(1));
                }
                discordCommandCaller.message("!spigot <resource> | Available resources: (a)ll " + available);
                return true;
            }

            final ArrayList<Resource> resources = new ArrayList<>();

            if (args[0].equalsIgnoreCase("all") || args[0].equalsIgnoreCase("a")) {
                resources.addAll(RESOURCES);
            } else {
                for (final Resource resource : RESOURCES) {
                    final String name = resource.resourceName.toLowerCase();
                    if (name.charAt(0) == args[0].toLowerCase().charAt(0) || name.equalsIgnoreCase(args[0])) {
                        resources.add(resource);
                    }
                }
            }

            if (resources.isEmpty()) {
                discordCommandCaller.message("No such resource: " + args[0]);
                return true;
            }

            for (final Resource resource : resources) {
                channel.sendTyping().queue();
                final Request request = new Request.Builder()
                    .url("https://api.spiget.org/v2/resources/" + resource.resourceId).get().build();
                final Request update = new Request.Builder()
                    .url(String.format("https://api.spiget.org/v2/resources/%s/updates?sort=-date",
                        resource.resourceId)).get().build();
                try {
                    final Response response = client.newCall(request).execute();
                    final String jsonString = response.body().string();
                    final JSONObject object = new JSONObject(jsonString);

                    final Response updateResponse = client.newCall(update).execute();
                    final String updateJsonString = updateResponse.body().string();
                    final JSONObject updateObject = new JSONArray(updateJsonString).getJSONObject(0);

                    final EmbedBuilder embedBuilder = new EmbedBuilder();
                    if (SPECIAL_COLORS.containsKey(resource.resourceName)) {
                        embedBuilder.setColor(SPECIAL_COLORS.get(resource.resourceName));
                    }

                    embedBuilder.setTitle("Spigot Status", resource.resourceUrl);
                    embedBuilder.addField("Resource", resource.resourceName, true);
                    final JSONObject rating = object.getJSONObject("rating");
                    embedBuilder.addField("Downloads", object.getInt("downloads") + "", true);
                    embedBuilder.addField("Rating", rating.getDouble("average") + "", true);
                    embedBuilder.addField("Authors", object.getString("contributors"), false);
                    embedBuilder.addField("Tag", object.getString("tag"), false);
                    embedBuilder.addField("Download URL", String.format("https://www.spigotmc.org/%s", object.getJSONObject("file").getString("url")), true);
                    embedBuilder.addField("Last Update", updateObject.getString("title"), false);
                    embedBuilder.addField("Update Likes", updateObject.getInt("likes") + "", true);
                    final String iconUrl = String.format("https://www.spigotmc.org/%s", object.getJSONObject("icon").getString("url"));
                    embedBuilder.addField("Updated", dateTimeFormatter.format(LocalDateTime
                        .ofInstant(Instant.ofEpochSecond(updateObject.getLong("date")), ZoneId.systemDefault())), true);
                    embedBuilder.setFooter("Requested by " + discordCommandCaller.getMessage().getMember().getEffectiveName()
                        /*" • Today at " + LocalTime.now().format(this.dateTimeFormatter) */, iconUrl == null || iconUrl.isEmpty() ? discordCommandCaller.getMessage().getMember().getUser().getAvatarUrl() : iconUrl).setTimestamp(Instant.now());

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
