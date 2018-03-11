package com.tanim.year71.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;


/**
 * Created by tanim on 3/8/2018.
 */
@Entity(tableName = "video_entity")
public class VideoEntity {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    public String id;
    @ColumnInfo(name = "name")
    public String name;
    @ColumnInfo(name = "time")
    public String time;
    @ColumnInfo(name = "type")
    public String type;
}
