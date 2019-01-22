package xyz.kvantum.plotbot.commands;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.intellectualsites.commands.Command;
import com.intellectualsites.commands.CommandDeclaration;
import com.intellectualsites.commands.CommandInstance;
import com.intellectualsites.commands.parser.impl.StringParser;
import xyz.kvantum.plotbot.IncendoPaster;
import xyz.kvantum.plotbot.IncendoPaster.PasteFile;

@CommandDeclaration(command = "paste", aliases = {"write"}, usage = "!paste <name> <content, |- replaced with newline>", description = "Paste to Incendo")
public class Paste extends Command {

  public Paste() {
    withArgument("name", new StringParser(), "paste file name");
  }

  @Override
  public boolean onCommand(final CommandInstance instance) {
    final String fileName = instance.getString("name");
    if (instance.getArguments().length < 2) {
      instance.getCaller().message("File content required!");
    } else {
      final StringBuilder content = new StringBuilder();
      for (int i = 1; i < instance.getArguments().length; i++) {
        content.append(instance.getArguments()[i]);
        if ((i + 1) < instance.getArguments().length) {
          content.append(" ");
        }
      }
      final String body = content.toString().replace("|-", "\n");
      final IncendoPaster paster = new IncendoPaster("plotsquared");
      paster.addFile(new PasteFile(fileName, body));
      try {
        final String response = paster.upload();
        final JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
        if (jsonObject.has("created")) {
          final String pasteId = jsonObject.get("paste_id").getAsString();
          instance.getCaller().message(String.format("Pasted! The paste can be viewed at https://incendo.org/paste/view/%s", pasteId));
        } else {
          instance.getCaller().message(String.format("Failed to upload files: %s",
              jsonObject.get("response").getAsString()));
        }
      } catch (final Throwable throwable) {
        instance.getCaller().message("Failed to paste content: " + throwable.getMessage());
      }
    }
    return true;
  }

}
