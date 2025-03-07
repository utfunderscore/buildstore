package org.readutf.buildstore.sql;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class ORMLiteBuildDataStoreTest {

    @Test
    void testConstruction() throws SQLException {
        ORMLiteBuildDataStore SQLBuildDataSource = new ORMLiteBuildDataStore(new JdbcConnectionSource("jdbc:h2:mem:test"));
    }

}