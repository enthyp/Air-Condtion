package com.po.app.REST;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Commune {
    @JsonProperty
    private String communeName;
    @JsonProperty
    private String districtName;
    @JsonProperty
    private String provinceName;

    public String getCommuneName() {
        return communeName;
    }

    public void setCommuneName(String communeName) {
        this.communeName = communeName;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    @Override
    public String toString() {
        return "Commune{" +
                "communeName='" + communeName + '\'' +
                ", districtName='" + districtName + '\'' +
                ", provinceName='" + provinceName + '\'' +
                '}';
    }
}
