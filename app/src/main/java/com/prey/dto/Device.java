/*******************************************************************************
 * Created by Orlando Aliaga
 * Copyright 2016 Prey Inc. All rights reserved.
 * License: GPLv3
 * Full license at "/LICENSE"
 ******************************************************************************/
package com.prey.dto;

public class Device {

    private String name;
    private String key ;
    private String type ;
    private String description;
    private String missing ;
    private String unreachable;
    private String location ;
    private String unreadReport ;
    private String state ;
    private String os ;
    private String osVersion ;
    private String clientVersion ;
    private String icon ;
    private String reportsCount ;
    private String delay ;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMissing() {
        return missing;
    }

    public void setMissing(String missing) {
        this.missing = missing;
    }

    public String getUnreachable() {
        return unreachable;
    }

    public void setUnreachable(String unreachable) {
        this.unreachable = unreachable;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getUnreadReport() {
        return unreadReport;
    }

    public void setUnreadReport(String unreadReport) {
        this.unreadReport = unreadReport;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getClientVersion() {
        return clientVersion;
    }

    public void setClientVersion(String clientVersion) {
        this.clientVersion = clientVersion;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getReportsCount() {
        return reportsCount;
    }

    public void setReportsCount(String reportsCount) {
        this.reportsCount = reportsCount;
    }

    public String getDelay() {
        return delay;
    }

    public void setDelay(String delay) {
        this.delay = delay;
    }
}
