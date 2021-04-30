package nz.gen.wellington.rsstotwitter.model;

public class ActivitySummary {

    private final long lastHour;
    private final long lastTwentyFourHours;

    public ActivitySummary(long lastHour, long lastTwentyFourHours) {
        this.lastHour = lastHour;
        this.lastTwentyFourHours = lastTwentyFourHours;
    }

    public long getLastHour() {
        return lastHour;
    }

    public long getLastTwentyFourHours() {
        return lastTwentyFourHours;
    }

    @Override
    public String toString() {
        return "ActivitySummary{" +
                "lastHour=" + lastHour +
                ", lastTwentyFourHours=" + lastTwentyFourHours +
                '}';
    }
}
