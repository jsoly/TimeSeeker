package d.cityaurora.com.libtimeseeker.v;

/**
 * Created by jsj on 2017/10/10.
 */


public abstract class SimpleChangeLinstener implements TimeSeeker.OnTimeChangeLinstener {
    public void onChangeStart(TimeSeeker.SeekerTime time){};
    public void onChanging(TimeSeeker.SeekerTime time){};
    public abstract void onChangeFinish(TimeSeeker.SeekerTime time);
}
