package nz.gen.wellington.rsstotwitter.model;

public class JobWithActivity {

    private final FeedToTwitterJob job;
    private final ActivitySummary activity;

    public JobWithActivity(FeedToTwitterJob job, ActivitySummary activity) {
        this.job = job;
        this.activity = activity;
    }

    public FeedToTwitterJob getJob() {
        return job;
    }

    public ActivitySummary getActivity() {
        return activity;
    }

    @Override
    public String toString() {
        return "JobWithActivity{" +
                "job=" + job +
                ", activity=" + activity +
                '}';
    }
}
