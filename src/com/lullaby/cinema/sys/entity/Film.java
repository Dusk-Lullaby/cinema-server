package com.lullaby.cinema.sys.entity;

import java.io.Serializable;

/**
 * 影片
 */
public class Film implements Serializable {
    /**
     * 编号
     */
    private String id;
    /**
     * 名字
     */
    private String name;
    /**
     * 制片人
     */
    private String producer;
    /**
     * 描述
     */
    private String description;

    public Film(String id, String name, String producer, String description) {
        this.id = id;
        this.name = name;
        this.producer = producer;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return id + "\t" + name + "\t" + producer + "\t" + description;
    }
}
