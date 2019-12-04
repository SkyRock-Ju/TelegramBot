package Subscriber;

import com.google.cloud.firestore.GeoPoint;

public class Subscriber {

    private Integer chatID;
    private Integer messageID;
    private GeoPoint geoPoint;
    private Integer userID;

    public Subscriber() {
    }

    public Subscriber(Integer chatID, Integer messageID, GeoPoint geoPoint, Integer userID) {
        this.chatID = chatID;
        this.messageID = messageID;
        this.geoPoint = geoPoint;
        this.userID = userID;
    }

    public Integer getChatID() {
        return chatID;
    }

    public void setChatID(Integer chatID) {
        this.chatID = chatID;
    }

    public Integer getMessageID() {
        return messageID;
    }

    public void setMessageID(Integer messageID) {
        this.messageID = messageID;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }
}