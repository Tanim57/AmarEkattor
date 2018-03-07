package com.tanim.year71.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.tanim.year71.HomeActivity;
import com.tanim.year71.VideoInfo;

import java.util.List;

/**
 * Created by tanim on 3/8/2018.
 */
@Dao
public interface VideoDao {

    @Insert
    void insert(VideoInfo videoInfo);

    @Query("Select * from video_entity Where type= :movie")
    public LiveData<List<VideoInfo>> getMovie(String movie);

    @Query("Select * from video_entity Where type= :documentary")
    public LiveData<List<VideoInfo>> getDocumentary(String documentary);
}
