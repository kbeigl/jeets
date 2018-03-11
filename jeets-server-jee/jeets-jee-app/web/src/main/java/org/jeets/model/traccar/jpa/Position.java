package org.jeets.model.traccar.jpa;
// Generated 20.02.2017 21:12:15 by Hibernate Tools 4.3.5.Final

import java.util.Date;

public class Position implements java.io.Serializable {

    private static final long serialVersionUID = 1L;
    private int id;
    private Device device;
    private String protocol;
    private Date servertime;
    private Date devicetime;
    private Date fixtime;
    private boolean valid;
    private double latitude;
    private double longitude;
    private double altitude;
    private double speed;
    private double course;
    private String address;
    private String attributes;
    private double accuracy;
    private String network;

    public Position() {
    }

    public Position(int id, Device device, Date servertime, Date devicetime, Date fixtime, boolean valid,
            double latitude, double longitude, double altitude, double speed, double course, double accuracy) {
        this.id = id;
        this.device = device;
        this.servertime = servertime;
        this.devicetime = devicetime;
        this.fixtime = fixtime;
        this.valid = valid;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.speed = speed;
        this.course = course;
        this.accuracy = accuracy;
    }

    public Position(int id, Device device, String protocol, Date servertime, Date devicetime, Date fixtime,
            boolean valid, double latitude, double longitude, double altitude, double speed, double course,
            String address, String attributes, double accuracy, String network) {
        this.id = id;
        this.device = device;
        this.protocol = protocol;
        this.servertime = servertime;
        this.devicetime = devicetime;
        this.fixtime = fixtime;
        this.valid = valid;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.speed = speed;
        this.course = course;
        this.address = address;
        this.attributes = attributes;
        this.accuracy = accuracy;
        this.network = network;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Device getDevice() {
        return this.device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public String getProtocol() {
        return this.protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public Date getServertime() {
        return this.servertime;
    }

    public void setServertime(Date servertime) {
        this.servertime = servertime;
    }

    public Date getDevicetime() {
        return this.devicetime;
    }

    public void setDevicetime(Date devicetime) {
        this.devicetime = devicetime;
    }

    public Date getFixtime() {
        return this.fixtime;
    }

    public void setFixtime(Date fixtime) {
        this.fixtime = fixtime;
    }

    public boolean isValid() {
        return this.valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAltitude() {
        return this.altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public double getSpeed() {
        return this.speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getCourse() {
        return this.course;
    }

    public void setCourse(double course) {
        this.course = course;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAttributes() {
        return this.attributes;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

    public double getAccuracy() {
        return this.accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public String getNetwork() {
        return this.network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    @Override
    public String toString() {
//      + ", device=" + device 
        return "Position [id=" + id + ", protocol=" + protocol + ", servertime=" + servertime
                + ", devicetime=" + devicetime + ", fixtime=" + fixtime + ", valid=" + valid + ", latitude=" + latitude
                + ", longitude=" + longitude + ", altitude=" + altitude + ", speed=" + speed + ", course=" + course
                + "]";
    }

}
