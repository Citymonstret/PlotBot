package xyz.kvantum.plotbot;

import java.util.HashSet;
import java.util.Set;
import lombok.NonNull;
import net.dv8tion.jda.core.entities.Member;

public final class Annoyer {

  private static final Annoyer instance = new Annoyer();

  private static final String[] MESSAGES =
      new String[] {"I agree", "Yep", "Mhm", "Exactly", "Wow!", "Ah...", "Oh, I see...", "Really??", "Plz help me", "where fave 1.13????", "when ps update?!!?!", "ok!", "ok.",
        "Пожалуйста, дайте мне помощь на русском языке. Я не говорю по английски", "¿Qué?", "i dont undertstand", "uh i put in folder and then error", "WHEN CAN U HELP ME?", "y no update yet?",
      "That was a very smart thing to say!", "Wow. I haven't thought of it that way", "Is that so?"};

  public static Annoyer getInstance() {
    return instance;
  }

  private final Set<Long> peopleToAnnoy = new HashSet<>();

  private Annoyer() {
  }

  public String getRandomMessage() {
    return MESSAGES[(int) (Math.random() * (MESSAGES.length - 1))];
  }

  public boolean shouldAnnoy(@NonNull final Member member) {
    return this.peopleToAnnoy.contains(member.getUser().getIdLong());
  }

  public void toggleAnnoy(@NonNull final Member member) {
    if (this.peopleToAnnoy.contains(member.getUser().getIdLong())) {
      this.peopleToAnnoy.remove(member.getUser().getIdLong());
    } else {
      this.peopleToAnnoy.add(member.getUser().getIdLong());
    }
  }

}
