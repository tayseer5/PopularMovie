package tayseer.test;
import android.widget.Toast;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.util.Log;

/**
 * Created by pc on 10/20/2015.
 */
public class MainActivity extends ActionBarActivity implements MovieFragment.Callback {
    //Now we will be heading towards hooking the adapter to GridView and make it functional
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private String msort;
    MovieDbHelper mhelper = new MovieDbHelper(this);
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private boolean mTwoPane;
    MovieItem movieItem;
    @Override
    public void onItemSelected(MovieItem movieItem,int flag) {
        this.movieItem = movieItem;
        if (mTwoPane) {
Log.e("in two pane","onclick");
            Bundle args = new Bundle();
            args.putParcelable("Movie", movieItem);

            DtailFragment fragment = new DtailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else if(flag==0) {
            Intent intent = new Intent(this, DetailActivity.class)
                    .putExtra("Movie", movieItem);
            Log.e("in one pane","on item select ");
            startActivity(intent);
        }
    }
    public void Favorite(View view) {
        ImageButton fav = (ImageButton) view.findViewById(R.id.fav);
        if (((int)fav.getTag()) == 0)
            addFav(view);
        else
            removeFav(view);
    }
    public void addFav(View view) {
        ImageButton fav = (ImageButton) view.findViewById(R.id.fav);
        fav.setTag(1);
        SQLiteDatabase db = mhelper.getReadableDatabase();
        fav.setImageResource(R.drawable.favorite2);
        ContentValues c = new ContentValues();
        c.put(MovieContract.MovieEntry.title, movieItem.getTitle());
        c.put(MovieContract.MovieEntry.vote_average, movieItem.getVoteAverage());
        c.put(MovieContract.MovieEntry.mid, movieItem.getId());
        c.put(MovieContract.MovieEntry.overview, movieItem.getOverview());
        c.put(MovieContract.MovieEntry.poster_path, movieItem.getImage());
        c.put(MovieContract.MovieEntry.release_date, movieItem.getReleaseDate());
        db.insert(
                MovieContract.MovieEntry.TABLE_NAME,
                null,
                c);
        Toast.makeText(this,"The Movie Add To Favourite",Toast.LENGTH_LONG).show();
    }
    public void removeFav(View view)
    {
        ImageButton fav = (ImageButton) view.findViewById(R.id.fav);
        fav.setTag(0);
        SQLiteDatabase db = mhelper.getReadableDatabase();
        fav.setImageResource(R.drawable.favorite1);
        db.delete(MovieContract.MovieEntry.TABLE_NAME,
                MovieContract.MovieEntry.mid + " = ?",
                new String[]{String.valueOf(movieItem.getId())});
        Toast.makeText(this,"The Movie Removd From Favourite",Toast.LENGTH_LONG).show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e("in on create","main activty");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        String sort = prefs.getString(getString(R.string.sort_syncConnectionType),
                getString(R.string.pref_syncConnectionTypes_default));
        msort=sort;
        if (findViewById(R.id.detail_container) != null) {
            if (savedInstanceState == null) {
                mTwoPane=true;
                Log.e("two paner","yes");
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_container, new DtailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        }
        else
            mTwoPane=false;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this,SettingActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

