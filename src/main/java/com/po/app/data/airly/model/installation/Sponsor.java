package com.po.app.data.airly.model.installation;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Sponsor {
    @JsonProperty
    private int id;

    @JsonProperty
    private String logo;

    @JsonProperty
    private String description;

    @JsonProperty
    private String link;

    @JsonProperty
    private String name;

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
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

    @Override
    public String toString() {
        return "Sponsor{" +
                "id=" + id +
                ", logo='" + logo + '\'' +
                ", description='" + description + '\'' +
                ", link='" + link + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
