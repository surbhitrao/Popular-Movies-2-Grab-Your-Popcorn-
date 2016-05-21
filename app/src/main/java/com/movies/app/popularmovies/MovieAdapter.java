package com.movies.app.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;
/**
 * Created by Surbhit
 * */


public class MovieAdapter extends ArrayAdapter {


    public MovieAdapter(Context context, int grid_item_movies, int resource, List<MovieObject> listmovie) {
        super(context,R.layout.fragment_main,resource);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

            MovieObject movieObj= (MovieObject) getItem(position);
        String poster_path=movieObj.poster_path;
        String baseImageUrl="http://image.tmdb.org/t/p/w500/";
        if(convertView==null)
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.grid_item_movies, parent, false);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.grid_item_movies_imageview);
            Picasso.with(getContext()).load(baseImageUrl + poster_path).into(imageView);
            return convertView;
    }
}
