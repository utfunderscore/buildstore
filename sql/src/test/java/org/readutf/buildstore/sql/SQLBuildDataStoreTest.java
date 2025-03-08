package org.readutf.buildstore.sql;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import java.io.IOException;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.readutf.buildstore.api.BuildData;
import org.readutf.buildstore.api.exception.BuildException;

class SQLBuildDataStoreTest {

    @Test
    void testConstruction() throws SQLException, BuildException {
        new SQLBuildDataStore(new JdbcConnectionSource("jdbc:h2:mem:test"));
    }

    @Test
    void testSmallBuildDataInsert() throws BuildException, SQLException, IOException {
        SQLBuildDataStore buildDataStore = new SQLBuildDataStore(new JdbcConnectionSource("jdbc:h2:mem:test1"));

        buildDataStore.save(new BuildData("test", 1, getClass().getClassLoader().getResource("small.schem").getFile().getBytes()));
    }

    @Test
    void testMediumBuildDataInsert() throws BuildException, SQLException {
        SQLBuildDataStore buildDataStore = new SQLBuildDataStore(new JdbcConnectionSource("jdbc:h2:mem:test2"));

        buildDataStore.save(new BuildData("test", 1, getClass().getClassLoader().getResource("medium.schem").getFile().getBytes()));
    }


    @Test
    void loadBuildData() throws BuildException, SQLException {
        SQLBuildDataStore buildDataStore = new SQLBuildDataStore(new JdbcConnectionSource("jdbc:h2:mem:test3"));

        BuildData original = new BuildData(
                "test", 1, getClass().getClassLoader().getResource("small.schem").getFile().getBytes());
        buildDataStore.save(original);

        BuildData buildData = buildDataStore.load("test", 1);

        assertEquals(original, buildData);

    }



}