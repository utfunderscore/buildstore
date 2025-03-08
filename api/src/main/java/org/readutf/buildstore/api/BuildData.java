package org.readutf.buildstore.api;

import java.util.Arrays;
import java.util.Objects;

public final class BuildData {
    private final String name;
    private final int version;
    private final byte[] schematicBytes;

    public BuildData(String name, int version, byte[] schematicBytes) {
        this.name = name;
        this.version = version;
        this.schematicBytes = schematicBytes;
    }

    public String name() {
        return name;
    }

    public int version() {
        return version;
    }

    public byte[] schematicBytes() {
        return schematicBytes;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (BuildData) obj;
        return Objects.equals(this.name, that.name) &&
                this.version == that.version &&
                Arrays.equals(this.schematicBytes, that.schematicBytes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, version, Arrays.hashCode(schematicBytes));
    }

    @Override
    public String toString() {
        return "BuildData[" +
                "name=" + name + ", " +
                "version=" + version + ", " +
                "schematicBytes=" + Arrays.toString(schematicBytes) + ']';
    }


}