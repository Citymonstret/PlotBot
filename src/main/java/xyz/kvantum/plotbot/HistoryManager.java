package xyz.kvantum.plotbot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.core.entities.Message;
import org.polyjdbc.core.query.Order;
import org.polyjdbc.core.query.QueryRunner;
import org.polyjdbc.core.query.SelectQuery;
import org.polyjdbc.core.query.VoidTransactionWrapper;
import org.polyjdbc.core.query.mapper.StringMapper;

@RequiredArgsConstructor
public class HistoryManager
{

	private final SQLiteManager sqliteManager;

	public void logMessage(final Message message)
	{
		sqliteManager.getPolyJDBC().transactionRunner().run( new VoidTransactionWrapper()
		{
			@Override public void performVoid(final QueryRunner queryRunner)
			{
				queryRunner.insert( sqliteManager.getPolyJDBC().query().insert().into( "history" ).value( "timestamp", System.currentTimeMillis() ).value( "user", message.getAuthor().getIdLong() ).value( "message", message.getContentRaw() ).value( "channel", message.getChannel().getIdLong() ) );
			}
		} );
	}

	public List<String> getMessages(final long userId, final long channelId, final int count)
	{
		final List<String> strings = new ArrayList<>( sqliteManager.getPolyJDBC().transactionRunner().run( queryRunner ->
		{
			final SelectQuery selectQuery = sqliteManager.getPolyJDBC().query().select( "message" ).from( "history" ).where( "user = :user AND channel = :channel" ).limit( count ).withArgument( "user", userId ).withArgument( "channel", channelId ).orderBy( "timestamp",
					Order.DESC );
			return queryRunner.queryList( selectQuery, new StringMapper() );
		} ) );
		Collections.reverse( strings );
		return strings;
	}
	
}
