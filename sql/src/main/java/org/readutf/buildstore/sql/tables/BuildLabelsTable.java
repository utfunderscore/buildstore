package org.readutf.buildstore.sql.tables;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "build_labels")
public class BuildLabelsTable {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(foreign = true, canBeNull = false)
    private BuildMetaTable buildMetaTable;

    @DatabaseField(columnName = "label", canBeNull = false)
    private String label;

    BuildLabelsTable() {
    }

    public BuildLabelsTable(BuildMetaTable buildMetaTable, String label) {
        this.buildMetaTable = buildMetaTable;
        this.label = label;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BuildMetaTable getBuildMetaTable() {
        return buildMetaTable;
    }

    public void setBuildMetaTable(BuildMetaTable buildMetaTable) {
        this.buildMetaTable = buildMetaTable;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
