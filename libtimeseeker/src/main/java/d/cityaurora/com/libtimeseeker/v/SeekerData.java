package d.cityaurora.com.libtimeseeker.v;

/**
 * Created by jsj on 2017/10/10.
 */

public class SeekerData {
    private int startTime;
    private int endTime;

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public SeekerData(int startTime, int endTime) {

        this.startTime = startTime;
        this.endTime = endTime;
    }
}
