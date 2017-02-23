package bosch.mx.lud1ga.pushcontextflybits270;

import com.google.gson.annotations.SerializedName;

/**
 * Created by LUD1GA on 14/12/2016.
 */

public class FlybitsBeacon {

    @SerializedName("majorID")
    String majorId;
    @SerializedName("minorID")
    String minorId;
    @SerializedName("uuid")
    String uuid;
    @SerializedName("type")
    String type;
    @SerializedName("inRange")
    boolean inRange;

    public String getMajorId() {
        return majorId;
    }

    public void setMajorId(String majorId) {
        this.majorId = majorId;
    }

    public String getMinorId() {
        return minorId;
    }

    public void setMinorId(String minorId) {
        this.minorId = minorId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isInRange() {
        return inRange;
    }

    public void setInRange(boolean inRange) {
        this.inRange = inRange;
    }
    @Override
    public String toString(){
        return
                "majorID "+majorId+
                        " minorID "+minorId+
                        " UUID "+uuid+
                        " inRange "+inRange;
    }
}
