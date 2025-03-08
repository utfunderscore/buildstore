package org.readutf.buildstore.sql;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import java.sql.SQLException;
import java.util.Map;
import org.jspecify.annotations.NonNull;
import org.readutf.buildstore.api.BuildData;
import org.readutf.buildstore.api.BuildDataStore;
import org.readutf.buildstore.api.exception.BuildException;
import org.readutf.buildstore.sql.tables.BuildDataTable;

public class SQLBuildDataStore implements BuildDataStore {

    private final Dao<BuildDataTable, Integer> buildDataDao;

    public SQLBuildDataStore(@NonNull ConnectionSource connectionSource) {
        try {
            System.out.println("Creating build data table");
            buildDataDao = DaoManager.createDao(connectionSource, BuildDataTable.class);
            TableUtils.createTableIfNotExists(connectionSource, BuildDataTable.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public BuildData load(String name, int version) throws BuildException {
        try {
            BuildDataTable buildDataTable = buildDataDao.queryForFieldValues(Map.of("name", name, "version", version)).getFirst();
            if (buildDataTable == null) {
                throw new BuildException("Could not find v%d of %s".formatted(version, name));
            }
            return new BuildData(name, version, buildDataTable.getSchematicBytes());
        } catch (SQLException e) {
            throw new BuildException(e, "Failed to load build data.");
        }
    }

    @Override
    public void save(BuildData data) throws BuildException {
        try {
            buildDataDao.createOrUpdate(new BuildDataTable(data));
        } catch (SQLException e) {
            throw new BuildException(e, "Failed to store build data.");
        }

    }
}
