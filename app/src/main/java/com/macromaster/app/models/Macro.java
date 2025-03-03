package com.macromaster.app.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a complete macro with a name, unique ID, actions, and conditions.
 */
public class Macro implements Parcelable {
    
    private String id;
    private String name;
    private List<Action> actions;
    private List<Condition> conditions;
    private boolean isRunning;
    private long createdAt;
    private long lastModifiedAt;
    
    public Macro(String name, List<Action> actions, List<Condition> conditions) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.actions = new ArrayList<>(actions);
        this.conditions = new ArrayList<>(conditions);
        this.isRunning = false;
        this.createdAt = System.currentTimeMillis();
        this.lastModifiedAt = this.createdAt;
    }
    
    protected Macro(Parcel in) {
        id = in.readString();
        name = in.readString();
        actions = new ArrayList<>();
        in.readTypedList(actions, Action.CREATOR);
        conditions = new ArrayList<>();
        in.readTypedList(conditions, Condition.CREATOR);
        isRunning = in.readByte() != 0;
        createdAt = in.readLong();
        lastModifiedAt = in.readLong();
    }
    
    public static final Creator<Macro> CREATOR = new Creator<Macro>() {
        @Override
        public Macro createFromParcel(Parcel in) {
            return new Macro(in);
        }
        
        @Override
        public Macro[] newArray(int size) {
            return new Macro[size];
        }
    };
    
    @Override
    public int describeContents() {
        return 0;
    }
    
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeTypedList(actions);
        dest.writeTypedList(conditions);
        dest.writeByte((byte) (isRunning ? 1 : 0));
        dest.writeLong(createdAt);
        dest.writeLong(lastModifiedAt);
    }
    
    // Getters and setters
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
        this.lastModifiedAt = System.currentTimeMillis();
    }
    
    public List<Action> getActions() {
        return actions;
    }
    
    public void setActions(List<Action> actions) {
        this.actions = new ArrayList<>(actions);
        this.lastModifiedAt = System.currentTimeMillis();
    }
    
    public void addAction(Action action) {
        this.actions.add(action);
        this.lastModifiedAt = System.currentTimeMillis();
    }
    
    public void removeAction(int index) {
        if (index >= 0 && index < actions.size()) {
            this.actions.remove(index);
            this.lastModifiedAt = System.currentTimeMillis();
        }
    }
    
    public List<Condition> getConditions() {
        return conditions;
    }
    
    public void setConditions(List<Condition> conditions) {
        this.conditions = new ArrayList<>(conditions);
        this.lastModifiedAt = System.currentTimeMillis();
    }
    
    public void addCondition(Condition condition) {
        this.conditions.add(condition);
        this.lastModifiedAt = System.currentTimeMillis();
    }
    
    public void removeCondition(int index) {
        if (index >= 0 && index < conditions.size()) {
            this.conditions.remove(index);
            this.lastModifiedAt = System.currentTimeMillis();
        }
    }
    
    public boolean isRunning() {
        return isRunning;
    }
    
    public void setRunning(boolean running) {
        isRunning = running;
    }
    
    public long getCreatedAt() {
        return createdAt;
    }
    
    public long getLastModifiedAt() {
        return lastModifiedAt;
    }
    
    @Override
    public String toString() {
        return "Macro{" +
                "name='" + name + '\'' +
                ", actions=" + actions.size() +
                ", conditions=" + conditions.size() +
                '}';
    }
}