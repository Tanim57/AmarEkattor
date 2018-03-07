package com.tanim.year71.database;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.tanim.year71.VideoInfo;

import java.util.List;

/**
 * Created by tanim on 3/8/2018.
 */

public class VideoVideoModel extends ViewModel {
    public LiveData<List<VideoInfo>> mLiveData;
    VideoRepository videoRepository;
    public VideoVideoModel(VideoRepository videoRepository)
    {
        this.videoRepository = videoRepository;
    }
    private LiveData<List<VideoInfo>> getMovie(String movie)
    {
        return videoRepository.getMovie(movie);
    }

    private LiveData<List<VideoInfo>> getDocumentary(String documentary)
    {
        return videoRepository.getMovie(documentary);
    }

}
