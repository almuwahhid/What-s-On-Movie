package com.finalproject.androidkejar.wahid.finalproject;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ParseException;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.view.View;
import android.widget.Toast;

import com.finalproject.androidkejar.wahid.finalproject.controller.adapter.MovieAdapter;
import com.finalproject.androidkejar.wahid.finalproject.controller.database.Database;
import com.finalproject.androidkejar.wahid.finalproject.controller.http.MovieDb;
import com.finalproject.androidkejar.wahid.finalproject.model.Movie;

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


public class MainActivity extends AppCompatActivity{

    DrawerLayout mDrawerLayout;
    private static final String TAG = MainActivity.class.getSimpleName();
    StringBuilder sb;
    SQLiteDatabase db;
    MovieDb mv = new MovieDb();
    GridView grid;
    private ArrayList<Movie> items;
    private MovieAdapter adapter;
    private ProgressBar progressBar;
    private boolean test = true;
    private boolean phone;
    Cursor cursor;
    Intent i;
    ActionBarDrawerToggle mDrawerToggle;
    Toolbar toolbar;
    Button btn_cari;
    EditText txt_cari;

    HttpURLConnection conn;
    private MovieDBAsyncTask request;
    NavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /** inisialisasi objek **/
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.nav_view) ;
        db = (new Database(this)).getWritableDatabase();
        grid = (GridView)findViewById(R.id.grid);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        items = new ArrayList<>();
        phone = getResources().getBoolean(R.bool.portrait_aja);
        i = new Intent(Intent.ACTION_SEND);
        mv.CATEGORY="now_playing";
        btn_cari = (Button)findViewById(R.id.cari_button);
        txt_cari = (EditText) findViewById(R.id.cari_text);
        update();


        initToolbar(R.string.app_name);
        /** ----- **/
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                if (item.getItemId() == R.id.nav_info) {
                    startActivity(new Intent(getBaseContext(), Info.class));
                }else if(item.getItemId() == R.id.nav_tentang) {
                    startActivity(new Intent(getBaseContext(), Tentang.class));
                }else if(item.getItemId() == R.id.nav_fav) {
                    //Toast.makeText(MainActivity.this, "Maaf, Menu favorit belum tersedia saaat ini.", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getBaseContext(), FavouritePage.class));
                }else if(item.getItemId() == R.id.nav_send) {
                    i.setType("message/rfc822");
                    i.putExtra(Intent.EXTRA_EMAIL, new String[]{"muh.almuwahhid@gmail.com"});
                    i.putExtra(Intent.EXTRA_SUBJECT, "feedback_whats_on_movie");
                    i.putExtra(Intent.EXTRA_TEXT, "Tulis Feedback Anda disini");
                    try {
                        startActivity(Intent.createChooser(i, "Berikan Feedback"));
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(MainActivity.this, "Maaf, Tidak ada Email Client yang terinstal.", Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });

        /** cek size phone **/
        if (phone) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie getMovie = items.get(position);
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

        /** klik cari **/
        btn_cari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txt_cari.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this, "Kata Kunci masih kosong!!!", Toast.LENGTH_LONG).show();
                }else{
                    String caridata = txt_cari.getText().toString().replace(' ', '+');
                    search(caridata);
                }
            }
        });
    }

    private class MovieDBAsyncTask extends AsyncTask<String, Void, Boolean> {

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

                    JSONArray imageArray = jsonData.getJSONArray("results");
                    if(imageArray.length()==0){
                        notifGagal();
                    }

                    items = new ArrayList<>();
                    for (int i = 0; i < imageArray.length(); i++) {
                        Movie movie = new Movie();
                        JSONObject object2 = imageArray.getJSONObject(i);
                        movie.setTitle(object2.getString("title"));
                        movie.setId(object2.getString("id"));
                        movie.setPosterUrl(object2.getString("poster_path"));
                        movie.setReleaseDate(object2.getString("release_date"));
                        movie.setSynopsis(object2.getString("overview"));
                        movie.setVote_average(object2.getString("vote_average"));
                        movie.setBgPath(object2.getString("backdrop_path"));
                        movie.setPosterUrl(object2.getString("poster_path"));
                        items.add(movie);
                        Log.d(TAG, "tes : " + movie.getPosterUrl() + "\n");
                    }
        //            adapter.notifyDataSetChanged();
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
                Log.d(TAG, "onPostExecute() returned: " + items.toString());
                if (!result) {
                    progressBar.setVisibility(View.GONE);
                    ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(getBaseContext().CONNECTIVITY_SERVICE);
                    if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {

                    }else{
                        Toast.makeText(MainActivity.this, "Sepertinya kamu tidak memiliki koneksi internet", Toast.LENGTH_LONG).show();
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                }
            adapter = new MovieAdapter(MainActivity.this, items);
            grid.setAdapter(adapter);
        }
    }

    private void notifGagal() {
        Toast.makeText(this,
                "Tidak Ditemukan data, coba lagi",
                Toast.LENGTH_SHORT).
                show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.content_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == R.id.action_popular) {
                mv.CATEGORY = "popular";
                initToolbar(R.string.popular);
                update();
                return true;
            }else if(id == R.id.action_topRated){
                mv.CATEGORY = "top_rated";
                initToolbar(R.string.top);
                update();
                return true;
            }
            else if(id == R.id.action_nowPlaying){
                mv.CATEGORY = "now_playing";
                initToolbar(R.string.nowPlaying);
                update();
                return true;
            }
            else if(id == R.id.action_upComing){
                mv.CATEGORY = "upcoming";
                initToolbar(R.string.upcoming);
                update();
                return true;
            }

        return super.onOptionsItemSelected(item);
    }

    void update(){
        progressBar.setVisibility(View.VISIBLE);
        request = new MovieDBAsyncTask();
        final String link = mv.URL+"/"+mv.CATEGORY+"?&api_key="+mv.API_KEY;
        new Thread(new Runnable() {
            public void run() {
                try {
                    request.execute(link).get(10000, TimeUnit.MILLISECONDS);
                } catch (TimeoutException | ExecutionException | InterruptedException | CancellationException e) {
                    request.cancel(true);
                    test = false;
                    if (conn != null)
                        conn.disconnect();
                }
            }
        }).start();
    }
    void search(String keyword){
        progressBar.setVisibility(View.VISIBLE);
        request = new MovieDBAsyncTask();
        final String link = "http://api.themoviedb.org/3/search/movie?query="+keyword+"&api_key="+mv.API_KEY;
        new Thread(new Runnable() {
            public void run() {
                try {
                    request.execute(link).get(10000, TimeUnit.MILLISECONDS);
                } catch (TimeoutException | ExecutionException | InterruptedException | CancellationException e) {
                    request.cancel(true);
                    test = false;
                    if (conn != null)
                        conn.disconnect();
                }
            }
        }).start();
    }

    private void initToolbar(int title) {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,title,
                title);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        /**ActionBar actionbar = getSupportActionBar ();
            actionbar.setTitle(title);**/
    }
    @Override
    public void onStart() {
        super.onStart();
        /**mv.CATEGORY = "popular";
        update();**/
    }
}