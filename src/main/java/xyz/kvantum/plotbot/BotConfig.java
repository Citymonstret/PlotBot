package xyz.kvantum.plotbot;

import com.intellectualsites.configurable.ConfigurationImplementation;
import com.intellectualsites.configurable.annotations.ConfigSection;
import com.intellectualsites.configurable.annotations.Configuration;

@Configuration(implementation = ConfigurationImplementation.JSON) public class BotConfig {

    public static String token = "token";
    public static String initialCharacter = "!";
    public static String listeningTo = "messages | !help";

    @ConfigSection public static class Yandex {
        public static String apiToken = "";
    }

    @ConfigSection public static class Github {
        public static String issueLink = "http://google.com";
        public static String organization = "organization";
        public static String repository = "repository";
    }

    @ConfigSection public static class CmdSpigot {
        public static boolean enable = true;
        public static String resourceId = "1177|13932";
        public static long cacheTime = 60000;
        public static String resourceName = "PlotSquared|FastAsyncWorldEdit";
        public static String url = "https://www.spigotmc.org/resources/plotsquared.1177/|https://www.spigotmc.org/resources/fast-async-worldedit-voxelsniper.13932/";
    }

    @ConfigSection public static class Guild {
        public static String trustedRank = "Trusted";
        public static String announcementChannel = "off-topic";
    }

    @ConfigSection public static class AutoRank {
        public static String message = "The message has not been configured.";
        public static String emojiOne = "airplane";
        public static String rankOne = "rank1";
        public static String emojiTwo = "dog";
        public static String rankTwo = "rank2";
    }

    @ConfigSection public static class CmdIssue {
        public static boolean enable = true;
        public static String permission = "everyone";
        public static String channel = "bot-channel";
    }

}
