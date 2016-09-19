package com.finalproject.androidkejar.wahid.finalproject.controller.adapter;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.finalproject.androidkejar.wahid.finalproject.R;
import com.finalproject.androidkejar.wahid.finalproject.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by gueone on 6/27/2016.
 */
public class FavAdapter extends ArrayAdapter<Movie> {
    private static final String TAG = FavAdapter.class.getSimpleName();
    private final Context context;
    private ArrayList<Movie> gridItems;
    private LayoutInflater inflater;
    public FavAdapter(Context context, ArrayList<Movie> data) {
        super(context, R.layout.grid_row_fav, data);
        this.context = context;
        this.gridItems = data;
    }
    @Override
    public long getItemId(int position) {
        return gridItems.size();
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder viewHolder = new ViewHolder();
        if (convertView == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.grid_row_fav, parent, false);
        } else {
            view = convertView;
        }
        viewHolder.imageView = (ImageView) view.findViewById(R.id.image);
        for (int h = 0;h<6;h++){
            Log.d(TAG, "getView() returned: " + gridItems.get(h).getPosterUrl());;
        }
        String url = gridItems.get(position).getPosterUrl();
        if (url != null) {
            Picasso.with(context)
                    .load("https://image.tmdb.org/t/p/w185"+url)
                    .placeholder(R.drawable.movie_default)
                    .error(R.drawable.movie_default)
                    .noFade()
                    .into(viewHolder.imageView);
        }
        return view;
    }
    public class ViewHolder {
        ImageView imageView;
    }
}
