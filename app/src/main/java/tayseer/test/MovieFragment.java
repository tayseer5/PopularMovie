package tayseer.test;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;


/**
 * Created by pc on 10/20/2015.
 */
public class MovieFragment extends Fragment {
    private GridView mGridView;
    private ProgressBar mPrograssBar;
    private ArrayList<MovieItem> mGridData;
    private GridViewAdapter mGridAdapter;
    private int mPosition = GridView.INVALID_POSITION;
    private static final String SELECTED_KEY = "selected_position";
    private final String TAG = MovieFragment.class.getSimpleName();
    public MovieFragment() {
    }

    public interface Callback {
        public void onItemSelected(MovieItem movieItem, int flag);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.moviefragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            //FetchMovieTask weatherTask = new FetchMovieTask();
            //weatherTask.execute("c993d041d56bfff4541d9dbd9fac96b2");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mGridView = (GridView) rootView.findViewById(R.id.gridView);
        mGridData= new ArrayList<>();
        mPrograssBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        mGridAdapter= new GridViewAdapter(getActivity(),R.layout.grid_view_item,mGridData);
        mGridView.setAdapter(mGridAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                mPosition = position;
                MovieItem selectedItem = (MovieItem) adapterView.getAdapter().getItem(position);
                ((Callback) getActivity())
                        .onItemSelected(selectedItem,0);
            }
        });
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        if(savedInstanceState == null || !savedInstanceState.containsKey("key")) {
            updateMovies();
        }
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateMovies();
//        mGridData.clear();
//        mGridAdapter.clear();

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {

            mGridData = savedInstanceState.getParcelableArrayList("key");
            mGridAdapter= new GridViewAdapter(getActivity(),R.layout.grid_view_item,mGridData);
            mGridView.setAdapter(mGridAdapter);
            //mGridAdapter= new GridViewAdapter(getActivity(),R.layout.grid_view_item,mGridData);
            mGridAdapter.setGridData(mGridData);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("key",mGridData);
        super.onSaveInstanceState(outState);
        if (mPosition != GridView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }
    public void updateMovies()
    {
        mGridData.clear();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sort = prefs.getString(getString(R.string.sort_syncConnectionType),
                getString(R.string.pref_syncConnectionTypes_default));
        if(sort.equals("fav"))
            favorite();
            //startActivity(new Intent(this,favorite.class));
        else
        {
            new AsyncHttpTask().execute(sort);
            mPrograssBar.setVisibility(View.VISIBLE);
        }
    }
    public class AsyncHttpTask extends AsyncTask<String,Void,Integer> {


        @Override
        protected Integer doInBackground(String... params) {
            Integer result = 0;
            String baseURL="http://api.themoviedb.org/3/discover/movie?";
            String sort_by="sort_by";
            String api_key="api_key";
            String key ="76277347eaed8f1535c8babcfd49c1a7";
            try {
                Uri builtUri = Uri.parse(baseURL).buildUpon()
                        .appendQueryParameter(sort_by, params[0])
                        .appendQueryParameter(api_key, key)
                        .build();

                URL url = new URL(builtUri.toString());

//                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//                urlConnection.setRequestMethod("GET");
//                urlConnection.connect();
                // Create Apache HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse httpResponse = httpclient.execute(new HttpGet(url.toString()));
                // int statusCode = httpResponse.getStatusLine().getStatusCode();

                // 200 represents HTTP OK

                String response = streamToString(httpResponse.getEntity().getContent());
                parseResult(response);
                result = 1; // Successful
            } catch (Exception e) {
                Log.d(TAG, e.getLocalizedMessage());
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            // Download complete. Let us update UI
            if (result == 1) {
                mGridAdapter.setGridData(mGridData);
                MovieItem m = mGridData.get(0);
                //Toast.makeText(getActivity(),m.getTitle(),Toast.LENGTH_LONG).show();
                ((Callback) getActivity())
                        .onItemSelected(m,1);
            } else {
                Toast.makeText(getActivity(), "Failed to fetch data!", Toast.LENGTH_SHORT).show();
            }
            mPrograssBar.setVisibility(View.GONE);
        }
    }
    String streamToString(InputStream stream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
        String line;
        String result = "";
        while ((line = bufferedReader.readLine()) != null) {
            result += line;
        }

        // Close stream
        if (null != stream) {
            stream.close();
        }
        return result;
    }
    private void parseResult(String responsesFromAsy) {
        try {
            JSONObject response = new JSONObject(responsesFromAsy);
            JSONArray results = response.optJSONArray("results");
            MovieItem item;
            for (int i = 0; i < results.length(); i++) {
                JSONObject result = results.optJSONObject(i);
                //title = post.optString("title");
                item = new MovieItem();
                String title = result.getString("original_title");
                item.setTitle(title);
                //JSONArray attachments = result.getJSONArray("attachments");
                String attach = result.getString("poster_path");
                String fullPath="http://image.tmdb.org/t/p/w185/"+attach;

                String overview = result.getString("overview");
                item.setOverview(overview);

                String release_date = result.getString("release_date");
                item.setReleaseDate(release_date);

                String vote_average = result.getString("vote_average");
                item.setVoteAverage(vote_average);

                int id = result.getInt("id");
                item.setId(id);

                item.setImage(fullPath);
//                if (null != attachments && attachments.length() > 0) {
//                    JSONObject attachment = attachments.getJSONObject(0);
//                    if (attachment != null)
//                        item.setImage(attachment.getString("url"));
//                }
                mGridData.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void favorite()
    {   MovieItem item;
        String[] col={MovieContract.MovieEntry.title,
                MovieContract.MovieEntry.poster_path,
                MovieContract.MovieEntry.release_date,
                MovieContract.MovieEntry.vote_average,
                MovieContract.MovieEntry.overview,
                MovieContract.MovieEntry.mid};
        MovieDbHelper mhelper = new MovieDbHelper(getActivity());
        SQLiteDatabase db = mhelper.getReadableDatabase();
        Cursor cur = db.query(
                MovieContract.MovieEntry.TABLE_NAME,
                col,
                null,
                null,
                null,
                null,
                null
        );
        if (cur.moveToFirst()) {
            do {
                item = new MovieItem();
                String title = cur.getString(0);
                String attach = cur.getString(1);
                String release_date = cur.getString(2);
                String vote_average = cur.getString(3);
                String overview = cur.getString(4);
                int id = cur.getInt(5);
                item.setTitle(title);
                String fullPath="http://image.tmdb.org/t/p/w185/"+attach;
                item.setOverview(overview);
                item.setReleaseDate(release_date);
                item.setVoteAverage(vote_average);
                item.setId(id);
                item.setImage(fullPath);
                mGridData.add(item);

            } while (cur.moveToNext());
        }
        mGridAdapter.setGridData(mGridData);
    }

}

