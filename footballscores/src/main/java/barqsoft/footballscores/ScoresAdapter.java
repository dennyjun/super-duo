package barqsoft.footballscores;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by yehya khaled on 2/26/2015.
 */
public class ScoresAdapter extends CursorAdapter {                                                  // Class name should start with a capital letter
    public static final int COL_HOME = 3;
    public static final int COL_AWAY = 4;
    public static final int COL_HOME_GOALS = 6;
    public static final int COL_AWAY_GOALS = 7;
    public static final int COL_DATE = 1;
    public static final int COL_LEAGUE = 5;
    public static final int COL_MATCHDAY = 9;
    public static final int COL_ID = 8;
    public static final int COL_MATCHTIME = 2;
    private static final String FOOTBALL_SCORES_HASHTAG = "#Football_Scores";
    private static final String PLAIN_TEXT_TYPE = "text/plain";
    public double detail_match_id = 0;

    public ScoresAdapter(Context context) {
        super(context, null, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        final View item =
                LayoutInflater.from(context).inflate(R.layout.scores_list_item, parent, false);
        final ViewHolder holder = new ViewHolder(item);
        item.setTag(holder);
        //Log.v(FetchScoreTask.LOG_TAG, "new View inflated");
        return item;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        final ViewHolder holder = (ViewHolder) view.getTag();
        holder.homeName.setText(cursor.getString(COL_HOME));
        holder.awayName.setText(cursor.getString(COL_AWAY));
        holder.date.setText(cursor.getString(COL_MATCHTIME));
        holder.score.setText(
                Utilities.getScores(cursor.getInt(COL_HOME_GOALS), cursor.getInt(COL_AWAY_GOALS)));
        holder.matchId = cursor.getDouble(COL_ID);
        holder.homeCrest.setImageResource(Utilities.getTeamCrestByTeamName(context,
                cursor.getString(COL_HOME)));
        holder.awayCrest.setImageResource(Utilities.getTeamCrestByTeamName(context,
                cursor.getString(COL_AWAY)
        ));
        //Log.v(FetchScoreTask.LOG_TAG,holder.homeName.getText() + " Vs. " + holder.awayName.getText() +" id " + String.valueOf(holder.matchId));
        //Log.v(FetchScoreTask.LOG_TAG,String.valueOf(detail_match_id));
        LayoutInflater vi = (LayoutInflater) context.getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View v = vi.inflate(R.layout.detail_fragment, null);
        final ViewGroup container = (ViewGroup) view.findViewById(R.id.details_fragment_container);
        if (holder.matchId == detail_match_id) {
            //Log.v(FetchScoreTask.LOG_TAG,"will insert extraView");

            container.addView(v, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.MATCH_PARENT));
            final TextView matchDay = (TextView) v.findViewById(R.id.matchday_textview);
            matchDay.setText(Utilities.getMatchDay(context, cursor.getInt(COL_MATCHDAY),
                    cursor.getInt(COL_LEAGUE)));
            final TextView league = (TextView) v.findViewById(R.id.league_textview);
            league.setText(Utilities.getLeague(context, cursor.getInt(COL_LEAGUE)));
            final Button shareButton = (Button) v.findViewById(R.id.share_button);
            shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //add Share Action
                    context.startActivity(createShareForecastIntent(holder.homeName.getText() + " "
                            + holder.score.getText() + " " + holder.awayName.getText() + " "));
                }
            });
        } else {
            container.removeAllViews();
        }

    }

    public Intent createShareForecastIntent(String ShareText) {
        final Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType(PLAIN_TEXT_TYPE);
        shareIntent.putExtra(Intent.EXTRA_TEXT, ShareText + FOOTBALL_SCORES_HASHTAG);
        return shareIntent;
    }

}
