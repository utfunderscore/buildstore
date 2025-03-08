package org.readutf.buildstore.sql;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.readutf.buildstore.api.BuildMeta;
import org.readutf.buildstore.api.exception.BuildException;

class SQLBuildMetaStoreTest {

    @Test
    void testConstructor() throws SQLException {
        SQLBuildMetaStore sqlBuildMetaStore = new SQLBuildMetaStore(new JdbcConnectionSource("jdbc:h2:mem:buildmetastore1"));
        assertNotNull(sqlBuildMetaStore);
    }

    @Test
    void saveBuildMeta() throws SQLException, BuildException {
        SQLBuildMetaStore sqlBuildMetaStore = new SQLBuildMetaStore(new JdbcConnectionSource("jdbc:h2:mem:buildmetastore2"));
        sqlBuildMetaStore.save(new BuildMeta("name", 1, "description", List.of("label1", "label2"), UUID.randomUUID(), LocalDateTime.now()));
    }

    @Test
    void getBuildMeta() throws SQLException, BuildException {
        SQLBuildMetaStore sqlBuildMetaStore = new SQLBuildMetaStore(new JdbcConnectionSource("jdbc:h2:mem:buildmetastore3"));
        BuildMeta original = new BuildMeta(
                "name", 1, "description", List.of("label1", "label2"), UUID.randomUUID(), LocalDateTime.now());
        sqlBuildMetaStore.save(original);

        BuildMeta buildMeta = sqlBuildMetaStore.getBuildMeta("name", 1);
        assertEquals(original, buildMeta);
    }

}