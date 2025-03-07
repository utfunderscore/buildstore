package org.readutf.buildstore.sql;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import java.sql.SQLException;
import java.util.PrimitiveIterator;
import org.jspecify.annotations.NonNull;
import org.readutf.buildstore.api.BuildData;
import org.readutf.buildstore.api.BuildDataStore;
import org.readutf.buildstore.api.exception.BuildException;

public class ORMLiteBuildDataStore implements BuildDataStore {


    private final Dao<BuildDataTable, Integer> buildDataDao;

    public ORMLiteBuildDataStore(@NonNull ConnectionSource connectionSource) {
        try {
            buildDataDao = DaoManager.createDao(connectionSource, BuildDataTable.class);
            TableUtils.createTable(connectionSource, BuildDataTable.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public BuildData load(String name, int version) throws BuildException {
        return null;
    }

    @Override
    public void save(BuildData data) throws BuildException {

    }
}
