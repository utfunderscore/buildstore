package org.readutf.buildstore.sql;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.jspecify.annotations.NonNull;
import org.readutf.buildstore.api.BuildMeta;

@DatabaseTable(tableName = "build_meta")
public class BuildMetaTable {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(columnName = "name", canBeNull = false)
    private String name;

    @DatabaseField(columnName = "version", canBeNull = false)
    private int version;

    @DatabaseField(columnName = "description", canBeNull = false)
    private String description;

    @DatabaseField(columnName = "labels", canBeNull = false)
    private List<String> labels;

    @DatabaseField(columnName = "saved_by", canBeNull = false)
    private UUID savedBy;

    @DatabaseField(columnName = "saved_at", canBeNull = false)
    private LocalDateTime savedAt;

    BuildMetaTable() {}

    public BuildMetaTable(BuildMeta buildMeta) {
        this.name = buildMeta.name();
        this.version = buildMeta.version();
        this.description = buildMeta.description();
        this.labels = buildMeta.labels();
        this.savedBy = buildMeta.savedBy();
        this.savedAt = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLabels() {
        return labels;
    }

    public void setLabels(String labels) {
        this.labels = labels;
    }

    public String getSavedBy() {
        return savedBy;
    }

    public void setSavedBy(String savedBy) {
        this.savedBy = savedBy;
    }

    public long getSavedAt() {
        return savedAt;
    }

    public void setSavedAt(long savedAt) {
        this.savedAt = savedAt;
    }
}
