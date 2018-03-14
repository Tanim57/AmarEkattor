package com.tanim.amarekattor.database;

import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.tanim.amarekattor.Constant;

import java.util.List;

/**
 * Created by tanim on 3/8/2018.
 */

public class VideoRepository{
    private VideoDao videoDao;
    private AppDatabase appDatabase;
    private LiveData<List<VideoEntity>> mMovies;
    private LiveData<List<VideoEntity>> mDocumentary;

    public VideoRepository()
    {
        appDatabase = AppDatabase.getDatabase();
        videoDao = appDatabase.videoDao();
        mDocumentary = videoDao.getDocumentary("2");
        mMovies = videoDao.getMovie("1");
    }
    void insert(VideoEntity entity)
    {
        new insertVideo(videoDao).execute(entity);
    }

    VideoEntity getData(String id)
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
