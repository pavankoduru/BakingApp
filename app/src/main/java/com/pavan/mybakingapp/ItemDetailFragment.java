package com.pavan.mybakingapp;

import android.app.Activity;
import android.net.Uri;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.pavan.mybakingapp.POJOs.StepsModel;


/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link ItemListActivity}
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
 */
public class ItemDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    String[] details;
    SimpleExoPlayerView exoPlayerView;
    SimpleExoPlayer simpleExoPlayer;
    private boolean playWhenReady=true;
    private long playbackPosition=0;
    private int currentWindow=0;

    /**
     * The dummy content this fragment is presenting.
     */
    //private StepsModel mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            details=(getArguments().getStringArray(ARG_ITEM_ID));
            assert details != null;
            if(details[2]==null)
            {
                Toast.makeText(getActivity(), "No Video Available", Toast.LENGTH_SHORT).show();
            }

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(details[1]);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_detail, container, false);



        // Show the dummy content as text in a TextView.
        if (details!= null) {
            ((TextView) rootView.findViewById(R.id.item_detail)).setText(details[0]);
            exoPlayerView=rootView.findViewById(R.id.exoplayerview);



           // MediaSource mediaSource=new ExtractorMediaSource.Factory(factory).createMediaSource(Uri.parse(details[2]));
            RenderersFactory renderersFactory=new DefaultRenderersFactory(getContext());
            LoadControl loadControl=new DefaultLoadControl();
            TrackSelector trackSelector=new DefaultTrackSelector();
            DataSource.Factory factory=new DefaultDataSourceFactory(getContext(), Util.getUserAgent(getContext(),getActivity().getPackageName()));
            MediaSource mediaSource=new ExtractorMediaSource(Uri.parse(details[2]),factory,new DefaultExtractorsFactory(),null,null);

            Log.i("url",details[2]);

            if(details[2].equals(""))
            {
                Toast.makeText(getActivity(), "No Video Available", Toast.LENGTH_SHORT).show();

                exoPlayerView.setVisibility(View.GONE);
            }else {
                simpleExoPlayer= ExoPlayerFactory.newSimpleInstance(renderersFactory,trackSelector,loadControl);
                simpleExoPlayer.prepare(mediaSource);
                exoPlayerView.setVisibility(View.VISIBLE);

            }
            settingplayer();



        }



        return rootView;
    }
    private void releasePlayer() {
        if (simpleExoPlayer != null) {
            playbackPosition = simpleExoPlayer.getCurrentPosition();
            currentWindow = simpleExoPlayer.getCurrentWindowIndex();
            playWhenReady = simpleExoPlayer.getPlayWhenReady();
            simpleExoPlayer.release();
            simpleExoPlayer = null;
        }
    }
    private void settingplayer()
    {
        if(details[2].equals(""))
        {
            Toast.makeText(getActivity(), "No Video Available", Toast.LENGTH_SHORT).show();

            exoPlayerView.setVisibility(View.GONE);
        }else {
            exoPlayerView.setPlayer(simpleExoPlayer);
            simpleExoPlayer.setPlayWhenReady(playWhenReady);
            simpleExoPlayer.seekTo(currentWindow,playbackPosition);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            settingplayer();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if ((Util.SDK_INT <= 23 || simpleExoPlayer == null)) {
            settingplayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }
}
