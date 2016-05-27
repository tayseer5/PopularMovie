package tayseer.test;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by pc on 10/20/2015.
 */
public class MovieContract {
    public static final String CONTENT_AUTHORITY = "com.example.pc.test";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_Movie = "movie";

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_Movie).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_Movie;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_Movie;

        public static final String TABLE_NAME = "Movie";
        public static final String adult = "adult";
        public static final String backdrop_path = "backdrop_path";
        public static final String mid = "mid";
        public static final String original_title = "original_title";
        public static final String overview = "overview";
        public static final String release_date = "release_date";
        public static final String poster_path = "poster_path";
        public static final String title = "title";
        public static final String vote_average = "vote_average";

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getLocationSettingFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static long getDateFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(2));
        }
    }
}
