package xyz.kvantum.plotbot.text;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.core.entities.User;
import xyz.kvantum.plotbot.SQLiteManager;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor public class NickManager {

    private final SQLiteManager sqLiteManager;
    private final Map<Long, String> nickCache = new ConcurrentHashMap<>();
    private final Map<Long, Boolean> nickStatusCache = new ConcurrentHashMap<>();

    @Nullable public String getNick(@NonNull final User user) {
        if (!nickStatusCache.containsKey(user.getIdLong())) {
            this.lookup(user);
        }
        if (nickStatusCache.get(user.getIdLong())) {
            return nickCache.get(user.getIdLong());
        }
        return null;
    }

    private void lookup(@NonNull final User user) {
		/*sqLiteManager.getPolyJDBC().transactionRunner().run( queryRunner -> {
			final SelectQuery selectQuery = sqLiteManager.getPolyJDBC().query().select()
		} );*/
    }

}
