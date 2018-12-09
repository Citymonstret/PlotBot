package xyz.kvantum.plotbot;

import com.intellectualsites.configurable.ConfigurationImplementation;
import com.intellectualsites.configurable.annotations.ConfigSection;
import com.intellectualsites.configurable.annotations.Configuration;

@Configuration(implementation = ConfigurationImplementation.JSON) public class BotConfig {

    public static String token = "token";
    public static String initialCharacter = "!";


    @ConfigSection public static class Github {
        public static String issueLink = "http://google.com";
        public static String organization = "organization";
        public static String repository = "repository";
    }


    @ConfigSection public static class CmdSpigot {
        public static boolean enable = true;
        public static int resourceId = -1;
        public static long cacheTime = 60000;
        public static String resourceName = "Your Resource Name";
        public static String url = "http://google.com";
    }


    @ConfigSection public static class Guild {
        public static String trustedRank = "@trusted";
    }


    @ConfigSection public static class CmdIssue {
        public static boolean enable = true;
        public static String permission = "everyone";
        public static String channel = "bot-channel";
    }

}
