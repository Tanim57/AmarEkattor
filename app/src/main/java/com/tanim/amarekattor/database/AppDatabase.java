package com.tanim.amarekattor.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;

import com.tanim.amarekattor.App;

/**
 * Created by tanim on 3/8/2018.
 */
@Database(entities = {VideoEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
      public static AppDatabase database;
      public abstract VideoDao videoDao();

    public static AppDatabase getDatabase()
      {
          if(database == null)
          {
              synchronized (AppDatabase.class)
              {
                  if(database==null)
                      database = Room.databaseBuilder(App.getContext(),
                              AppDatabase.class, "video").build();
              }
          }
          return database;
      }
}
