package com.tanim.amarekattor.database;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.tanim.amarekattor.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by tanim on 3/14/2018.
 */

public class ReadFromJson extends AsyncTask {

    private Context mContext;
    VideoVideoModel model;

    public ReadFromJson(Context context)
    {
        this.mContext = context;
        model = new VideoVideoModel();
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        String json = null;
        try {
            InputStream is = mContext.getAssets().open("Muktijuddho.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            JSONObject object = new JSONObject(json);
            JSONArray jsonArray = object.getJSONArray("Muktijuddho");
            for (int i=0;i<jsonArray.length();i++)
            {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                VideoEntity entity = new VideoEntity();
                String mark = jsonObject.getString("mark");
                String title = jsonObject.getString("title");
                String link = jsonObject.getString("link");
                link = link.replace("https://www.youtube.com/watch?v=","");
                String duration = jsonObject.getString("Duration");

                entity.id = link;
                entity.name = title;
                entity.type = mark;
                entity.time = duration;
                VideoEntity existEntity = model.getData(entity.id);
                //if()
                if (existEntity == null) {
                    model.insert(entity);
                } else {
                    if (!(existEntity.id.equals(entity.id) && existEntity.name.equals(entity.name)
                            && entity.type.equals(existEntity.type) && entity.time.equals(existEntity.time))) {
                        model.insert(entity);
                    }
                }
                //Log.d("Check",entity.id+" "+entity.name );
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
