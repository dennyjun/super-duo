package barqsoft.footballscores.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Vector;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.R;

/**
 * Created by yehya khaled on 3/2/2015.
 */
public class MyFetchService extends IntentService {
    public static final String LOG_TAG = MyFetchService.class.getSimpleName();

    private static final String TIME_FRAME_N2 = "n2";
    private static final String TIME_FRAME_P2 = "p2";

    private static final String GET_REQUEST_METHOD = "GET";
    private static final String REQUEST_PROP_X_AUTH_TOKEN = "X-Auth-Token";
    private static final String BASE_URL = "http://api.football-data.org/alpha/fixtures";           //Base URL
    private static final String QUERY_TIME_FRAME = "timeFrame";                                     //Time Frame parameter to determine days

    private static final String JSON_NAME_HREF = "href";
    private static final SimpleDateFormat MATCH_DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-ddHH:mm:ss", Locale.getDefault());
    private static final TimeZone UTC_TIMEZONE = TimeZone.getTimeZone("UTC");
    private static final SimpleDateFormat HOUR_MIN_FORMAT =
            new SimpleDateFormat("HH:mm", Locale.getDefault());

    //JSON data
    // This set of league codes is for the 2015/2016 season. In fall of 2016, they will need to
    // be updated. Feel free to use the codes
    final String BUNDESLIGA1 = "394";
    final String BUNDESLIGA2 = "395";
    final String LIGUE1 = "396";
    final String LIGUE2 = "397";
    final String PREMIER_LEAGUE = "398";
    final String PRIMERA_DIVISION = "399";
    final String SEGUNDA_DIVISION = "400";
    final String SERIE_A = "401";
    final String PRIMERA_LIGA = "402";
    final String Bundesliga3 = "403";
    final String EREDIVISIE = "404";

    final String SEASON_LINK = "http://api.football-data.org/alpha/soccerseasons/";
    final String MATCH_LINK = "http://api.football-data.org/alpha/fixtures/";
    final String FIXTURES = "fixtures";
    final String LINKS = "_links";
    final String SOCCER_SEASON = "soccerseason";
    final String SELF = "self";
    final String MATCH_DATE = "date";
    final String HOME_TEAM = "homeTeamName";
    final String AWAY_TEAM = "awayTeamName";
    final String RESULT = "result";
    final String HOME_GOALS = "goalsHomeTeam";
    final String AWAY_GOALS = "goalsAwayTeam";
    final String MATCH_DAY = "matchday";

