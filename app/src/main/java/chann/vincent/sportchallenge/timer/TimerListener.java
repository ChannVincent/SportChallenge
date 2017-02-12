package chann.vincent.sportchallenge.timer;

/**
 * Created by vincentchann on 12/02/2017.
 */

public interface TimerListener {
    public void progress(int timer);
    public void onFinish();
}
