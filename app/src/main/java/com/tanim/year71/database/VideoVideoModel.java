package com.tanim.year71.database;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.tanim.year71.App;
import com.tanim.year71.VideoInfo;

import java.util.List;

/**
 * Created by tanim on 3/8/2018.
 */

public class VideoVideoModel extends ViewModel {
    public LiveData<List<VideoInfo>> mLiveData;
    private VideoRepository videoRepository;
    public VideoVideoModel()
    {
        super();
        videoRepository = new VideoRepository();
    }
    public void insert(VideoEntity entity)
    {
        videoRepository.insert(entity);
    }

    public VideoEntity getData(String id)
    {
        return videoRepository.getData(id);
    }

    public LiveData<List<VideoEntity>> getMovie()
    {
        return videoRepository.getMovies();
    }
    public LiveData<List<VideoEntity>> getDocumentary()
    {
        return videoRepository.getDocumentary();
    }
    public void deleteAll()
    {
        videoRepository.deleteALL();
    }

}
