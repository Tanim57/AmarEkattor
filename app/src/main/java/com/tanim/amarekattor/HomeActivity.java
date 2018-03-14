package com.tanim.amarekattor;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.youtube.player.YouTubeApiServiceUtil;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.OnFullscreenListener;
import com.google.android.youtube.player.YouTubePlayer.OnInitializedListener;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailLoader.ErrorReason;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.tanim.amarekattor.database.ReadFromJson;
import com.tanim.amarekattor.database.VideoEntity;
import com.tanim.amarekattor.database.VideoVideoModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * A sample Activity showing how to manage multiple YouTubeThumbnailViews in an adapter for display
 * in a List. When the list items are clicked, the video is played by using a YouTubePlayerFragment.
 * <p>
 * The demo supports custom fullscreen and transitioning between portrait and landscape without
 * rebuffering.
 */
public final class HomeActivity extends AppCompatActivity implements
        OnFullscreenListener, YouTubePlayer.PlayerStateChangeListener,
        YouTubePlayer.PlaybackEventListener {

    /**
     * The duration of the animation sliding up the video in portrait.
     */

    private int max = 19;
    private int min = 2;
    private int inAd=7,rewordAd =12;
    private static final int ANIMATION_DURATION_MILLIS = 300;
    Random mRandom;
    static int First = 0;
    /**
     * The padding between the video list and the video in landscape orientation.
     */
    private static final int LANDSCAPE_VIDEO_PADDING_DP = 5;

    /**
     * The request code when calling startActivityForResult to recover from an API service error.
     */
    private static final int RECOVERY_DIALOG_REQUEST = 1;

    private VideoFragment videoFragment;

    private View videoBox;
    private boolean isFullscreen;
    private AdView mAdView;
    private ViewPager viewPager;
    private Context mContext;
    private ViewPagerAdapter mAdapter;
    private MovieFragment mMoviewFragment;
    private DocumentaryFragment mDocumentaryFragment;
    private static VideoFragment mVideoFragment;
    private InterstitialAd mInterstitialAd;
    private RewardedVideoAd mRewardedVideoAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        /*getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
        //getSupportActionBar().hide();

        setContentView(R.layout.main_activity);

        videoFragment =
                (VideoFragment) getFragmentManager().findFragmentById(R.id.video_fragment_container);

        videoBox = findViewById(R.id.video_box);
        mContext = getApplicationContext();

        //new InsertMovie().execute();
        new ReadFromJson(this).execute();
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mMoviewFragment = new MovieFragment(mContext);
        Bundle args = new Bundle();

        mDocumentaryFragment = new DocumentaryFragment(mContext);

        mAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPager = (ViewPager) findViewById(R.id.view_pager);


        setupViewPager(viewPager);

        mVideoFragment = (VideoFragment) getFragmentManager().findFragmentById(R.id.video_fragment_container);


        layout();

        //setupViewPager(viewPager);

        checkYouTubeApi();



        loadInterstitialAd();
        loadRewordViedo();
        mRandom = new Random();
    }

    void loadInterstitialAd()
    {
        mInterstitialAd = new InterstitialAd(this);
        //"ca-app-pub-3940256099942544/1033173712"
        mInterstitialAd.setAdUnitId(getString(R.string.interstial));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    void loadRewordViedo()
    {
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        //"ca-app-pub-3940256099942544/5224354917"
        mRewardedVideoAd.loadAd(getString(R.string.reword),
                new AdRequest.Builder().build());
    }

    private void checkYouTubeApi() {
        YouTubeInitializationResult errorReason =
                YouTubeApiServiceUtil.isYouTubeApiServiceAvailable(this);
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
        } else if (errorReason != YouTubeInitializationResult.SUCCESS) {
            String errorMessage =
                    String.format(getString(R.string.error_player), errorReason.toString());
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_DIALOG_REQUEST) {
            // Recreate the activity if user performed a recovery action
            recreate();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        layout();
    }

    @Override
    public void onFullscreen(boolean isFullscreen) {
        this.isFullscreen = isFullscreen;
        layout();
    }

    private void layout() {
        boolean isPortrait =
                getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

        //closeButton.setVisibility(isPortrait ? View.VISIBLE : View.GONE);

        if (isFullscreen) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            videoBox.setTranslationY(0); // Reset any translation that was applied in portrait.
            setLayoutSize(videoFragment.getView(), WRAP_CONTENT, WRAP_CONTENT);
            setLayoutSizeAndGravity(videoBox, MATCH_PARENT, WRAP_CONTENT, Gravity.TOP | Gravity.LEFT);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setLayoutSize(videoFragment.getView(), MATCH_PARENT, WRAP_CONTENT);
            setLayoutSizeAndGravity(videoBox, MATCH_PARENT, WRAP_CONTENT, Gravity.BOTTOM);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        mAdapter.addFragment(mMoviewFragment, "Movie");
        mAdapter.addFragment(mDocumentaryFragment, "Documentary");
        viewPager.setAdapter(mAdapter);
    }

    @Override
    public void onLoading() {

    }

    @Override
    public void onLoaded(String s) {

    }

    @Override
    public void onAdStarted() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
            loadInterstitialAd();
        }
    }

    @Override
    public void onVideoStarted() {

    }

    @Override
    public void onVideoEnded() {
        if(mInterstitialAd.isLoaded())
        {
            mInterstitialAd.show();
            loadInterstitialAd();
        }
    }

    @Override
    public void onError(YouTubePlayer.ErrorReason errorReason) {

    }

    @Override
    public void onPlaying() {

    }

    @Override
    public void onPaused() {

        int r = mRandom.nextInt(max-min+1)+min;

        if(mRewardedVideoAd.isLoaded() && r == rewordAd)
        {
            mRewardedVideoAd.show();
            loadRewordViedo();
        }

        if (mInterstitialAd.isLoaded() && r == inAd) {

            mInterstitialAd.show();
            loadInterstitialAd();
        }
    }

    @Override
    public void onStopped() {

    }

    @Override
    public void onBuffering(boolean b) {

    }

    @Override
    public void onSeekTo(int i) {

    }


    @SuppressLint("ValidFragment")
    public static final class MovieFragment extends Fragment {
        public final static String MOVIE_TAG = "MOVIE_TAG";
        private RecyclerView mRecyclerView;
        private MovieAdapter movieAdapter;
        private Context mContext;
        private VideoVideoModel model;


        public MovieFragment(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.fragment_moview_list, container, false);
            mRecyclerView = view.findViewById(R.id.layout_movie_list);
            model = ViewModelProviders.of(this).get(VideoVideoModel.class);

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
            mRecyclerView.setLayoutManager(layoutManager);
            mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext));

            movieAdapter = new MovieAdapter(mContext);


            model.getMovie().observe(this, new Observer<List<VideoEntity>>() {
                @Override
                public void onChanged(@Nullable List<VideoEntity> videoEntities) {
                    movieAdapter.setVideo(videoEntities);
                }
            });
            mRecyclerView.setAdapter(movieAdapter);

            return view;
        }

    }

    @SuppressLint("ValidFragment")
    public static final class DocumentaryFragment extends Fragment {

        private RecyclerView mRecyclerView;
        private DocumentaryAdapter documentaryAdapter;
        private Context mContext;
        private VideoVideoModel model;

        public DocumentaryFragment(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.fragment_documentary_list, container, false);
            mRecyclerView = view.findViewById(R.id.layout_documentary_list);
            model = ViewModelProviders.of(this).get(VideoVideoModel.class);

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
            mRecyclerView.setLayoutManager(layoutManager);
            mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext));

            documentaryAdapter = new DocumentaryAdapter(mContext);


            model.getDocumentary().observe(this, new Observer<List<VideoEntity>>() {
                @Override
                public void onChanged(@Nullable List<VideoEntity> videoEntities) {
                    documentaryAdapter.setVideo(videoEntities);
                }
            });
            mRecyclerView.setAdapter(documentaryAdapter);
            return view;
        }
    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public static final class DocumentaryAdapter extends RecyclerView.Adapter<DocumentaryAdapter.Holder> {


        Map<YouTubeThumbnailView, YouTubeThumbnailLoader> thumbnailViewToLoaderMap;
        static DocumentaryAdapter.ThumbnailListener thumbnailListener;
        Context context;
        boolean labelsVisible;
        int pos = -1;
        private List<VideoEntity> mDocumentary;


        public DocumentaryAdapter(Context context) {

            this.context = context;
            thumbnailViewToLoaderMap = new HashMap<YouTubeThumbnailView, YouTubeThumbnailLoader>();
            thumbnailListener = new DocumentaryAdapter.ThumbnailListener();
            labelsVisible = true;
        }

        void setVideo(List<VideoEntity> videoEntities) {
            mDocumentary = videoEntities;
            notifyDataSetChanged();
        }

        public void releaseLoaders() {
            for (YouTubeThumbnailLoader loader : thumbnailViewToLoaderMap.values()) {
                loader.release();
            }
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.video_list_item, parent, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(final Holder holder, final int position) {
            if (holder != null) {

                final VideoEntity entity = mDocumentary.get(position);

                YouTubeThumbnailLoader loader = thumbnailViewToLoaderMap.get(holder.thumbnail);
                if (loader == null) {
                    // 2) The view is already created, and is currently being initialized. We store the
                    //    current videoId in the tag.
                    holder.thumbnail.setTag(entity.id);
                    //entry.setTubeThumbnailLoader(loader);
                } else {
                    // 3) The view is already created and already initialized. Simply set the right videoId
                    //    on the loader.
                    holder.thumbnail.setImageResource(R.drawable.loading_thumbnail);
                    loader.setVideo(entity.id);
                }

                holder.label.setText(entity.name);
                holder.tvTime.setText(entity.time);

                if (pos == position) {
                    holder.itemLayout.setBackgroundResource(R.color.colorAccent);
                } else {
                    //holder.itemLayout.setBackgroundResource(R.drawable.t);
                    /*TypedArray a = mContext.obtainStyledAttributes(new int[] { android.R.attr.activatedBackgroundIndicator });
                    int resource = a.getResourceId(0, 0);
                    //first 0 is the index in the array, second is the   default value
                    a.recycle();*/

                    holder.itemLayout.setBackgroundColor(android.R.attr.activatedBackgroundIndicator);
                    //holder.itemLayout.setBackgroundResource(R.color.colorWhite);
                }

                holder.itemClickListener = new Holder.ItemClickListener() {
                    @Override
                    public void onItemClick() {
                        mVideoFragment.setVideoId(entity.id);
                        notifyItemChanged(pos);
                        pos = position;

                        holder.itemLayout.setBackgroundResource(R.color.colorAccent);
                    }
                };

            }
        }

        @Override
        public int getItemCount() {
            //return 0;
            if(mDocumentary!=null)
            {
                return mDocumentary.size();
            }
            return 0;
        }



        public static class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
            ItemClickListener itemClickListener;
            YouTubeThumbnailView thumbnail;
            TextView label;
            TextView tvTime;
            LinearLayout itemLayout;

            public Holder(View itemView) {
                super(itemView);
                thumbnail = (YouTubeThumbnailView) itemView.findViewById(R.id.thumbnail);
                thumbnail.initialize(DeveloperKey.DEVELOPER_KEY, thumbnailListener);
                label = itemView.findViewById(R.id.video_name);
                tvTime = itemView.findViewById(R.id.video_duration);
                itemLayout = itemView.findViewById(R.id.item_layout);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                itemClickListener.onItemClick();
            }

            interface ItemClickListener {
                void onItemClick();
            }
        }


        private final class ThumbnailListener implements
                YouTubeThumbnailView.OnInitializedListener,
                YouTubeThumbnailLoader.OnThumbnailLoadedListener {

            @Override
            public void onInitializationSuccess(
                    YouTubeThumbnailView view, YouTubeThumbnailLoader loader) {
                loader.setOnThumbnailLoadedListener(this);
                thumbnailViewToLoaderMap.put(view, loader);
                view.setImageResource(R.drawable.loading_thumbnail);
                String videoId = (String) view.getTag();
                loader.setVideo(videoId);
            }

            @Override
            public void onInitializationFailure(
                    YouTubeThumbnailView view, YouTubeInitializationResult loader) {
                view.setImageResource(R.drawable.no_thumbnail);
            }

            @Override
            public void onThumbnailLoaded(YouTubeThumbnailView view, String videoId) {

            }

            @Override
            public void onThumbnailError(YouTubeThumbnailView view, ErrorReason errorReason) {
                view.setImageResource(R.drawable.no_thumbnail);
            }
        }
    }


    public static final class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.Holder> {


        Map<YouTubeThumbnailView, YouTubeThumbnailLoader> thumbnailViewToLoaderMap;
        static MovieAdapter.ThumbnailListener thumbnailListener;
        Context context;
        boolean labelsVisible;
        int pos = -1;
        private List<VideoEntity> mMovie;

        public MovieAdapter(Context context) {

            this.context = context;
            thumbnailViewToLoaderMap = new HashMap<YouTubeThumbnailView, YouTubeThumbnailLoader>();
            thumbnailListener = new MovieAdapter.ThumbnailListener();
            labelsVisible = true;
        }

        void setVideo(List<VideoEntity> entity) {
            mMovie = entity;
            notifyDataSetChanged();
        }

        public void releaseLoaders() {
            for (YouTubeThumbnailLoader loader : thumbnailViewToLoaderMap.values()) {
                loader.release();
            }
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.video_list_item, parent, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(final Holder holder, final int position) {

            if (holder != null) {

                final VideoEntity entity = mMovie.get(position);
                if(First == 0)
                {
                    mVideoFragment.setVideoId(entity.id);
                    First++;
                }

                YouTubeThumbnailLoader loader = thumbnailViewToLoaderMap.get(holder.thumbnail);
                if (loader == null) {
                    holder.thumbnail.setTag(entity.id);
                } else {
                    holder.thumbnail.setImageResource(R.drawable.loading_thumbnail);
                    loader.setVideo(entity.id);
                }

                holder.label.setText(entity.name);
                holder.tvTime.setText(entity.time);

                if (pos == position) {
                    holder.itemLayout.setBackgroundResource(R.color.colorAccent);
                } else {
                    holder.itemLayout.setBackgroundColor(android.R.attr.activatedBackgroundIndicator);

                    //holder.itemLayout.setBackgroundResource(R.color.colorWhite);
                }

                holder.itemClickListener = new MovieAdapter.Holder.ItemClickListener() {
                    @Override
                    public void onItemClick() {
                        mVideoFragment.setVideoId(entity.id);
                        notifyItemChanged(pos);
                        pos = position;
                        holder.itemLayout.setBackgroundResource(R.color.colorAccent);
                    }
                };

            }
        }

        @Override
        public int getItemCount() {
            if (mMovie != null) {
                return mMovie.size();
            }
            return 0;
        }

        public static class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
            ItemClickListener itemClickListener;
            YouTubeThumbnailView thumbnail;
            TextView label;
            TextView tvTime;
            LinearLayout itemLayout;

            public Holder(View itemView) {
                super(itemView);
                thumbnail = (YouTubeThumbnailView) itemView.findViewById(R.id.thumbnail);
                thumbnail.initialize(DeveloperKey.DEVELOPER_KEY, thumbnailListener);
                label = itemView.findViewById(R.id.video_name);
                tvTime = itemView.findViewById(R.id.video_duration);
                itemLayout = itemView.findViewById(R.id.item_layout);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                itemClickListener.onItemClick();
            }

            interface ItemClickListener {
                void onItemClick();
            }
        }


        private final class ThumbnailListener implements
                YouTubeThumbnailView.OnInitializedListener,
                YouTubeThumbnailLoader.OnThumbnailLoadedListener {

            @Override
            public void onInitializationSuccess(
                    YouTubeThumbnailView view, YouTubeThumbnailLoader loader) {
                loader.setOnThumbnailLoadedListener(this);
                thumbnailViewToLoaderMap.put(view, loader);
                view.setImageResource(R.drawable.loading_thumbnail);
                String videoId = (String) view.getTag();
                loader.setVideo(videoId);
            }

            @Override
            public void onInitializationFailure(
                    YouTubeThumbnailView view, YouTubeInitializationResult loader) {
                view.setImageResource(R.drawable.no_thumbnail);
            }

            @Override
            public void onThumbnailLoaded(YouTubeThumbnailView view, String videoId) {

            }

            @Override
            public void onThumbnailError(YouTubeThumbnailView view, ErrorReason errorReason) {
                view.setImageResource(R.drawable.no_thumbnail);
            }
        }
    }

    public static final class VideoFragment extends YouTubePlayerFragment
            implements OnInitializedListener {

        private YouTubePlayer player;


        private String videoId;

        public static VideoFragment newInstance() {
            return new VideoFragment();
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            initialize(DeveloperKey.DEVELOPER_KEY, this);
        }

        @Override
        public void onDestroy() {
            if (player != null) {
                player.release();
            }
            super.onDestroy();
        }


        public void setVideoId(String videoId) {
            if (videoId != null && !videoId.equals(this.videoId)) {
                this.videoId = videoId;
                if (player != null) {
                    player.cueVideo(videoId);
                }
            }
        }

        public void pause() {
            if (player != null) {
                player.pause();
            }
        }


        @Override
        public void onInitializationSuccess(Provider provider, YouTubePlayer player, boolean restored) {
            this.player = player;
            player.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
            player.setOnFullscreenListener((HomeActivity) getActivity());
            player.setPlayerStateChangeListener((HomeActivity) getActivity());
            player.setPlaybackEventListener((HomeActivity) getActivity());
            if (!restored && videoId != null) {
                player.cueVideo(videoId);
            }
        }

        @Override
        public void onInitializationFailure(Provider provider, YouTubeInitializationResult result) {
            this.player = null;
        }


    }


    class CustomYoutubePlayer implements YouTubePlayer.PlayerStateChangeListener, YouTubePlayer {

        @Override
        public void release() {

        }

        @Override
        public void cueVideo(String s) {

        }

        @Override
        public void cueVideo(String s, int i) {

        }

        @Override
        public void loadVideo(String s) {

        }

        @Override
        public void loadVideo(String s, int i) {

        }

        @Override
        public void cuePlaylist(String s) {

        }

        @Override
        public void cuePlaylist(String s, int i, int i1) {

        }

        @Override
        public void loadPlaylist(String s) {

        }

        @Override
        public void loadPlaylist(String s, int i, int i1) {

        }

        @Override
        public void cueVideos(List<String> list) {

        }

        @Override
        public void cueVideos(List<String> list, int i, int i1) {

        }

        @Override
        public void loadVideos(List<String> list) {

        }

        @Override
        public void loadVideos(List<String> list, int i, int i1) {

        }

        @Override
        public void play() {

        }

        @Override
        public void pause() {

        }

        @Override
        public boolean isPlaying() {
            return false;
        }

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public boolean hasPrevious() {
            return false;
        }

        @Override
        public void next() {

        }

        @Override
        public void previous() {

        }

        @Override
        public int getCurrentTimeMillis() {
            return 0;
        }

        @Override
        public int getDurationMillis() {
            return 0;
        }

        @Override
        public void seekToMillis(int i) {

        }

        @Override
        public void seekRelativeMillis(int i) {

        }

        @Override
        public void setFullscreen(boolean b) {

        }

        @Override
        public void setOnFullscreenListener(OnFullscreenListener onFullscreenListener) {

        }

        @Override
        public void setFullscreenControlFlags(int i) {

        }

        @Override
        public int getFullscreenControlFlags() {
            return 0;
        }

        @Override
        public void addFullscreenControlFlag(int i) {

        }

        @Override
        public void setPlayerStyle(PlayerStyle playerStyle) {

        }

        @Override
        public void setShowFullscreenButton(boolean b) {

        }

        @Override
        public void setManageAudioFocus(boolean b) {

        }

        @Override
        public void setPlaylistEventListener(PlaylistEventListener playlistEventListener) {

        }

        @Override
        public void setPlayerStateChangeListener(PlayerStateChangeListener playerStateChangeListener) {

        }

        @Override
        public void setPlaybackEventListener(PlaybackEventListener playbackEventListener) {

        }

        @Override
        public void onLoading() {

        }

        @Override
        public void onLoaded(String s) {

        }

        @Override
        public void onAdStarted() {

        }

        @Override
        public void onVideoStarted() {

        }

        @Override
        public void onVideoEnded() {

        }

        @Override
        public void onError(ErrorReason errorReason) {

        }
    }

    public static final class VideoEntry {
        private final String text;
        private final String videoId;
        private YouTubeThumbnailLoader tubeThumbnailLoader;

        public VideoEntry(String text, String videoId) {
            this.text = text;
            videoId = videoId.replace("https://www.youtube.com/watch?v=", "");
            //Log.d("Check", videoId);
            this.videoId = videoId;
        }
    }

    // Utility methods for layouting.

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
    }

    private static void setLayoutSize(View view, int width, int height) {
        LayoutParams params = view.getLayoutParams();
        params.width = width;
        params.height = height;
        view.setLayoutParams(params);
    }

    private static void setLayoutSizeAndGravity(View view, int width, int height, int gravity) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
        //FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        params.width = width;
        params.height = height;
        //params.gravity = gravity;
        view.setLayoutParams(params);
    }

    @Override
    public void onBackPressed() {
        if(isFullscreen)
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            isFullscreen = false;
            layout();
        }
        else {
            super.onBackPressed();
        }

    }
}
