package chann.vincent.sportchallenge.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import chann.vincent.sportchallenge.R;
import fr.smartapps.lib.SMAAssetManager;
import fr.smartapps.lib.glide.SMAFile;

/**
 * Created by vincentchann on 14/02/2017.
 */

public class WorkoutFragment extends Fragment {

    protected View view;
    protected TextView titleView;
    protected ImageView imageView;

    protected String title;
    protected String gifUrl;
    protected boolean paused;
    protected SMAAssetManager assetManager;
    static final protected String EXTRA_TITLE = "title";
    static final protected String EXTRA_GIF_URL = "gif_url";
    static final protected String EXTRA_PAUSED = "paused";

    static public WorkoutFragment newInstance(String title, String gifUrl) {
        WorkoutFragment newInstance = new WorkoutFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_TITLE, title);
        bundle.putString(EXTRA_GIF_URL, gifUrl);
        newInstance.setArguments(bundle);
        return newInstance;
    }

    static public WorkoutFragment newInstance(String title, String gifUrl, boolean paused) {
        WorkoutFragment newInstance = new WorkoutFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_TITLE, title);
        bundle.putString(EXTRA_GIF_URL, gifUrl);
        bundle.putBoolean(EXTRA_PAUSED, paused);
        newInstance.setArguments(bundle);
        return newInstance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_workout, container, false);
        initAttributes();
        initTitle();
        initGif();
        return view;
    }

    protected void initAttributes() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            this.title = bundle.getString(EXTRA_TITLE);
            this.gifUrl = bundle.getString(EXTRA_GIF_URL);
            this.paused = bundle.getBoolean(EXTRA_PAUSED);
        }
    }

    protected void initGif() {
        this.imageView = (ImageView) view.findViewById(R.id.image);
        if (this.gifUrl == null || this.imageView == null) {
            return;
        }

        assetManager = new SMAAssetManager(getActivity());
        assetManager.setDefaultStorageType(SMAAssetManager.STORAGE_TYPE_ASSETS);
        assetManager.setExtensionDirectory("shaun_t/gif/");

        if (!paused) {
            Glide.with(this).load(new SMAFile(this.gifUrl, assetManager))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(this.imageView);
        }
        else {
            Glide.with(this).load(new SMAFile(this.gifUrl, assetManager))
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(this.imageView);
        }
    }

    protected void initTitle() {
        this.titleView = (TextView) view.findViewById(R.id.title);
        if (this.title == null || this.titleView == null) {
            return;
        }
        this.titleView.setText(this.title);
    }
}
