package bosch.mx.lud1ga.pushcontextflybits270;

import com.flybits.core.api.context.v2.plugins.activity.ActivityData;

/**
 * Created by LUD1GA on 14/06/2017.
 *
 */

public class FlybitsUserActivity {

    private ActivityData.ActivityType activityType;
    private int confidence;

    public FlybitsUserActivity(ActivityData.ActivityType activityType, int confidence){
        this.activityType = activityType;
        this.confidence = confidence;
    }

    public ActivityData.ActivityType getActivityType() {
        return activityType;
    }

    public void setActivityType(ActivityData.ActivityType activityType) {
        this.activityType = activityType;
    }

    public int getConfidence() {
        return confidence;
    }

    public void setConfidence(int confidence) {
        this.confidence = confidence;
    }
}
