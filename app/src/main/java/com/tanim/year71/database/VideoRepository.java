package com.tanim.year71.database;

import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.provider.MediaStore;

import com.tanim.year71.Constant;
import com.tanim.year71.VideoInfo;

import java.util.List;

/**
 * Created by tanim on 3/8/2018.
 */

public class VideoRepository{
    VideoDao videoDao;
    AppDatabase appDatabase;
    LiveData<List<VideoEntity>> mMovies;
    LiveData<List<VideoEntity>> mDocumentary;

    public VideoRepository()
    {
        appDatabase = AppDatabase.getDatabase();
        videoDao = appDatabase.videoDao();
        mMovies = videoDao.getMovie();
        mDocumentary = videoDao.getDocumentary(Constant.DOCUMENTARY);
    }
    public void insert(VideoEntity entity)
    {
        new insertVideo(videoDao).execute(entity);
    }

    public VideoEntity getData(String id)
    {
        return videoDao.isExist(id);
    }

    public LiveData<List<VideoEntity>> getMovies()
    {
        return mMovies;
    }
    public LiveData<List<VideoEntity>> getDocumentary()
    {
        return mDocumentary;
    }

    public void deleteALL()
    {
        videoDao.deleteALl();
    }

    public static class insertVideo extends AsyncTask<VideoEntity,Void,Void>
    {
        VideoDao dao;
        public insertVideo(VideoDao dao)
        {
            this.dao = dao;
        }
        @Override
        protected Void doInBackground(VideoEntity... entities) {
            dao.insert(entities[0]);
            return null;
        }
    }


}
