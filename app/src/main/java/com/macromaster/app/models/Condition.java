package com.macromaster.app.models;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represents a condition that can trigger actions or stop a macro.
 * Conditions include image matching, text matching, and timeouts.
 */
public class Condition implements Parcelable {
    
    public enum Type {
        IMAGE_MATCH,
        TEXT_MATCH,
        TIMEOUT
    }
    
    private Type type;
    private Bitmap targetImage;
    private String targetText;
    private long timeout;
    private float matchThreshold;
    private boolean stopOnMatch;
    
    // Constructor for IMAGE_MATCH condition
    public Condition(Type type, Bitmap targetImage, float matchThreshold, boolean stopOnMatch) {
        this.type = type;
        this.targetImage = targetImage;
        this.matchThreshold = matchThreshold;
        this.stopOnMatch = stopOnMatch;
    }
    
    // Constructor for TEXT_MATCH condition
    public Condition(Type type, String targetText, boolean stopOnMatch) {
        this.type = type;
        this.targetText = targetText;
        this.stopOnMatch = stopOnMatch;
    }
    
    // Constructor for TIMEOUT condition
    public Condition(Type type, long timeout) {
        this.type = type;
        this.timeout = timeout;
        this.stopOnMatch = true; // Timeouts always stop the macro
    }
    
    protected Condition(Parcel in) {
        type = Type.valueOf(in.readString());
        targetImage = in.readParcelable(Bitmap.class.getClassLoader());
        targetText = in.readString();
        timeout = in.readLong();
        matchThreshold = in.readFloat();
        stopOnMatch = in.readByte() != 0;
    }
    
    public static final Creator<Condition> CREATOR = new Creator<Condition>() {
        @Override
        public Condition createFromParcel(Parcel in) {
            return new Condition(in);
        }
        
        @Override
        public Condition[] newArray(int size) {
            return new Condition[size];
        }
    };
    
    @Override
    public int describeContents() {
        return 0;
    }
    
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type.name());
        dest.writeParcelable(targetImage, flags);
        dest.writeString(targetText);
        dest.writeLong(timeout);
        dest.writeFloat(matchThreshold);
        dest.writeByte((byte) (stopOnMatch ? 1 : 0));
    }
    
    // Getters and setters
    
    public Type getType() {
        return type;
    }
    
    public void setType(Type type) {
        this.type = type;
    }
    
    public Bitmap getTargetImage() {
        return targetImage;
    }
    
    public void setTargetImage(Bitmap targetImage) {
        this.targetImage = targetImage;
    }
    
    public String getTargetText() {
        return targetText;
    }
    
    public void setTargetText(String targetText) {
        this.targetText = targetText;
    }
    
    public long getTimeout() {
        return timeout;
    }
    
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }
    
    public float getMatchThreshold() {
        return matchThreshold;
    }
    
    public void setMatchThreshold(float matchThreshold) {
        this.matchThreshold = matchThreshold;
    }
    
    public boolean isStopOnMatch() {
        return stopOnMatch;
    }
    
    public void setStopOnMatch(boolean stopOnMatch) {
        this.stopOnMatch = stopOnMatch;
    }
    
    @Override
    public String toString() {
        switch (type) {
            case IMAGE_MATCH:
                return "Match image with threshold " + matchThreshold + 
                       (stopOnMatch ? " (Stop on match)" : " (Continue on match)");
            case TEXT_MATCH:
                return "Match text: \"" + targetText + "\"" + 
                       (stopOnMatch ? " (Stop on match)" : " (Continue on match)");
            case TIMEOUT:
                return "Timeout after " + timeout + " ms";
            default:
                return "Unknown condition";
        }
    }
}