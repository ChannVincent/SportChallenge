package chann.vincent.sportchallenge.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import chann.vincent.sportchallenge.R;

/**
 * Created by vincentchann on 14/02/2017.
 */

public class WorkoutFragment extends Fragment {

    protected View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_workout, container, false);
        return view;
    }

}
