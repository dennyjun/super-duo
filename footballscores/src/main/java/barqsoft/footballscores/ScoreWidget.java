package barqsoft.footballscores;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import barqsoft.footballscores.service.MyFetchService;
import barqsoft.footballscores.service.ScoreWidgetService;

/**
 * Implementation of App Widget functionality.
 */
public class ScoreWidget extends AppWidgetProvider {
    private static final String LOG_TAG = ScoreWidget.class.getSimpleName();
    public static final String UPDATE_WIDGET_ACTION = "downloadFinishedUpdateNow";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        Log.d(LOG_TAG, context.getString(R.string.msg_update_app_widget_prefix) + appWidgetId);
        // Construct the RemoteViews object
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.score_widget);
        final Intent intent = new Intent(context, ScoreWidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        views.setRemoteAdapter(appWidgetId, R.id.score_listview, intent);
        views.setEmptyView(R.id.score_listview, R.id.no_games_textview);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        updateScores(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if(UPDATE_WIDGET_ACTION.equals(intent.getAction())) {
            final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            final ComponentName componentName =
                    new ComponentName(context.getPackageName(),ScoreWidget.class.getName());
            final int[] appWidgetIds = appWidgetManager.getAppWidgetIds(componentName);

            for (final Integer appWidgetId : appWidgetIds) {
                updateAppWidget(context, appWidgetManager, appWidgetId);
            }
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    private void updateScores(Context context) {
        final Intent intent = new Intent(context, MyFetchService.class);
        intent.setAction(UPDATE_WIDGET_ACTION);
        context.startService(intent);
    }
}

