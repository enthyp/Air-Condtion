package com.po.app.data.airly.repository;

public abstract class AirlyDataSourceDecorator implements IAirlyDataSource {

    protected IAirlyDataSource dataSource;

    protected AirlyDataSourceDecorator(IAirlyDataSource dataSource) {
        this.dataSource = dataSource;
    }
}
