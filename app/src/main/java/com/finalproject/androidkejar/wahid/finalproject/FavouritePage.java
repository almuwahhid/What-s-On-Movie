package com.finalproject.androidkejar.wahid.finalproject;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.finalproject.androidkejar.wahid.finalproject.controller.adapter.FavAdapter;
import com.finalproject.androidkejar.wahid.finalproject.controller.adapter.MovieAdapter;
import com.finalproject.androidkejar.wahid.finalproject.controller.database.Database;
import com.finalproject.androidkejar.wahid.finalproject.model.Movie;

import java.util.ArrayList;

public class FavouritePage extends AppCompatActivity {
    private static final String TAG = FavouritePage.class.getSimpleName();
    SQLiteDatabase db;
    Cursor cursor;
    private ArrayList<Movie> movies;
    private MovieAdapter adapter;
    private boolean phone;
    GridView grid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_page);

        phone = getResources().getBoolean(R.bool.portrait_aja);
        db = (new Database(this)).getWritableDatabase();
        grid = (GridView)findViewById(R.id.grid_fav);
        movies = new ArrayList<>();
        ambilData();
        adapter = new MovieAdapter(FavouritePage.this, movies);
        grid.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        if (phone) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie getMovie = movies.get(position);
                Intent intent = new Intent(getBaseContext(), DetailMovie.class);
                intent.putExtra("id", getMovie.getId());
                intent.putExtra("title", getMovie.getTitle());
                intent.putExtra("bgPath", getMovie.getBgPath());
                intent.putExtra("date", getMovie.getReleaseDate());
                intent.putExtra("overview", getMovie.getSynopsis());
                intent.putExtra("vote", getMovie.getVote_average());
                intent.putExtra("posterPath", getMovie.getPosterUrl());
                startActivity(intent);
            }
        });
        initToolbar();
    }
    private void initToolbar() {
        ActionBar actionbar = getSupportActionBar ();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setTitle("Movie Favorit");
        }
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
    public void ambilData(){
        try{
            cursor = db.rawQuery("SELECT * FROM favorit ORDER BY _id ASC", null);
            Movie movie;
            movies.clear();
            while(cursor.moveToNext()){
                movie = new Movie();
                Log.d(TAG, "ini datanya : "+cursor.getString(cursor.getColumnIndex("id_Movie"))+"\n");
                movie.setId(cursor.getString(cursor.getColumnIndex("id_Movie")));
                movie.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                movie.setBgPath(cursor.getString(cursor.getColumnIndex("bgPath")));
                movie.setReleaseDate(cursor.getString(cursor.getColumnIndex("rilis")));
                movie.setSynopsis(cursor.getString(cursor.getColumnIndex("sinopsis")));
                movie.setVote_average(cursor.getString(cursor.getColumnIndex("rate")));
                movie.setPosterUrl(cursor.getString(cursor.getColumnIndex("pathPoster")));
                movies.add(movie);
            }
        }catch (Exception e){
            e.printStackTrace();
            Log.d(TAG, "error nih");
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        ambilData();
    }
    @Override
    public void onStart() {
        super.onStart();
        ambilData();
    }
}
