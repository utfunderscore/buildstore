package org.readutf.buildstore.sql;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.table.TableUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.readutf.buildstore.api.BuildMeta;

class BuildMetaTableTest {

    private static final String jdbcUrl = "jdbc:h2:mem:test";

    @Test
    void testCreateTable() throws SQLException {
        Dao<BuildMetaTable, Integer> buildMetaDao = DaoManager.createDao(new JdbcConnectionSource(jdbcUrl), BuildMetaTable.class);

        TableUtils.createTable(buildMetaDao);
    }

    @Test
    void testInsert() throws SQLException, IOException {
        Dao<BuildMetaTable, Integer> buildMetaDao = DaoManager.createDao(new JdbcConnectionSource(jdbcUrl), BuildMetaTable.class);

        BuildMeta buildMeta = new BuildMeta("test", 1, "", List.of(), UUID.randomUUID(), 0);
        new BuildMetaTable()


        buildMetaDao.create(buildMeta);
    }

}