package com.example.b07_project21.ui.reminder;

public class Reminder {
    public enum Frequency { ONCE, DAILY, WEEKLY, MONTHLY }
    private String id;
    private long triggerAt;
    private String message;

    // ── NEW schedule fields ───────────────────────────────────────────────
    private Frequency frequency = Frequency.ONCE;   // default = one-shot

    /** for WEEKLY: 1 = Sunday … 7 = Saturday; else 0 */
    private int dayOfWeek  = 0;
    /** for MONTHLY: 1-31; else 0 */
    private int dayOfMonth = 0;

    public Reminder() {}  // Firestore needs no-arg ctor

    public Reminder(String id, long t) {
        this.id = id;
        this.triggerAt = t;
    }

    /** new constructor that lets us set the notification text */
    public Reminder(String id, long t, String msg) {
        this.id        = id;
        this.triggerAt = t;
        this.message   = msg != null ? msg : "";
    }

    /** new constructor to set text + freq */
    public Reminder(String id, long t, String msg, Frequency freq) {
        this.id        = id;
        this.triggerAt = t;
        this.message   = msg  != null ? msg  : "";
        this.frequency = freq != null ? freq : Frequency.ONCE;
    }

    /** Needed when we edit an existing reminder */
    public void setMessage(String msg) {
        this.message = msg != null ? msg : "";
    }

    public String getId()           { return id; }
    public void   setId(String id)  { this.id = id; }
    public long   getTriggerAt()    { return triggerAt; }
    public void   setTriggerAt(long t) { this.triggerAt = t; }
    public Frequency getFrequency()        { return frequency; }
    public String getMessage() { return message != null ? message : ""; }

    public void setFrequency(Frequency freq) {
        this.frequency = freq != null ? freq : Frequency.ONCE;
    }
}