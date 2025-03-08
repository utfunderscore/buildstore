package org.readutf.buildstore.sql;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import org.jspecify.annotations.NonNull;
import org.readutf.buildstore.api.BuildMeta;
import org.readutf.buildstore.api.BuildMetaStore;
import org.readutf.buildstore.api.exception.BuildException;
import org.readutf.buildstore.sql.tables.BuildDataTable;
import org.readutf.buildstore.sql.tables.BuildLabelsTable;
import org.readutf.buildstore.sql.tables.BuildMetaTable;

public class SQLBuildMetaStore implements BuildMetaStore {

    private @NonNull final Dao<BuildMetaTable, Integer> buildDataDao;
    private @NonNull final Dao<BuildLabelsTable, Integer> buildLabelsDao;

    public SQLBuildMetaStore(@NonNull ConnectionSource connectionSource) {
        try {
            buildDataDao = DaoManager.createDao(connectionSource, BuildMetaTable.class);
            buildLabelsDao = DaoManager.createDao(connectionSource, BuildLabelsTable.class);
            TableUtils.createTableIfNotExists(connectionSource, BuildMetaTable.class);
            TableUtils.createTableIfNotExists(connectionSource, BuildLabelsTable.class);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public BuildMeta getBuildMeta(String name, int version) throws BuildException {
        try {
            BuildMetaTable buildMetaTable = buildDataDao.queryForFieldValues(
                    Map.of("name", name, "version", version)).getFirst();

            if (buildMetaTable == null) {
                throw new BuildException("Could not find v%d of %s".formatted(version, name));
            } else {
                return new BuildMeta(
                        buildMetaTable.getName(), buildMetaTable.getVersion(), buildMetaTable.getDescription(),
                        buildMetaTable.getLabels().stream().map(BuildLabelsTable::getLabel).toList(),
                        buildMetaTable.getSavedBy(), buildMetaTable.getSavedAt().toLocalDateTime()
                );
            }
        } catch (SQLException e) {
            throw new BuildException(e, "Failed to load build data.");
        }
    }

    @Override
    public List<BuildMeta> getBuildMetas(String name) throws BuildException {
        try {
            return buildDataDao.queryForAll().stream().map(buildMetaTable -> new BuildMeta(
                    buildMetaTable.getName(), buildMetaTable.getVersion(), buildMetaTable.getDescription(),
                    buildMetaTable.getLabels().stream().map(BuildLabelsTable::getLabel).toList(),
                    buildMetaTable.getSavedBy(), buildMetaTable.getSavedAt().toLocalDateTime()
            )).toList();
        } catch (SQLException e) {
            throw new BuildException(e, "Failed to load build data.");
        }
    }

    @Override
    public boolean exists(String buildName) throws BuildException {
        try {
            return !buildDataDao.queryForEq("name", buildName).isEmpty();
        } catch (SQLException e) {
            throw new BuildException(e, "Failed to load build data.");
        }
    }

    @Override
    public void save(BuildMeta meta) throws BuildException {
        try {
            BuildMetaTable table = new BuildMetaTable(meta);
            buildDataDao.createOrUpdate(table);

            for (String label : meta.labels()) {
                buildLabelsDao.createOrUpdate(new BuildLabelsTable(table, label));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new BuildException(e, "Failed to store build data.");
        }
    }

    @Override
    public BuildMeta getLatest(String buildName) throws BuildException {
        try {
            List<BuildMetaTable> results = buildDataDao.queryBuilder()
                    .where().eq("name", buildName)
                    .queryBuilder()
                    .orderBy("version", false)
                    .limit(1L).query();

            if (results.isEmpty()) {
                throw new BuildException("Could not find latest build for %s".formatted(buildName));
            } else {
                BuildMetaTable buildMetaTable = results.getFirst();
                return new BuildMeta(
                        buildMetaTable.getName(), buildMetaTable.getVersion(), buildMetaTable.getDescription(),
                        buildMetaTable.getLabels().stream().map(BuildLabelsTable::getLabel).toList(),
                        buildMetaTable.getSavedBy(), buildMetaTable.getSavedAt().toLocalDateTime()
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
