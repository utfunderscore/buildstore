package org.readutf.buildstore.api;

import org.readutf.buildstore.api.exception.BuildException;

public interface BuildDataStore {

    BuildData load(String name, int version) throws BuildException;

    void save(BuildData data) throws BuildException;

}
