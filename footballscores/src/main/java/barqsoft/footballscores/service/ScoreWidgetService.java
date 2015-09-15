package barqsoft.footballscores.service;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilies;
import barqsoft.footballscores.ScoresAdapter;

/**
 * Created by Denny on 9/14/2015.
 */
public class ScoreWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ScoreWidgetRemoteViewsFactory(this.getApplicationContext(), intent);
    }

    class ScoreWidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
        private final Context context;
        private final int appWidgetId;
        private Cursor scoresCursor;

        public ScoreWidgetRemoteViewsFactory(Context context, Intent intent) {
            this.context = context;
            appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        @Override
        public void onCreate() {
            final SimpleDateFormat dateFormat =
                    new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            scoresCursor = context.getContentResolver()
                    .query(DatabaseContract.scores_table.buildScoreWithDate(),
                            null,
                            null,
                            new String[]{ dateFormat.format(new Date()) },
                            null);
        }

        @Override
        public void onDataSetChanged() {
        }

        @Override
        public void onDestroy() {
            scoresCursor.close();
        }

        @Override
        public int getCount() {
            return scoresCursor.getCount();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            scoresCursor.moveToPosition(position);
            final RemoteViews remoteViews =
                    new RemoteViews(context.getPackageName(), R.layout.scores_list_item);
            remoteViews.setTextViewText(R.id.home_name,
                    scoresCursor.getString(ScoresAdapter.COL_HOME));
            remoteViews.setTextViewText(R.id.away_name,
                    scoresCursor.getString(ScoresAdapter.COL_AWAY));
            remoteViews.setTextViewText(R.id.data_textview,
                    scoresCursor.getString(ScoresAdapter.COL_MATCHTIME));
            remoteViews.setTextViewText(R.id.score_textview, Utilies.getScores(
                    scoresCursor.getInt(ScoresAdapter.COL_HOME_GOALS),
                    scoresCursor.getInt(ScoresAdapter.COL_AWAY_GOALS)));
            remoteViews.setImageViewResource(R.id.home_crest, Utilies.getTeamCrestByTeamName(
                    scoresCursor.getString(ScoresAdapter.COL_HOME)));
            remoteViews.setImageViewResource(R.id.away_crest, Utilies.getTeamCrestByTeamName(
                    scoresCursor.getString(ScoresAdapter.COL_AWAY)));
            return remoteViews;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            scoresCursor.moveToPosition(position);
            final int columnIndex =
                    scoresCursor.getColumnIndex(DatabaseContract.scores_table._ID);
            return scoresCursor.getLong(columnIndex);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
