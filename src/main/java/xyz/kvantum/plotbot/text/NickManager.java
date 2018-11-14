package xyz.kvantum.plotbot.text;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.core.entities.User;
import org.polyjdbc.core.query.SelectQuery;
import xyz.kvantum.plotbot.SQLiteManager;

@RequiredArgsConstructor public class NickManager
{

	private final SQLiteManager sqLiteManager;
	private final Map<Long, String> nickCache = new ConcurrentHashMap<>();
	private final Map<Long, Boolean> nickStatusCache = new ConcurrentHashMap<>();

	@Nullable public String getNick(@NonNull final User user)
	{
		if ( !nickStatusCache.containsKey( user.getIdLong() ) )
		{
			this.lookup( user );
		}
		if ( nickStatusCache.get( user.getIdLong() ) )
		{
			return nickCache.get( user.getIdLong() );
		}
		return null;
	}

	private void lookup(@NonNull final User user)
	{
		sqLiteManager.getPolyJDBC().transactionRunner().run( queryRunner -> {
			final SelectQuery selectQuery = sqLiteManager.getPolyJDBC().query().select()
		} );
	}

}
