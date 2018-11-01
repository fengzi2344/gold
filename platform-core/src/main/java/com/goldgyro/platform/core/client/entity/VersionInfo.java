package com.goldgyro.platform.core.client.entity;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "T_VERSION_INFO" )
public class VersionInfo {

    @Id
    @GeneratedValue(generator = "jpa-uuid")
    @Column(length = 32)
    private String id;
    @Column(name="VERSION_CODE", length = 32)
    private String versionCode;
    @Column(name = "CODE", length = 32)
    private String code;
    @Column(name = "FLAG",length = 1)
    private String flag;
    @Column(name = "UPDATE_NOTE", length = 255)
    private String updateNote;
    @Column(name = "RELEASE_TIME")
    private Timestamp releaseTime;
    @Column(name = "OS_TYPE")
    private String osType;

    public VersionInfo(){}

    public VersionInfo(String id, String versionCode, String code, String flag, String updateNote, Timestamp releaseTime,String osType) {
        this.id = id;
        this.versionCode = versionCode;
        this.code = code;
        this.flag = flag;
        this.updateNote = updateNote;
        this.releaseTime = releaseTime;
        this.osType = osType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getUpdateNote() {
        return updateNote;
    }

    public void setUpdateNote(String updateNote) {
        this.updateNote = updateNote;
    }

    public Timestamp getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(Timestamp releaseTime) {
        this.releaseTime = releaseTime;
    }

    public String getOsType() {
        return osType;
    }

    public void setOsType(String osType) {
        this.osType = osType;
    }
}
