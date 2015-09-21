package barqsoft.footballscores;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;

/**
 * Created by yehya khaled on 2/25/2015.
 */
public class ScoresProvider extends ContentProvider {
    private static final int MATCHES = 100;
    private static final int MATCHES_WITH_LEAGUE = 101;
    private static final int MATCHES_WITH_ID = 102;
    private static final int MATCHES_WITH_DATE = 103;
    private static final SQLiteQueryBuilder SCORE_QUERY =
            new SQLiteQueryBuilder();                                                               // static final variables should be all capital
    private static final String SCORES_BY_LEAGUE = DatabaseContract.ScoresTable.LEAGUE_COL + " = ?";
    private static final String SCORES_BY_DATE =
            DatabaseContract.ScoresTable.DATE_COL + " LIKE ?";
    private static final String SCORES_BY_ID =
            DatabaseContract.ScoresTable.MATCH_ID + " = ?";
    private ScoresDBHelper scoresDBHelper;                                                          // Does not need to be static
    private static final UriMatcher URI_MATCHER = buildUriMatcher();                                // static final variables should be all capital

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DatabaseContract.BASE_CONTENT_URI.toString();
        matcher.addURI(authority, null, MATCHES);
        matcher.addURI(authority, DatabaseContract.ScoresTable.LEAGUE_COL, MATCHES_WITH_LEAGUE);
        matcher.addURI(authority, DatabaseContract.ScoresTable.ID_URI_PATH, MATCHES_WITH_ID);
        matcher.addURI(authority, DatabaseContract.ScoresTable.DATE_COL, MATCHES_WITH_DATE);
        return matcher;
    }

    private int match_uri(Uri uri) {
        final String link = uri.toString();
        if (link.contentEquals(DatabaseContract.BASE_CONTENT_URI.toString())) {
            return MATCHES;
        } else if (link.contentEquals(DatabaseContract.ScoresTable.buildScoreWithDate().toString())) {
            return MATCHES_WITH_DATE;
        } else if (link.contentEquals(DatabaseContract.ScoresTable.buildScoreWithId().toString())) {
            return MATCHES_WITH_ID;
        } else if (link.contentEquals(DatabaseContract.ScoresTable.buildScoreWithLeague().toString())) {
            return MATCHES_WITH_LEAGUE;
        }
        return -1;
    }

    @Override
    public boolean onCreate() {
        scoresDBHelper = new ScoresDBHelper(getContext());
        return false;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        final int match = URI_MATCHER.match(uri);
        switch (match) {
            case MATCHES:
                return DatabaseContract.ScoresTable.CONTENT_TYPE;
            case MATCHES_WITH_LEAGUE:
                return DatabaseContract.ScoresTable.CONTENT_TYPE;
            case MATCHES_WITH_ID:
                return DatabaseContract.ScoresTable.CONTENT_ITEM_TYPE;
            case MATCHES_WITH_DATE:
                return DatabaseContract.ScoresTable.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException(
                        getContext().getString(R.string.unknown_uri_prefix_msg) + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        //Log.v(FetchScoreTask.LOG_TAG,uri.getPathSegments().toString());
        int match = match_uri(uri);
        //Log.v(FetchScoreTask.LOG_TAG,SCORES_BY_LEAGUE);
        //Log.v(FetchScoreTask.LOG_TAG,selectionArgs[0]);
        //Log.v(FetchScoreTask.LOG_TAG,String.valueOf(match));
        switch (match) {
            case MATCHES:
                retCursor = scoresDBHelper.getReadableDatabase().query(
                        DatabaseContract.SCORES_TABLE,
                        projection,
                        null,
                        null,
                        null,
                        null,
                        sortOrder);
                break;
            case MATCHES_WITH_DATE:
                //Log.v(FetchScoreTask.LOG_TAG,selectionArgs[1]);
                //Log.v(FetchScoreTask.LOG_TAG,selectionArgs[2]);
                retCursor = scoresDBHelper.getReadableDatabase().query(
                        DatabaseContract.SCORES_TABLE,
                        projection,
                        SCORES_BY_DATE,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case MATCHES_WITH_ID:
                retCursor = scoresDBHelper.getReadableDatabase().query(
                        DatabaseContract.SCORES_TABLE,
                        projection,
                        SCORES_BY_ID,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case MATCHES_WITH_LEAGUE:
                retCursor = scoresDBHelper.getReadableDatabase().query(
                        DatabaseContract.SCORES_TABLE,
                        projection,
                        SCORES_BY_LEAGUE,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException(
                        getContext().getString(R.string.unknown_uri_prefix_msg) + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        return null;
    }

    @Override
    public int bulkInsert(Uri uri, @NonNull ContentValues[] values) {
        SQLiteDatabase db = scoresDBHelper.getWritableDatabase();
        //db.delete(DatabaseContract.SCORES_TABLE,null,null);
        //Log.v(FetchScoreTask.LOG_TAG,String.valueOf(URI_MATCHER.match(uri)));
        switch (match_uri(uri)) {
            case MATCHES:
                db.beginTransaction();
                int returncount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insertWithOnConflict(DatabaseContract.SCORES_TABLE, null, value,
                                SQLiteDatabase.CONFLICT_REPLACE);
                        if (_id != -1) {
                            returncount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returncount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }
}
