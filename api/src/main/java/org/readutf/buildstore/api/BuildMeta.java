package org.readutf.buildstore.api;

import java.util.List;
import java.util.UUID;
import org.jspecify.annotations.NonNull;

public record BuildMeta(@NonNull String name, int version, @NonNull String description, @NonNull List<String> labels,
                        @NonNull UUID savedBy, long savedAt) {

    public String getId() {
        return name + ":" + version;
    }

}

