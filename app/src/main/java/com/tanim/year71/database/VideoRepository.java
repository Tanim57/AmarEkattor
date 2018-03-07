package com.tanim.year71.database;

import android.arch.lifecycle.LiveData;

import com.tanim.year71.VideoInfo;

import java.util.List;

/**
 * Created by tanim on 3/8/2018.
 */

public class VideoRepository implements VideoDao {
    VideoDao videoDao;
    AppDatabase appDatabase = AppDatabase.getDatabase();

    public LiveData<List<VideoInfo>> getMovie(String movie)
    {
        return videoDao.getMovie(movie);
    }
    public LiveData<List<VideoInfo>> getDocumentary(String documentary)
    {
        return videoDao.getMovie(documentary);
    }
    public void insert(VideoInfo videoInfo)
    {
        videoDao.insert(videoInfo);
    }
}
