package edu.mit.csail.sdg.squander.examples.trans;

public class Trans {

    private int timestamp;
    private boolean isBlocked;
    private boolean isReadOnly;
    
    public Trans(int timestamp, boolean isBlocked) {
        this(timestamp, isBlocked, false);
    }
    
    public Trans(int timestamp, boolean isBlocked, boolean isReadOnly) {
        this.timestamp = timestamp;
        this.isBlocked = isBlocked;
        this.isReadOnly = isReadOnly;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean isBlocked) {
        this.isBlocked = isBlocked;
    }

    public boolean isReadOnly() {
        return isReadOnly;
    }

    public void setReadOnly(boolean isReadOnly) {
        this.isReadOnly = isReadOnly;
    }

    @Override
    public String toString() {
        return String.format("%s%d (%s)", isBlocked ? "b" : "s", timestamp, isReadOnly ? "R" : "W");
    }
    
}
