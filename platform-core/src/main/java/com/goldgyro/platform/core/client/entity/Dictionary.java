package com.goldgyro.platform.core.client.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "T_DICTIONARY")
public class Dictionary {

    @Id
    private String id;
    @Column(name = "CODE", length = 32)
    private String code;
    @Column(name = "NAME", length = 255)
    private String name;
    @Column(name = "PARENT_CODE", length = 32)
    private String parentCode;
    @Column(name = "LEVEL", length = 10)
    private String level;
    @Column(name = "TYPE", length = 32)
    private String type;

    public Dictionary(String id, String code, String name, String parentCode, String level, String type) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.parentCode = parentCode;
        this.level = level;
        this.type = type;
    }
    public Dictionary(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
