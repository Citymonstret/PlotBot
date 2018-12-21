package xyz.kvantum.plotbot.commands;

import com.intellectualsites.commands.Command;
import com.intellectualsites.commands.CommandDeclaration;
import com.intellectualsites.commands.CommandInstance;
import com.unikre.yandex.YandexTranslator;
import com.unikre.yandex.params.Language;
import xyz.kvantum.plotbot.BotConfig.Yandex;

@CommandDeclaration(command = "translate", usage = "!translate <string>", aliases = "tr", permission = "all")
public class Translate extends Command {

  private final YandexTranslator yandexTranslator;

  public Translate() {
    if (Yandex.apiToken.isEmpty()) {
      throw new IllegalStateException("No Yandex token configured");
    }
    this.yandexTranslator = new YandexTranslator(Yandex.apiToken);
  }

  @Override
  public boolean onCommand(final CommandInstance instance) {
    final StringBuilder string = new StringBuilder();
    for (final String arg : instance.getArguments()) {
      string.append(arg).append(" ");
    }
    if (string.toString().isEmpty()) {
      instance.getCaller().message("You need to specify a string to translate");
    }
    try {
      final String translated = yandexTranslator.translate(string.toString(), Language.ENGLISH);
      instance.getCaller().message(String.format("Translated: %s", translated));
    } catch (final Exception e) {
      instance.getCaller().message(String.format("Failed to translate the message: %s", e.getMessage()));
      e.printStackTrace();
    }
    return true;
  }
}
