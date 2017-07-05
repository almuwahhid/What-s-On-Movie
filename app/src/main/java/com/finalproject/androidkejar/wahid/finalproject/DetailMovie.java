package com.finalproject.androidkejar.wahid.finalproject;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ParseException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;



import com.finalproject.androidkejar.wahid.finalproject.controller.adapter.MovieAdapter;
import com.finalproject.androidkejar.wahid.finalproject.controller.database.Database;
import com.finalproject.androidkejar.wahid.finalproject.controller.http.MovieDb;
import com.finalproject.androidkejar.wahid.finalproject.model.Movie;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DetailMovie extends AppCompatActivity {
    private static final String TAG = DetailMovie.class.getSimpleName();
    String id, title, bgPath, tanggal, overview, vote, posterPath, trailerPath;
    SQLiteDatabase db;
    private ArrayList<Movie> movies;
    Cursor cursor;
    HttpURLConnection conn;
    StringBuilder sb;
    private VideoAsyntax request;
    MovieDb mv = new MovieDb();

    @Bind(R.id.rate)TextView rate;
    @Bind(R.id.sinopsis)TextView sinopsis;
    @Bind(R.id.rilis)TextView rilis;
    @Bind(R.id.header)ImageView imageView;
    @Bind(R.id.fab)FloatingActionButton fab;
    @Bind(R.id.imageView)ImageView trailer;
    @Bind(R.id.imageButton)Button imageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_movie);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        title = intent.getStringExtra("title");
        bgPath = intent.getStringExtra("bgPath");
        tanggal = intent.getStringExtra("date");
        overview = intent.getStringExtra("overview");
        vote = intent.getStringExtra("vote");
        posterPath = intent.getStringExtra("posterPath");
        db = (new Database(this)).getWritableDatabase();

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, title);
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, trailerPath);
                startActivity(Intent.createChooser(sharingIntent, "Bagikan Melalui"));
            }
        });

        if(!cekFavorit())
            fab.setImageResource(R.drawable.heart_empty);
        else if(cekFavorit())
            fab.setImageResource(R.drawable.heart);


        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapse_toolbar);
        collapsingToolbar.setTitle(title);
        rate.setText(vote);
        sinopsis.setText(overview);
        rilis.setText(tanggal);
        Picasso.with(this)
                .load("https://image.tmdb.org/t/p/w185"+bgPath)
                .placeholder(R.drawable.movie_default)
                .error(R.drawable.movie_default)
                .noFade()
                .into(imageView);

        initToolbar();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cekFavorit()) {
                    fab.setImageResource(R.drawable.heart);
                    ContentValues values = new ContentValues();
                    values.put("id_Movie", id);
                    values.put("title", title);
                    values.put("bgPath", bgPath);
                    values.put("rilis", tanggal);
                    values.put("sinopsis", overview);
                    values.put("rate", vote);
                    values.put("trailer", trailerPath);
                    values.put("pathPoster", posterPath);

                    db.insert("favorit", "_id", values);
                } else if (cekFavorit()) {
                    fab.setImageResource(R.drawable.heart_empty);
                    db.execSQL("DELETE FROM favorit WHERE id_Movie= " + id);
                }
            }
        });
        trailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(trailerPath));
                startActivity(intent);
            }
        });
        update(id);

    }
    private void initToolbar() {
        ActionBar actionbar = getSupportActionBar ();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
        }
    }
    private boolean cekFavorit(){
        int num = 0;
        try{
            cursor = db.rawQuery("SELECT * FROM favorit ORDER BY _id ASC", null);
            while(cursor.moveToNext()){
                if(cursor.getString(cursor.getColumnIndex("id_Movie")).equals(id))
                    num++;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        if(num==0)
            return false;
        else
            return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return (super.onOptionsItemSelected(item));
    }
    private class VideoAsyntax extends AsyncTask<String, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... urls) {
            try {
                Log.d(TAG, "kamprett : \n");
                URL url = new URL(urls[0]);
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(10000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();

                int status = conn.getResponseCode();
                if (status == 200) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    br.close();
                    Log.d(TAG, "kamprett : \n"+sb.toString());
                    JSONObject jsonData = new JSONObject(sb.toString());
                    JSONObject video = jsonData.getJSONObject("videos");
                    JSONArray imageArray = video.getJSONArray("results");
                    if(imageArray.length()==0){
                        notifGagal();
                    }


                    for (int i = 0; i < imageArray.length(); i++) {
                        JSONObject object = imageArray.getJSONObject(i);
                        trailerPath = "https://www.youtube.com/watch?v="+object.getString("key");
                        Log.d(TAG, "tes : " +trailerPath+ "\n");
                    }
                }
            }catch (MalformedURLException e) {
                e.printStackTrace();
            }catch (ParseException | IOException | JSONException e) {
                if (conn != null)
                    conn.disconnect();
                e.printStackTrace();
            } finally {
                if (conn != null)
                    conn.disconnect();
            }
            return false;
        }
        protected void onPostExecute(Boolean result) {
            // is added checks if we are still on the same view, if we don't do this check the program will cra
            Log.d(TAG, "onPostExecute() returned: ");
        }
    }

    void update(String id){
        request = new VideoAsyntax();
        final String link = mv.URL+"/"+id+"?&api_key="+mv.API_KEY+"&append_to_response=videos";
        new Thread(new Runnable() {
            public void run() {
                try {
                    request.execute(link).get(10000, TimeUnit.MILLISECONDS);
                } catch (TimeoutException | ExecutionException | InterruptedException | CancellationException e) {
                    request.cancel(true);
                    if (conn != null)
                        conn.disconnect();
                }
            }
        }).start();
    }

    private void notifGagal() {
        Toast.makeText(this,
                "Tidak Ditemukan data, coba lagi",
                Toast.LENGTH_SHORT).
                show();
    }
}
