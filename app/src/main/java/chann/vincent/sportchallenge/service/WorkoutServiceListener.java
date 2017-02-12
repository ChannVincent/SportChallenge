package chann.vincent.sportchallenge.service;

/**
 * Created by vincentchann on 12/02/2017.
 */

public interface WorkoutServiceListener {

    public void play();
    public void pause();
    public void next();
    public void previous();
    public void timer(int timer, int maxTimer);

}
