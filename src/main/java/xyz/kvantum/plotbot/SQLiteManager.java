package xyz.kvantum.plotbot;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import lombok.Getter;
import org.polyjdbc.core.PolyJDBC;
import org.polyjdbc.core.PolyJDBCBuilder;
import org.polyjdbc.core.dialect.DialectRegistry;

public final class SQLiteManager implements AutoCloseable
{

	private static final String PATH = "plotbot.db";

	@Getter
	private final Connection connection;
	@Getter
	private final PolyJDBC polyJDBC;

	public SQLiteManager()
	{
		Connection connection = null;
		PolyJDBC polyJDBC = null;
		try
		{
			connection = DriverManager.getConnection( "jdbc:sqlite:" + PATH );
			polyJDBC = PolyJDBCBuilder.polyJDBC( DialectRegistry.MYSQL.getDialect() ).usingManagedConnections(
					this::getConnection ).build();
		} catch ( final SQLException e )
		{
			e.printStackTrace();
		}
		this.connection = connection;
		this.polyJDBC = polyJDBC;
	}

	@Override public void close() throws Exception
	{
		if ( this.connection != null )
		{
			this.connection.close();
		}
	}
}
