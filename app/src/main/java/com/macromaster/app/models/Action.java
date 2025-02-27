package com.macromaster.app.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represents an action that can be performed in a macro.
 * Actions include clicks, swipes, text input, and waiting.
 */
public class Action implements Parcelable {
    
    public enum Type {
        CLICK,
        SWIPE,
        TEXT_INPUT,
        WAIT
    }
    
    private Type type;
    private int x;
    private int y;
    private int endX;
    private int endY;
    private String text;
    private long duration;
    
    // Constructor for CLICK action
    public Action(Type type, int x, int y) {
        this.type = type;
        this.x = x;
        this.y = y;
    }
    
    // Constructor for SWIPE action
    public Action(Type type, int x, int y, int endX, int endY, long duration) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.endX = endX;
        this.endY = endY;
        this.duration = duration;
    }
    
    // Constructor for TEXT_INPUT action
    public Action(Type type, String text) {
        this.type = type;
        this.text = text;
    }
    
    // Constructor for WAIT action
    public Action(Type type, long duration) {
        this.type = type;
        this.duration = duration;
    }
    
    protected Action(Parcel in) {
        type = Type.valueOf(in.readString());
        x = in.readInt();
        y = in.readInt();
        endX = in.readInt();
        endY = in.readInt();
        text = in.readString();
        duration = in.readLong();
    }
    
    public static final Creator<Action> CREATOR = new Creator<Action>() {
        @Override
        public Action createFromParcel(Parcel in) {
            return new Action(in);
        }
        
        @Override
        public Action[] newArray(int size) {
            return new Action[size];
        }
    };
    
    @Override
    public int describeContents() {
        return 0;
    }
    
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type.name());
        dest.writeInt(x);
        dest.writeInt(y);
        dest.writeInt(endX);
        dest.writeInt(endY);
        dest.writeString(text);
        dest.writeLong(duration);
    }
    
    // Getters and setters
    
    public Type getType() {
        return type;
    }
    
    public void setType(Type type) {
        this.type = type;
    }
    
    public int getX() {
        return x;
    }
    
    public void setX(int x) {
        this.x = x;
    }
    
    public int getY() {
        return y;
    }
    
    public void setY(int y) {
        this.y = y;
    }
    
    public int getEndX() {
        return endX;
    }
    
    public void setEndX(int endX) {
        this.endX = endX;
    }
    
    public int getEndY() {
        return endY;
    }
    
    public void setEndY(int endY) {
        this.endY = endY;
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public long getDuration() {
        return duration;
    }
    
    public void setDuration(long duration) {
        this.duration = duration;
    }
    
    @Override
    public String toString() {
        switch (type) {
            case CLICK:
                return "Click at (" + x + ", " + y + ")";
            case SWIPE:
                return "Swipe from (" + x + ", " + y + ") to (" + endX + ", " + endY + ")";
            case TEXT_INPUT:
                return "Input text: " + text;
            case WAIT:
                return "Wait for " + duration + " ms";
            default:
                return "Unknown action";
        }
    }
}