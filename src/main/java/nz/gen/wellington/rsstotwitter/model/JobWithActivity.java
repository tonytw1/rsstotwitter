package nz.gen.wellington.rsstotwitter.model;

public class JobWithActivity {

    private final Job job;
    private final ActivitySummary activity;

    public JobWithActivity(Job job, ActivitySummary activity) {
        this.job = job;
        this.activity = activity;
    }

    public Job getJob() {
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
