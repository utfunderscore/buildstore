package org.readutf.buildstore.sql.tables;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.readutf.buildstore.api.BuildData;

@DatabaseTable(tableName = "build_data")
public class BuildDataTable {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(columnName = "name", canBeNull = false)
    private String name;

    @DatabaseField(columnName = "version", canBeNull = false)
    private int version;

    @DatabaseField(columnName = "schematic_bytes", canBeNull = false, dataType = DataType.BYTE_ARRAY)
    private byte[] schematicBytes;

    BuildDataTable() {}

    public BuildDataTable(BuildData buildData) {
        this.name = buildData.name();
        this.version = buildData.version();
        this.schematicBytes = buildData.schematicBytes();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public byte[] getSchematicBytes() {
        return schematicBytes;
    }

    public void setSchematicBytes(byte[] schematicBytes) {
        this.schematicBytes = schematicBytes;
    }
}
