package com.example.wallpaper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.AbsListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

     RecyclerView recyclerView;
     WallpaperAdapter wallpaperAdapter;
     List<WallpaperModel> wallpaperModelList;

     int pageNumber = 1;

     Boolean isScrolling = false;
     int currentItems, totalItem,scrollOutItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclearView);
        wallpaperModelList = new ArrayList<>();
        wallpaperAdapter = new WallpaperAdapter(this,wallpaperModelList);
        recyclerView.setAdapter(wallpaperAdapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(gridLayoutManager);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

               currentItems = gridLayoutManager.getChildCount();
               totalItem = gridLayoutManager.getItemCount();
               scrollOutItem = gridLayoutManager.findFirstVisibleItemPosition();

               if (isScrolling && (currentItems+scrollOutItem==totalItem)){
                   isScrolling = false;
                   fetchWallpaper();
               }
            }
        });


        fetchWallpaper();
    }

    public  void  fetchWallpaper(){
        StringRequest request = new StringRequest(Request.Method.GET, "https://api.pexels.com/v1/curated/?page="+pageNumber+"&per_page=80",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {




                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("photos");

                            int length = jsonArray.length();

                            for (int i =0;i<length;i++){
                                JSONObject object = jsonArray.getJSONObject(i);
                                int id = object.getInt("id");

                                JSONObject objectImage = object.getJSONObject("src");
                                String  originalUrl = objectImage.getString("original");
                                String  mediumlUrl = objectImage.getString("medium");


                                WallpaperModel wallpaperModel = new WallpaperModel(id,originalUrl,mediumlUrl);
                                wallpaperModelList.add(wallpaperModel);
                            }

                            wallpaperAdapter.notifyDataSetChanged();
                            pageNumber++;

                        }catch (JSONException e){

                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String > params  = new HashMap<>();
                params.put("Authorization","563492ad6f9170000100000141085dfb54c244eea6af3d135fc29a1d");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);
    }
}