    public MyFetchService() {
        super(LOG_TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        getData(TIME_FRAME_N2);
        getData(TIME_FRAME_P2);
    }

    private void getData(String timeFrame) {
        //final String QUERY_MATCH_DAY = "matchday";
        final Uri fetchBuild = Uri.parse(BASE_URL).buildUpon().
                appendQueryParameter(QUERY_TIME_FRAME, timeFrame).build();
        //Log.v(LOG_TAG, "The url we are looking at is: " + fetchBuild.toString()); //log spam
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        String jsonData = null;
        //Opening Connection
        try {
            final URL fetch = new URL(fetchBuild.toString());
            connection = (HttpURLConnection) fetch.openConnection();
            connection.setRequestMethod(GET_REQUEST_METHOD);
            connection.addRequestProperty(REQUEST_PROP_X_AUTH_TOKEN, getString(R.string.api_key));
            connection.connect();

            // Read the input stream into a String
            final InputStream inputStream = connection.getInputStream();
            final StringBuilder builder = new StringBuilder();
            if (inputStream == null) {
                // Nothing to do.
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // builder for debugging.
                builder.append(line).append("\n");
            }
            if (builder.length() == 0) {
                // Stream was empty.  No point in parsing.
                return;
            }
            jsonData = builder.toString();
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, getString(R.string.error_closing_stream_msg));
                }
            }
        }
        try {
            if (jsonData != null) {
                //This bit is to check if the data contains any matches. If not, we call processJson on the dummy data
                final JSONArray matches = new JSONObject(jsonData).getJSONArray(FIXTURES);
                if (matches.length() == 0) {
                    //if there is no data, call the function on dummy data
                    //this is expected behavior during the off season.
                    processJSONdata(getString(R.string.dummy_data), getApplicationContext(), false);
                    return;
                }


                processJSONdata(jsonData, getApplicationContext(), true);
            } else {
                //Could not Connect
                Log.d(LOG_TAG, getString(R.string.could_not_connect_to_server_msg));
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }

    private void processJSONdata(String jsonData, Context context, boolean isReal) {
        //Match data
        String league;                                                                              // variable name should not be capitalized
        String date;
        String time;
        String home;                                                                                // variable name should not be capitalized
        String away;                                                                                // variable name should not be capitalized
        String homeGoals;                                                                           // variable name should not be capitalized
        String awayGoals;                                                                           // variable name should not be capitalized
        String matchId;                                                                             // remove underscore from variable name
        String matchDay;                                                                            // remove underscore from variable name

        try {
            final JSONArray matches = new JSONObject(jsonData).getJSONArray(FIXTURES);

            //ContentValues to be inserted
            final Vector<ContentValues> values = new Vector<>(matches.length());
            for (int i = 0; i < matches.length(); i++) {
                final JSONObject matchData = matches.getJSONObject(i);
                league = matchData.getJSONObject(LINKS).getJSONObject(SOCCER_SEASON).
                        getString(JSON_NAME_HREF);
                league = league.replace(SEASON_LINK, "");
                //This if statement controls which leagues we're interested in the data from.
                //add leagues here in order to have them be added to the DB.
                // If you are finding no data in the app, check that this contains all the leagues.
                // If it doesn't, that can cause an empty DB, bypassing the dummy data routine.
                if (league.equals(PREMIER_LEAGUE) ||
                        league.equals(SERIE_A) ||
                        league.equals(BUNDESLIGA1) ||
                        league.equals(BUNDESLIGA2) ||
                        league.equals(PRIMERA_DIVISION)) {
                    matchId = matchData.getJSONObject(LINKS).getJSONObject(SELF).
                            getString(JSON_NAME_HREF);
                    matchId = matchId.replace(MATCH_LINK, "");
                    if (!isReal) {
                        //This if statement changes the match ID of the dummy data so that it all goes into the database
                        matchId = matchId + Integer.toString(i);
                    }

                    date = matchData.getString(MATCH_DATE);
                    time = date.substring(date.indexOf("T") + 1, date.indexOf("Z"));
                    date = date.substring(0, date.indexOf("T"));
                    MATCH_DATE_FORMAT.setTimeZone(UTC_TIMEZONE);
                    try {
                        final Date parsedDate = MATCH_DATE_FORMAT.parse(date + time);
                        HOUR_MIN_FORMAT.setTimeZone(TimeZone.getDefault());
                        time = HOUR_MIN_FORMAT.format(parsedDate);
                        date = DatabaseContract.ScoresTable.DATE_FORMAT.format(parsedDate);

                        if (!isReal) {
                            //This if statement changes the dummy data's date to match our current date range.
                            final Date fragmentDate =
                                    new Date(System.currentTimeMillis() + ((i - 2) * 86400000));
                            date = DatabaseContract.ScoresTable.DATE_FORMAT.format(fragmentDate);
                        }
                    } catch (Exception e) {
                        Log.e(LOG_TAG, e.getMessage());                                              // Removed redundant error message
                    }
                    home = matchData.getString(HOME_TEAM);
                    away = matchData.getString(AWAY_TEAM);
                    homeGoals = matchData.getJSONObject(RESULT).getString(HOME_GOALS);
                    awayGoals = matchData.getJSONObject(RESULT).getString(AWAY_GOALS);
                    matchDay = matchData.getString(MATCH_DAY);
                    final ContentValues matchValues = new ContentValues();
                    matchValues.put(DatabaseContract.ScoresTable.MATCH_ID, matchId);
                    matchValues.put(DatabaseContract.ScoresTable.DATE_COL, date);
                    matchValues.put(DatabaseContract.ScoresTable.TIME_COL, time);
                    matchValues.put(DatabaseContract.ScoresTable.HOME_COL, home);
                    matchValues.put(DatabaseContract.ScoresTable.AWAY_COL, away);
                    matchValues.put(DatabaseContract.ScoresTable.HOME_GOALS_COL, homeGoals);
                    matchValues.put(DatabaseContract.ScoresTable.AWAY_GOALS_COL, awayGoals);
                    matchValues.put(DatabaseContract.ScoresTable.LEAGUE_COL, league);
                    matchValues.put(DatabaseContract.ScoresTable.MATCH_DAY, matchDay);
                    //log spam

                    //Log.v(LOG_TAG,match_id);
                    //Log.v(LOG_TAG,date);
                    //Log.v(LOG_TAG,time);
                    //Log.v(LOG_TAG,home);
                    //Log.v(LOG_TAG,away);
                    //Log.v(LOG_TAG,homeGoals);
                    //Log.v(LOG_TAG,awayGoals);

                    values.add(matchValues);
                }
            }
            int inserted_data = 0;
            final ContentValues[] insertData = new ContentValues[values.size()];
            values.toArray(insertData);
            inserted_data = context.getContentResolver().bulkInsert(
                    DatabaseContract.BASE_CONTENT_URI, insertData);

            //Log.v(LOG_TAG,"Succesfully Inserted : " + String.valueOf(inserted_data));
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage());
        }

    }
}

