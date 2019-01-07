package com.po.app.data.gios.repository;

public interface IGiosCache {

    enum FileState {
        UP_TO_DATE,
        OUT_OF_DATE,
        MISSING;
    }

    FileState check(String signature);

    // TODO: add separate methods for getting and setting cached values
    // TODO: setting should take a flag that would mean old version should be deleted
    // TODO: it should be possible to choose (command line) whether to use cached or non-cached version.
}
