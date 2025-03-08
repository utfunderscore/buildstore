package org.readutf.buildstore.sql.tables;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
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

    @ForeignCollectionField(eager = true)
    private ForeignCollection<BuildLabelsTable> labels;

    @DatabaseField(columnName = "saved_by", canBeNull = false)
    private UUID savedBy;

    @DatabaseField(columnName = "saved_at", canBeNull = false)
    private Timestamp savedAt;

    BuildMetaTable() {}

    public BuildMetaTable(BuildMeta buildMeta) {
        this.name = buildMeta.name();
        this.version = buildMeta.version();
        this.description = buildMeta.description();
        this.savedBy = buildMeta.savedBy();
        this.savedAt = Timestamp.from(buildMeta.savedAt().toInstant(ZoneOffset.UTC));
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

    public ForeignCollection<BuildLabelsTable> getLabels() {
        return labels;
    }

    public void setLabels(ForeignCollection<BuildLabelsTable> labels) {
        this.labels = labels;
    }

    public UUID getSavedBy() {
        return savedBy;
    }

    public void setSavedBy(UUID savedBy) {
        this.savedBy = savedBy;
    }

    public Timestamp getSavedAt() {
        return savedAt;
    }

    public void setSavedAt(Timestamp savedAt) {
        this.savedAt = savedAt;
    }
}
