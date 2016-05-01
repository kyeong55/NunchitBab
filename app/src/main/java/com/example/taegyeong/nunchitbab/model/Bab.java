package com.example.taegyeong.nunchitbab.model;

import io.realm.RealmObject;
import io.realm.annotations.Required;

/**
 * Created by yjchang on 5/1/16.
 */
public class Bab extends RealmObject {

    @Required
    private String          type;
    private long            timestamp;

    private String          json;


    // Getter & Setter

    public String getType()                     { return type; }
    public void setType(String type)            { this.type = type; }

    public long getTimestamp()                  { return timestamp; }
    public void setTimestamp(long timestamp)    { this.timestamp = timestamp; }

    public String getJson()                     { return json; }
    public void setJson(String json)            { this.json = json; }

}
