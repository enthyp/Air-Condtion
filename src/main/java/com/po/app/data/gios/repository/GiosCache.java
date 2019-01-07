package com.po.app.data.gios.repository;

public class GiosCache implements IGiosCache {
    @Override
    public FileState check(String signature) {
        return FileState.MISSING;
    }
}
