package com.tanim.year71;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewPropertyAnimator;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        OnFullscreenListener,YouTubePlayer.PlayerStateChangeListener,YouTubePlayer.PlaybackEventListener {

    /**
     * The duration of the animation sliding up the video in portrait.
     */
    private static final int ANIMATION_DURATION_MILLIS = 300;
    /**
     * The padding between the video list and the video in landscape orientation.
     */
    private static final int LANDSCAPE_VIDEO_PADDING_DP = 5;

    /**
     * The request code when calling startActivityForResult to recover from an API service error.
     */
    private static final int RECOVERY_DIALOG_REQUEST = 1;

    private VideoListFragment listFragment;
    private VideoFragment videoFragment;

    private View videoBox;
    private View closeButton;

    private boolean isFullscreen;
    private AdView mAdView;
    private ViewPager viewPager;
    private static Context mContext;
    private ViewPagerAdapter mAdapter;
    private MovieFragment mMoviewFragment;
    private DocumentaryFragment mDocumentaryFragment;
    private static VideoFragment mVideoFragment;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        //getSupportActionBar().hide();

        setContentView(R.layout.video_list_demo);

        listFragment = (VideoListFragment) getFragmentManager().findFragmentById(R.id.list_fragment);
        videoFragment =
                (VideoFragment) getFragmentManager().findFragmentById(R.id.video_fragment_container);

        videoBox = findViewById(R.id.video_box);
        mContext = getApplicationContext();
        //closeButton = findViewById(R.id.close_button);

        //videoBox.setVisibility(View.VISIBLE);
        //closeButton.setVisibility(View.GONE);

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mMoviewFragment = new MovieFragment(mContext);
        mDocumentaryFragment = new DocumentaryFragment(mContext);

        mAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPager = (ViewPager) findViewById(R.id.view_pager);


        setupViewPager(viewPager);

        mVideoFragment = (VideoFragment) getFragmentManager().findFragmentById(R.id.video_fragment_container);


        layout();

        //setupViewPager(viewPager);

        checkYouTubeApi();
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    /*private void setupViewPager(ViewPager viewPager) {
        swipeAdapter.addFragment(listFragment, "ALL");
        swipeAdapter.addFragment(listFragment, "GP");
        viewPager.setAdapter(swipeAdapter);
    }*/




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

    /**
     * Sets up the layout programatically for the three different states. Portrait, landscape or
     * fullscreen+landscape. This has to be done programmatically because we handle the orientation
     * changes ourselves in order to get fluent fullscreen transitions, so the xml layout resources
     * do not get reloaded.
     */
    private void layout() {
        boolean isPortrait =
                getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;


        //closeButton.setVisibility(isPortrait ? View.VISIBLE : View.GONE);

        if (isFullscreen) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            listFragment.getView().setVisibility(View.GONE);
            listFragment.setLabelVisibility(false);

            videoBox.setTranslationY(0); // Reset any translation that was applied in portrait.
            setLayoutSize(videoFragment.getView(), WRAP_CONTENT, WRAP_CONTENT);
            setLayoutSizeAndGravity(videoBox, MATCH_PARENT, WRAP_CONTENT, Gravity.TOP | Gravity.LEFT);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            listFragment.getView().setVisibility(View.GONE);
            listFragment.setLabelVisibility(true);
           /* MoviewListFragment criminalBusinessFragment = new MoviewListFragment(this);
            getSupportFragmentManager().beginTransaction().add(R.id.layout_list, criminalBusinessFragment).commit();
          */
           setLayoutSize(listFragment.getView(), MATCH_PARENT, MATCH_PARENT);
            setLayoutSize(videoFragment.getView(), MATCH_PARENT, WRAP_CONTENT);
            setLayoutSizeAndGravity(videoBox, MATCH_PARENT, WRAP_CONTENT, Gravity.BOTTOM);
        } /*else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            videoBox.setTranslationY(0); // Reset any translation that was applied in portrait.
            int screenWidth = dpToPx(getResources().getConfiguration().screenWidthDp);
            setLayoutSize(listFragment.getView(), screenWidth / 4, MATCH_PARENT);
            int videoWidth = screenWidth - screenWidth / 4 - dpToPx(LANDSCAPE_VIDEO_PADDING_DP);
            setLayoutSize(videoFragment.getView(), videoWidth, WRAP_CONTENT);
            setLayoutSizeAndGravity(videoBox, videoWidth, WRAP_CONTENT,
                    Gravity.RIGHT | Gravity.CENTER_VERTICAL);
            *//*videoBox.setTranslationY(0); // Reset any translation that was applied in portrait.
            setLayoutSize(videoFragment.getView(), MATCH_PARENT, WRAP_CONTENT);
            setLayoutSizeAndGravity(videoBox, MATCH_PARENT, MATCH_PARENT, Gravity.TOP | Gravity.LEFT);
*//*
        }*/
    }


    public void onClickClose(@SuppressWarnings("unused") View view) {
        listFragment.getListView().clearChoices();
        listFragment.getListView().requestLayout();
        videoFragment.pause();
        ViewPropertyAnimator animator = videoBox.animate()
                .translationYBy(videoBox.getHeight())
                .setDuration(ANIMATION_DURATION_MILLIS);
        runOnAnimationEnd(animator, new Runnable() {
            @Override
            public void run() {
                videoBox.setVisibility(View.INVISIBLE);
            }
        });
    }

    @TargetApi(16)
    private void runOnAnimationEnd(ViewPropertyAnimator animator, final Runnable runnable) {
        if (Build.VERSION.SDK_INT >= 16) {
            animator.withEndAction(runnable);
        } else {
            animator.setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    runnable.run();
                }
            });
        }
    }

    /**
     * A fragment that shows a static list of videos.
     *
     *
     */

    private void setupViewPager(ViewPager viewPager) {

        mAdapter.addFragment(mMoviewFragment, "Movie");
        mAdapter.addFragment(mDocumentaryFragment, "Documentary");
        viewPager.setAdapter(mAdapter);
    }

    @Override
    public void onLoading() {
        Log.d("Player", "Loading");

    }

    @Override
    public void onLoaded(String s) {
        Log.d("Player", "Loaded");
    }

    @Override
    public void onAdStarted() {
        Log.d("Player", "Adloaded");
    }

    @Override
    public void onVideoStarted() {
        Log.d("Player", "Stated");
    }

    @Override
    public void onVideoEnded() {
        Log.d("Player", "End");
    }

    @Override
    public void onError(YouTubePlayer.ErrorReason errorReason) {
        Log.d("Player", "Error");
    }

    @Override
    public void onPlaying() {
        Log.d("Player", "Playing");
    }

    @Override
    public void onPaused() {
        Log.d("Player", "Paused");
        if(mInterstitialAd.isLoaded())
        {
            mInterstitialAd.show();
            mInterstitialAd.loadAd(new AdRequest.Builder().build());
        }
    }

    @Override
    public void onStopped() {
        Log.d("Player", "Stop");
    }

    @Override
    public void onBuffering(boolean b) {
        Log.d("Player", "buffering"+b);
    }

    @Override
    public void onSeekTo(int i) {

    }

    public static final class VideoListFragment extends ListFragment {

        private static final List<VideoEntry> VIDEO_LIST;

        static {
            List<VideoEntry> list = new ArrayList<VideoEntry>();
            list.add(new VideoEntry("Bangla Song \"Dipannita\"", "Bph709EqnHk"));
            list.add(new VideoEntry("Kolkata | Full Video Song | PRAKTAN | Anupam Roy | Shreya Ghoshal | Prosenjit & Rituparna", "YmIhZCNXfJE"));
            list.add(new VideoEntry("কেউ কথা রাখে নি (Keu kotha rakhe ni) | সুনীল গঙ্গোপাধ্যায় | Medha Bandopadhyay recitation", "nhrOuQYU8XI"));
            list.add(new VideoEntry("Deyale Deyale | Minar | Tomar Amar Prem | Siam | Ognila | Mizanur Rahman Aryan |Bangla New Song 2017", "XChdfPIvoIo"));
            list.add(new VideoEntry("কেউ কথা রাখে নি (Keu kotha rakhe ni) | সুনীল গঙ্গোপাধ্যায় | কেউ কথা রাখে নি (Keu kotha rakhe ni) | সুনীল গঙ্গোপাধ্যায় |  Autocompleter Autocompleter Autocompleter Autocompleter Autocompleter" +
                    "Autocompleter Autocompleter Autocompleter Autocompleter Autocompleter Autocompleter", "blB_X38YSxQ"));
            list.add(new VideoEntry("GMail Motion", "Bu927_ul_X0"));
            list.add(new VideoEntry("Translate for Animals", "3I24bSteJpw"));
            list.add(new VideoEntry("aaaaaaaaaaaa", "Bu927_ul_X0"));
            list.add(new VideoEntry("bbbbbbbbbbb", "3I24bSteJpw"));
            list.add(new VideoEntry("ccccccccccc", "Bu927_ul_X0"));
            list.add(new VideoEntry("dddddddddddddddd", "3I24bSteJpw"));
            VIDEO_LIST = Collections.unmodifiableList(list);
        }

        private PageAdapter adapter;
        private View videoBox;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            adapter = new PageAdapter(getActivity(), VIDEO_LIST);
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            //getListView().setBackgroundColor(getResources().getColor(R.color.colorPrimary));

            ListView v = getListView();
            videoBox = getActivity().findViewById(R.id.video_box);
            v.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            VideoFragment videoFragment =
                    (VideoFragment) getFragmentManager().findFragmentById(R.id.video_fragment_container);
            videoFragment.setVideoId(VIDEO_LIST.get(0).videoId);
            setListAdapter(adapter);
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            String videoId = VIDEO_LIST.get(position).videoId;
            VideoFragment videoFragment =
                    (VideoFragment) getFragmentManager().findFragmentById(R.id.video_fragment_container);
            videoFragment.setVideoId(videoId);


            // The videoBox is INVISIBLE if no video was previously selected, so we need to show it now.
            if (videoBox.getVisibility() != View.VISIBLE) {
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    // Initially translate off the screen so that it can be animated in from below.
                    videoBox.setTranslationY(videoBox.getHeight());
                }
                videoBox.setVisibility(View.VISIBLE);
            }

            // If the fragment is off the screen, we animate it in.
            if (videoBox.getTranslationY() > 0) {
                videoBox.animate().translationY(0).setDuration(ANIMATION_DURATION_MILLIS);
            }
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();

            adapter.releaseLoaders();
        }

        public void setLabelVisibility(boolean visible) {
            adapter.setLabelVisibility(visible);
        }

    }


    /**
     * Adapter for the video list. Manages a set of YouTubeThumbnailViews, including initializing each
     * of them only once and keeping track of the loader of each one. When the ListFragment gets
     * destroyed it releases all the loaders.
     */
    private static final class PageAdapter extends BaseAdapter {

        private final List<VideoEntry> entries;
        private final List<View> entryViews;
        private final Map<YouTubeThumbnailView, YouTubeThumbnailLoader> thumbnailViewToLoaderMap;
        private final LayoutInflater inflater;
        private final ThumbnailListener thumbnailListener;

        private boolean labelsVisible;

        public PageAdapter(Context context, List<VideoEntry> entries) {
            this.entries = entries;

            entryViews = new ArrayList<View>();
            thumbnailViewToLoaderMap = new HashMap<YouTubeThumbnailView, YouTubeThumbnailLoader>();
            inflater = LayoutInflater.from(context);
            thumbnailListener = new ThumbnailListener();

            labelsVisible = true;
        }

        public void releaseLoaders() {
            for (YouTubeThumbnailLoader loader : thumbnailViewToLoaderMap.values()) {
                loader.release();
            }
        }

        public void setLabelVisibility(boolean visible) {
            labelsVisible = visible;
            for (View view : entryViews) {
                view.findViewById(R.id.video_name).setVisibility(visible ? View.VISIBLE : View.GONE);
            }
        }

        @Override
        public int getCount() {
            return entries.size();
        }

        @Override
        public VideoEntry getItem(int position) {
            return entries.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            VideoEntry entry = entries.get(position);

            // There are three cases here
            if (view == null) {
                // 1) The view has not yet been created - we need to initialize the YouTubeThumbnailView.
                Log.d("Checking","null view");
                view = inflater.inflate(R.layout.video_list_item, parent, false);
                YouTubeThumbnailView thumbnail = (YouTubeThumbnailView) view.findViewById(R.id.thumbnail);
                thumbnail.setTag(entry.videoId);
                thumbnail.initialize(DeveloperKey.DEVELOPER_KEY, thumbnailListener);
            } else {
                YouTubeThumbnailView thumbnail = (YouTubeThumbnailView) view.findViewById(R.id.thumbnail);
                YouTubeThumbnailLoader loader = thumbnailViewToLoaderMap.get(thumbnail);
                if (loader == null) {
                    // 2) The view is already created, and is currently being initialized. We store the
                    //    current videoId in the tag.
                    thumbnail.setTag(entry.videoId);
                } else {
                    // 3) The view is already created and already initialized. Simply set the right videoId
                    //    on the loader.
                    thumbnail.setImageResource(R.drawable.loading_thumbnail);
                    loader.setVideo(entry.videoId);
                }
            }
            TextView label = view.findViewById(R.id.video_name);
            label.setText(entry.text);
            label.setVisibility(labelsVisible ? View.VISIBLE : View.GONE);
            return view;
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

    // RecyleView

    @SuppressLint("ValidFragment")
    public static final class MovieFragment extends Fragment{

        private RecyclerView mRecyclerView;
        private VideoAdapter mAdapter;
        private Context mContext;

        public MovieFragment(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.fragment_moview_list,container,false);
            mRecyclerView = view.findViewById(R.id.layout_movie_list);

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
            mRecyclerView.setLayoutManager(layoutManager);
            mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext));

            mAdapter = new VideoAdapter(mContext);
            mRecyclerView.setAdapter(mAdapter);

            return view;
        }

    }

    @SuppressLint("ValidFragment")
    public static final class DocumentaryFragment extends Fragment{

        private RecyclerView mRecyclerView;
        private DocumentaryAdapter mAdapter;
        private Context mContext;

        public DocumentaryFragment(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.fragment_documentary_list,container,false);
            mRecyclerView = view.findViewById(R.id.layout_documentary_list);

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
            mRecyclerView.setLayoutManager(layoutManager);
            mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext));

            mAdapter = new DocumentaryAdapter(mContext);
            mRecyclerView.setAdapter(mAdapter);

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
        int pos=-1;



        public DocumentaryAdapter(Context context) {

            this.context = context;
            thumbnailViewToLoaderMap = new HashMap<YouTubeThumbnailView, YouTubeThumbnailLoader>();
            thumbnailListener = new DocumentaryAdapter.ThumbnailListener();
            labelsVisible = true;
        }

        public void releaseLoaders() {
            for (YouTubeThumbnailLoader loader : thumbnailViewToLoaderMap.values()) {
                loader.release();
            }
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.video_list_item,parent,false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(final Holder holder, final int position) {
            if(holder!=null)
            {
                final VideoEntry entry = GlobalVideoList.VIDEO_LIST.get(position);
                //holder.thumbnail.setTag(entry.videoId);

                /*YouTubeThumbnailLoader loader = entry.getTubeThumbnailLoader();
                entry.setTubeThumbnailLoader(loader);*/

                YouTubeThumbnailLoader loader = thumbnailViewToLoaderMap.get(holder.thumbnail);
                if (loader == null) {
                    // 2) The view is already created, and is currently being initialized. We store the
                    //    current videoId in the tag.
                    holder.thumbnail.setTag(entry.videoId);
                    //entry.setTubeThumbnailLoader(loader);
                } else {
                    // 3) The view is already created and already initialized. Simply set the right videoId
                    //    on the loader.
                    holder.thumbnail.setImageResource(R.drawable.loading_thumbnail);
                    loader.setVideo(entry.videoId);
                }

                holder.label.setText(entry.text);

                if(pos==position)
                {
                    holder.itemLayout.setBackgroundResource(R.color.colorAccent);
                }
                else {
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
                        mVideoFragment.setVideoId(entry.videoId);
                        notifyItemChanged(pos);
                        pos = position;
                        holder.itemLayout.setBackgroundResource(R.color.colorAccent);
                    }
                };

            }
        }

        @Override
        public int getItemCount() {
            return GlobalVideoList.VIDEO_LIST.size();
        }

        public static class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
            ItemClickListener itemClickListener;
            YouTubeThumbnailView thumbnail;
            TextView label;
            LinearLayout itemLayout;

            public Holder(View itemView) {
                super(itemView);
                thumbnail = (YouTubeThumbnailView) itemView.findViewById(R.id.thumbnail);
                thumbnail.initialize(DeveloperKey.DEVELOPER_KEY, thumbnailListener);
                label = itemView.findViewById(R.id.video_name);
                itemLayout = itemView.findViewById(R.id.item_layout);
                itemView.setOnClickListener(this);
            }
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClick();
            }

            interface ItemClickListener{
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


    public static final class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.Holder> {


        Map<YouTubeThumbnailView, YouTubeThumbnailLoader> thumbnailViewToLoaderMap;
        static VideoAdapter.ThumbnailListener thumbnailListener;
        Context context;
        boolean labelsVisible;
        int pos =-1;



        public VideoAdapter(Context context) {

            this.context = context;
            thumbnailViewToLoaderMap = new HashMap<YouTubeThumbnailView, YouTubeThumbnailLoader>();
            thumbnailListener = new VideoAdapter.ThumbnailListener();
            labelsVisible = true;
        }

        public void releaseLoaders() {
            for (YouTubeThumbnailLoader loader : thumbnailViewToLoaderMap.values()) {
                loader.release();
            }
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.video_list_item,parent,false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(final Holder holder, final int position) {
            if(holder!=null)
            {
                final VideoEntry entry = GlobalVideoList.VIDEO_LIST.get(position);
                //holder.thumbnail.setTag(entry.videoId);

                /*YouTubeThumbnailLoader loader = entry.getTubeThumbnailLoader();
                entry.setTubeThumbnailLoader(loader);*/

                YouTubeThumbnailLoader loader = thumbnailViewToLoaderMap.get(holder.thumbnail);
                if (loader == null) {
                    // 2) The view is already created, and is currently being initialized. We store the
                    //    current videoId in the tag.
                    holder.thumbnail.setTag(entry.videoId);
                    //entry.setTubeThumbnailLoader(loader);
                } else {
                    // 3) The view is already created and already initialized. Simply set the right videoId
                    //    on the loader.
                    holder.thumbnail.setImageResource(R.drawable.loading_thumbnail);
                    loader.setVideo(entry.videoId);
                }

                holder.label.setText(entry.text);

                if(pos==position)
                {
                    holder.itemLayout.setBackgroundResource(R.color.colorAccent);
                }
                else {
                    holder.itemLayout.setBackgroundColor(android.R.attr.activatedBackgroundIndicator);

                    //holder.itemLayout.setBackgroundResource(R.color.colorWhite);
                }

                holder.itemClickListener = new VideoAdapter.Holder.ItemClickListener() {
                    @Override
                    public void onItemClick() {
                        mVideoFragment.setVideoId(entry.videoId);
                        notifyItemChanged(pos);
                        pos = position;
                        holder.itemLayout.setBackgroundResource(R.color.colorAccent);
                    }
                };


            }
        }

        @Override
        public int getItemCount() {
            return GlobalVideoList.VIDEO_LIST.size();
        }

        public static class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
            ItemClickListener itemClickListener;
            YouTubeThumbnailView thumbnail;
            TextView label;
            LinearLayout itemLayout;

            public Holder(View itemView) {
                super(itemView);
                thumbnail = (YouTubeThumbnailView) itemView.findViewById(R.id.thumbnail);
                thumbnail.initialize(DeveloperKey.DEVELOPER_KEY, thumbnailListener);
                label = itemView.findViewById(R.id.video_name);
                itemLayout = itemView.findViewById(R.id.item_layout);
                itemView.setOnClickListener(this);
            }
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClick();
            }

            interface ItemClickListener{
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
            this.videoId = videoId;
        }

        public void setTubeThumbnailLoader(YouTubeThumbnailLoader tubeThumbnailLoader) {
            this.tubeThumbnailLoader = tubeThumbnailLoader;
        }

        public YouTubeThumbnailLoader getTubeThumbnailLoader() {
            return tubeThumbnailLoader;
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

}
