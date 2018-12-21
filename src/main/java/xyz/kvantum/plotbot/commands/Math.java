package xyz.kvantum.plotbot.commands;

import com.intellectualsites.commands.Command;
import com.intellectualsites.commands.CommandDeclaration;
import com.intellectualsites.commands.CommandInstance;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import xyz.kvantum.plotbot.DiscordCommandCaller;
import xyz.kvantum.plotbot.MathManager;

import java.io.IOException;

@CommandDeclaration(description = "Do some calculation magic", usage = "!math [expression]", command = "math", permission = "all")
public class Math extends Command {

    private final MathManager mathManager;

    public Math() {
        this.mathManager = new Retrofit.Builder().baseUrl("http://api.mathjs.org/v4/").build()
            .create(MathManager.class);
    }

    @Override public boolean onCommand(CommandInstance instance) {
        final DiscordCommandCaller discordCommandCaller =
            (DiscordCommandCaller) instance.getCaller();
        discordCommandCaller.getChannel().sendTyping().queue();

        if (instance.getArguments().length == 0) {
            discordCommandCaller.message("You need to specify an expression!");
            return true;
        }

        final StringBuilder builder = new StringBuilder();
        for (final String arg : instance.getArguments()) {
            builder.append(arg).append(" ");
        }

        try {
            final Response<ResponseBody> bodyResponse =
                mathManager.mathify(builder.toString()).execute();
            if (!bodyResponse.isSuccessful()) {
                discordCommandCaller.message("Failed to send the request :(");
            } else {
                discordCommandCaller.message("Result: " + bodyResponse.body().string());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }
}
