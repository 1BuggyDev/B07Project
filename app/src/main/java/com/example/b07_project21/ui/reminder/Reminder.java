package com.example.b07_project21.ui.reminder;

public class Reminder {
    public enum Frequency { NONE }
    private String id;
    private long triggerAt;
    private String message;
    private Frequency frequency = Frequency.NONE;

    public Reminder() {}  // Firestore needs no-arg ctor

    public Reminder(String id, long t) {
        this.id = id;
        this.triggerAt = t;
    }

    public String getId()           { return id; }
    public void   setId(String id)  { this.id = id; }
    public long   getTriggerAt()    { return triggerAt; }
    public void   setTriggerAt(long t) { this.triggerAt = t; }
    public Frequency getFrequency()        { return frequency; }
    public String getMessage() { return message != null ? message : ""; }
}
