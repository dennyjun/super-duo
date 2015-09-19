package barqsoft.footballscores;

import android.content.Context;

/**
 * Created by yehya khaled on 3/3/2015.
 */
public class Utilities {                                                                            // Corrected spelling for Utilities
    public static final int SERIE_A = 357;
    public static final int PREMIER_LEGAUE = 354;
    public static final int CHAMPIONS_LEAGUE = 362;
    public static final int PRIMERA_DIVISION = 358;
    public static final int BUNDESLIGA = 351;

    public static String getLeague(Context context, int leagueNum) {
        switch (leagueNum) {
            case SERIE_A : return context.getString(R.string.seriaa);
            case PREMIER_LEGAUE : return context.getString(R.string.premierleague);
            case CHAMPIONS_LEAGUE : return context.getString(R.string.champions_league);
            case PRIMERA_DIVISION : return context.getString(R.string.primeradivison);
            case BUNDESLIGA : return context.getString(R.string.bundesliga);
            default: return context.getString(R.string.not_known_league_msg);
        }
    }
    
    public static String getMatchDay(Context context, int matchDay, int leagueNum) {
        if(leagueNum == CHAMPIONS_LEAGUE) {
            if (matchDay <= 6) {
                return context.getString(R.string.match_day_group_stages);
            } else if(matchDay == 7 || matchDay == 8) {
                return context.getString(R.string.match_day_first_knockout_round);
            } else if(matchDay == 9 || matchDay == 10) {
                return context.getString(R.string.match_day_quarter_final);
            } else if(matchDay == 11 || matchDay == 12) {
                return context.getString(R.string.match_day_semi_final);
            } else {
                return context.getString(R.string.match_day_finals);
            }
        } else {
            return context.getString(R.string.match_day_normal_prefix) + String.valueOf(matchDay);
        }
    }

    public static String getScores(int homeGoals, int awayGoals) {
        if(homeGoals < 0 || awayGoals < 0) {
            return " - ";
        } else {
            return String.valueOf(homeGoals) + " - " + String.valueOf(awayGoals);
        }
    }

    public static int getTeamCrestByTeamName (String teamName) {
        if (teamName==null){return R.drawable.no_icon;}
        switch (teamName) {
            //This is the set of icons that are currently in the app. Feel free to find and add more
            //as you go.
            case "Arsenal London FC" : return R.drawable.arsenal;
            case "Manchester United FC" : return R.drawable.manchester_united;
            case "Swansea City" : return R.drawable.swansea_city_afc;
            case "Leicester City" : return R.drawable.leicester_city_fc_hd_logo;
            case "Everton FC" : return R.drawable.everton_fc_logo1;
            case "West Ham United FC" : return R.drawable.west_ham;
            case "Tottenham Hotspur FC" : return R.drawable.tottenham_hotspur;
            case "West Bromwich Albion" : return R.drawable.west_bromwich_albion_hd_logo;
            case "Sunderland AFC" : return R.drawable.sunderland;
            case "Stoke City FC" : return R.drawable.stoke_city;
            default: return R.drawable.no_icon;
        }
    }
}
