package com.po.app.data.gios.repository;

public abstract class GiosDataSourceDecorator implements IGiosDataSource {

    protected IGiosDataSource dataSource;

    protected GiosDataSourceDecorator(IGiosDataSource dataSource) {
        this.dataSource = dataSource;
    }
}
