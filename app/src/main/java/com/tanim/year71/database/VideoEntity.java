package com.tanim.year71.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;

/**
 * Created by tanim on 3/8/2018.
 */
@Entity(tableName = "video_entity")
public class VideoEntity {
    @ColumnInfo(name = "name")
    public String name;
    @ColumnInfo(name = "id")
    public String id;
    @ColumnInfo(name = "time")
    public String time;
    @ColumnInfo(name = "type")
    public String type;
}
