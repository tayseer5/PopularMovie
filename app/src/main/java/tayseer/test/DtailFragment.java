package tayseer.test;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

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
public class DtailFragment extends Fragment {
    private final String TAG = DtailFragment.class.getSimpleName();
    MovieItem moviestr;
    ListView mListView;
    ListView mReviewView;
    ArrayList<Trailer> mListData;
    ArrayList<Review> mReviewData;
    TrailerAdapter mListAdapter;
    ReviewAdapter mReviewAdapter;
    public DtailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent = getActivity().getIntent();
        Bundle arguments = getArguments();
        if (arguments != null) {
            MovieDbHelper mhelper = new MovieDbHelper(getActivity());
            ImageButton fav = (ImageButton) rootView.findViewById(R.id.fav);
            SQLiteDatabase db = mhelper.getReadableDatabase();
            moviestr = (MovieItem) arguments.getParcelable("Movie");
            Cursor cur = db.query(MovieContract.MovieEntry.TABLE_NAME,
                    new String[] {MovieContract.MovieEntry.title},
                    "mid=?",
                    new String[] { String.valueOf(moviestr.getId()) },
                    null,
                    null,
                    null);
            if (cur.moveToFirst())
            {
                fav.setImageResource(R.drawable.favorite2);
                fav.setTag(1);
            }
            else
            {
                fav.setImageResource(R.drawable.favorite1);
                fav.setTag(0);
            }
            ((TextView) rootView.findViewById(R.id.movie_title))
                    .setText(moviestr.getTitle());
            ((TextView) rootView.findViewById(R.id.movie_release_date))
                    .setText(moviestr.getReleaseDate());
            Picasso.with(getActivity()).load(moviestr.getImage()).into((ImageView) rootView.findViewById(R.id.movie_poster));
            ((TextView) rootView.findViewById(R.id.movie_rating))
                    .setText(moviestr.getVoteAverage() + "/10");
            ((TextView) rootView.findViewById(R.id.over_view))
                    .setText(moviestr.getOverview());
        }
        if (intent != null && intent.hasExtra("Movie")) {
            moviestr = (MovieItem) intent.getParcelableExtra("Movie");
            ((TextView) rootView.findViewById(R.id.movie_title))
                    .setText(moviestr.getTitle());
            ((TextView) rootView.findViewById(R.id.movie_release_date))
                    .setText(moviestr.getReleaseDate());
            Picasso.with(getActivity()).load(moviestr.getImage()).into((ImageView) rootView.findViewById(R.id.movie_poster));
            ((TextView) rootView.findViewById(R.id.movie_rating))
                    .setText(moviestr.getVoteAverage() + "/10");
            ((TextView) rootView.findViewById(R.id.over_view))
                    .setText(moviestr.getOverview());
        }
        mListView = (ListView) rootView.findViewById(R.id.listview2);
        mListData=new ArrayList<>();
        mListAdapter= new TrailerAdapter(getActivity(),R.layout.list_item_trailer,mListData);
        mListView.setAdapter(mListAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Trailer t = (Trailer) adapterView.getAdapter().getItem(position);
                Intent intent;
                try{
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + t.getKey()));
                    startActivity(intent);
                }catch (ActivityNotFoundException ex){
                    intent=new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://www.youtube.com/watch?v="+t.getKey()));
                    startActivity(intent);
                }
                startActivity(intent);
            }
        });
        if(moviestr!=null)
            new trailer().execute(String.valueOf(moviestr.getId()));

        mReviewView = (ListView) rootView.findViewById(R.id.review2);
        mReviewData=new ArrayList<>();
        mReviewAdapter= new ReviewAdapter(getActivity(),R.layout.list_item_review,mReviewData);
        mReviewView.setAdapter(mReviewAdapter);
        mReviewView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Review r = (Review) adapterView.getAdapter().getItem(position);
                Intent intent;
                intent=new Intent(Intent.ACTION_VIEW,
                        Uri.parse(r.getUrl()));
                startActivity(intent);
            }
        });
        if(moviestr!=null)
            new review().execute(String.valueOf(moviestr.getId()));
        return rootView;
    }
    public class trailer extends AsyncTask<String,Void,Integer> {


        @Override
        protected Integer doInBackground(String... params) {

            Integer result = 0;
            String baseURL="http://api.themoviedb.org/3/movie/"+params[0]+"/videos?";
            String api_key="api_key";
            String key ="c993d041d56bfff4541d9dbd9fac96b2";
            try {
                Uri builtUri = Uri.parse(baseURL).buildUpon()
                        .appendQueryParameter(api_key, key)
                        .build();

                URL url = new URL(builtUri.toString());

                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse httpResponse = httpclient.execute(new HttpGet(url.toString()));
                String response = streamToString(httpResponse.getEntity().getContent());
                Log.d("Parsing","Parsing....................");
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
                mListAdapter.setListData(mListData);
            } else {
                Toast.makeText(getActivity().getApplication(), "Failed to fetch data!", Toast.LENGTH_SHORT).show();
            }

        }
    }

    public class review extends AsyncTask<String,Void,Integer> {


        @Override
        protected Integer doInBackground(String... params) {

            Integer result = 0;
            String baseURL="http://api.themoviedb.org/3/movie/"+params[0]+"/reviews?";
            String api_key="api_key";
            String key ="c993d041d56bfff4541d9dbd9fac96b2";
            try {
                Uri builtUri = Uri.parse(baseURL).buildUpon()
                        .appendQueryParameter(api_key, key)
                        .build();

                URL url = new URL(builtUri.toString());

                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse httpResponse = httpclient.execute(new HttpGet(url.toString()));
                String response = streamToString(httpResponse.getEntity().getContent());
                Log.d("Parsing","Parsing....................");
                parseResult2(response);
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
                // mtAdapter.setListData(mData);
                mReviewAdapter.setListData(mReviewData);
            } else {
                Toast.makeText(getActivity(), "Failed to fetch data!", Toast.LENGTH_SHORT).show();
            }

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
            Trailer item;
            for (int i = 0; i < results.length(); i++) {
                JSONObject result = results.optJSONObject(i);
                item = new Trailer();
                String key = result.getString("key");
                item.setKey(key);
                String name = result.getString("name");
                item.setName(name);
                mListData.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void parseResult2(String responsesFromAsy) {
        try {
            JSONObject response = new JSONObject(responsesFromAsy);
            JSONArray results = response.optJSONArray("results");
            Review item;
            for (int i = 0; i < results.length(); i++) {
                JSONObject result = results.optJSONObject(i);
                item = new Review();
                String author = result.getString("author");
                item.setAuthor(author);
                String content = result.getString("content");
                item.setContent(content);
                String url = result.getString("url");
                item.setUrl(url);
                mReviewData.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}