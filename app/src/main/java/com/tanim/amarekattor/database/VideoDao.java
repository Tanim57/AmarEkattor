package com.tanim.amarekattor.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by tanim on 3/8/2018.
 */
@Dao
public interface VideoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(VideoEntity entity);

    @Query("Select * from video_entity Where id=:id")
    public VideoEntity isExist(String id);

    @Query("Select * from video_entity Where type= :movie")
    public LiveData<List<VideoEntity>> getMovie(String movie);

    @Query("Select * from video_entity Where type= :documentary")
    public LiveData<List<VideoEntity>> getDocumentary(String documentary);

    @Query("Delete from video_entity")
    public void deleteALl();
}
