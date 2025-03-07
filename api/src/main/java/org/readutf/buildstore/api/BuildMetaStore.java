package org.readutf.buildstore.api;

import java.util.List;
import org.readutf.buildstore.api.exception.BuildException;

public interface BuildMetaStore {

    BuildMeta getBuildMeta(String name, int version) throws BuildException;

    List<BuildMeta> getBuildMetas(String name) throws BuildException;

    boolean exists(String buildName) throws BuildException;

    void save(BuildMeta meta) throws BuildException;

    BuildMeta getLatest(String buildName) throws BuildException;
}
