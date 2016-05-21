package com.movies.app.popularmovies;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.movies.app.popularmovies.Data.MovieContract;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class DetailActivityFragment extends Fragment
{
    boolean isFavourite=false;
    public String share_key;
    ShareActionProvider mShareActionProvider;
    String baseUrlImage="http://image.tmdb.org/t/p/w500/";

    String baseUrl="http://www.youtube.com/watch?v=";

    String LOG_TAG= DetailActivityFragment.class.getSimpleName();
    String reviewStr;

    Button review, w_trailer;
    public static MovieObject movieRecieved;


    public DetailActivityFragment()
    {
        setHasOptionsMenu(true);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.fragment_detail, menu);


    }
FloatingActionButton updateFavouriteButton;
ImageView movieBackdrop,moviePoster;
    TextView movie_vote_average,movie_release,review_view,overview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        updateFavouriteButton = (FloatingActionButton) rootView.findViewById(R.id.favbtn);

        moviePoster = (ImageView) rootView.findViewById(R.id.movie_poster);
        movie_vote_average = (TextView) rootView.findViewById(R.id.movie_vote_average);
        movie_release = (TextView) rootView.findViewById(R.id.movie_release);
        overview = (TextView) rootView.findViewById(R.id.movie_overview);


        review = (Button) rootView.findViewById(R.id.button2);
        review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                review(v);
            }
        });
        w_trailer = (Button) rootView.findViewById(R.id.button);

        w_trailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trailer(v);
            }
        });





        String url = "content://com.movies.app.popularmovies.app/movie";

        Uri fetchUri = Uri.parse(url);
        Cursor findQuery = getContext().getContentResolver().query(fetchUri, null, "movie_id=" + movieRecieved.id, null, null);
        if (findQuery.moveToFirst()) {
            isFavourite = true;
        }
        if (isFavourite == true) {
            updateFavouriteButton.setImageResource(R.mipmap.like);

        } else {
            updateFavouriteButton.setImageResource(R.mipmap.dislike);

        }

        updateFavouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url="content://com.movies.app.popularmovies.app/movie";

                Uri fetchUri=Uri.parse(url);
                if(isFavourite==false) {

                    ContentValues values = new ContentValues();
                    values.put(MovieContract.MovieEntry.COLUMN_TITLE, movieRecieved.title);
                    values.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movieRecieved.overview);
                    values.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movieRecieved.release_date);
                    values.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, movieRecieved.vote_average);
                    values.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, movieRecieved.poster_path);
                    values.put(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH, movieRecieved.backdrop_path);
                    values.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movieRecieved.id);
                    Uri uri = getContext().getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, values);

                    Toast.makeText(getContext(), "Added TO Favourites...", Toast.LENGTH_SHORT).show();
                    updateFavouriteButton.setImageResource(R.mipmap.like);
                    isFavourite=true;
                    String result = "";
                    Cursor c = getContext().getContentResolver().query(fetchUri, null, null, null, null);
                    if (c.moveToFirst()) {
                        do {
                            {
                                result = "S. NUMBER           : " + c.getString(c.getColumnIndex(MovieContract.MovieEntry._ID))
                                        + "\nMOVIE ID             : " + c.getString(c.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID))
                                        + "\nMOVIE TITLE          : " + c.getString(c.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE))
                                    +"\nMOVIE OVERVIEW       : "+c.getString(c.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW))
                                    +"\nMOVIE RELEASE DATE   : "+c.getString(c.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE))
                                    +"\nMOVIE VOTE           : "+c.getString(c.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE))
                                    +"\nMOVIE POSTER         : "+c.getString(c.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH))
                                    +"\nMOVIE BACKDROP       : "+c.getString(c.getColumnIndex(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH)).toString();

                                Log.v("RESULT_QUERY VERBOSE", result);
                            }
                        } while (c.moveToNext());
                    }
                }
                else
                {
                    Toast.makeText(getContext(),"Movie Successfully Removed...",Toast.LENGTH_SHORT).show();

                    updateFavouriteButton.setImageResource(R.mipmap.dislike);
                    isFavourite=false;
                }

            }
        });


        setContent(movieRecieved);



        return rootView;


    }

    public void trailer(View view) {
        new AsyncHttpTask().execute("http://api.themoviedb.org/3/movie/");
    }

    private class AsyncHttpTask extends AsyncTask<String, Void, Integer> {

          private final String LOG_TAG = MainActivity.class.getSimpleName();//Check


        @Override
        protected Integer doInBackground(String... params) {

            Integer result = 0;
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String base = params[0];

            String forecastJsonStr = null;


            try {

                //String apiKey = "http://api.themoviedb.org/3/movie/";//Add your API Key Here

                String id = String.valueOf(movieRecieved.id);

                String con = base.concat(id);

                String con2=con.concat("/videos?");

                String con3 = "api_key=".concat(MainActivity.API);
                String con4 =con2.concat(con3);

                // String apiKey = "06e4f6f996852dc1290dbc8256b0565b";//Add your API Key Here
                URL url = new URL(con4);


                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();


                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {

                    buffer.append(line + "\n");

                }


                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    result = 0;
                    return null;
                } else {
                    result = 1;
                    forecastJsonStr = buffer.toString();
                    parseResult(forecastJsonStr);
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            return result;
        }


        @Override


        protected void onPostExecute(Integer result) {
            // Download complete. Let us update UI
            if (result == 1) {
                //  mGridAdapter.setGridData(mGridData);
            } else {
                //Toast.makeText(context, "Failed to fetch data!", Toast.LENGTH_SHORT).show();
            }

        }
    }


    private void parseResult(String forecastJsonStr) {
        try {
            JSONObject response = new JSONObject(forecastJsonStr);
            JSONArray posts = response.optJSONArray("results");
            //  GridItem item;
            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.optJSONObject(i);
                String key = post.optString("key");
                youtubeopener(key);
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
    }


    private void youtubeopener(String key) {

        Intent intent = new Intent(Intent.ACTION_VIEW);

        intent.setPackage("com.google.android.youtube");
        String linker = "https://www.youtube.com/watch?v=".concat(key);
        intent.setData(Uri.parse(linker));

        startActivity(intent);
    }



    public void review(View view) {
        new AsyncHttpTask2().execute("http://api.themoviedb.org/3/movie/");
    }



    private class AsyncHttpTask2 extends AsyncTask<String, Void, Integer> {

        //private final String LOG_TAG = GridViewActivity.class.getSimpleName();//Check


        @Override
        protected Integer doInBackground(String... params) {

            Integer result = 0;
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String base = params[0];

            String forecastJsonStr2 = null;


            try {

                //String apiKey = "http://api.themoviedb.org/3/movie/";//Add your API Key Here

              //  String id = String.valueOf(movieRecieved.id);

               // String con = apiKey.concat(id);
              //  String con2 = con.concat("/reviews?api_key=06e4f6f996852dc1290dbc8256b0565b");



                String id = String.valueOf(movieRecieved.id);

                String con = base.concat(id);

                String con2=con.concat("/reviews?");

                String con3 = "api_key=".concat(MainActivity.API);
                String con4 =con2.concat(con3);

                // String apiKey = "06e4f6f996852dc1290dbc8256b0565b";//Add your API Key Here
                URL url = new URL(con4);

                // String apiKey = "06e4f6f996852dc1290dbc8256b0565b";//Add your API Key Here
               //URL url = new URL(con2);


                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();


                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {

                    buffer.append(line + "\n");

                }


                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    result = 0;
                    return null;
                } else {
                    result = 1;
                    forecastJsonStr2 = buffer.toString();
                    parseResult2(forecastJsonStr2);
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            return result;
        }


        @Override


        protected void onPostExecute(Integer result) {
            // Download complete. Let us update UI
            if (result == 1) {
                //  mGridAdapter.setGridData(mGridData);
            } else {
               // Toast.makeText(context, "Failed to fetch data!", Toast.LENGTH_SHORT).show();
            }

        }
    }


    private void parseResult2(String forecastJsonStr) {
        try {
            JSONObject response = new JSONObject(forecastJsonStr);
            JSONArray posts = response.optJSONArray("results");
            //  GridItem item;
            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.optJSONObject(i);
                String url = post.optString("url");
                urlopener(url);
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
    }


    private void urlopener(String key) {

        Intent intent = new Intent(Intent.ACTION_VIEW);

        //  intent.setPackage("com.google.android.youtube");
        String linker = key;
        intent.setData(Uri.parse(linker));

        startActivity(intent);
        //startActivity(intent);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments()!=null) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            movieRecieved =getArguments().getParcelable("movie");
        }
        else
        {
            movieRecieved=getActivity().getIntent().getParcelableExtra("movie");
        }
    }

    public void setContent(MovieObject movieRecieved)
    {
        Picasso.with(getContext()).load(baseUrlImage + movieRecieved.poster_path).into(moviePoster);
        getActivity().setTitle(movieRecieved.title);
       movie_release.setText(movieRecieved.release_date);
      movie_vote_average.setText(movieRecieved.vote_average);
        overview.setText(movieRecieved.overview);

    }
}

