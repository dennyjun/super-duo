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
            case SERIE_A:
                return context.getString(R.string.seriaa);
            case PREMIER_LEGAUE:
                return context.getString(R.string.premierleague);
            case CHAMPIONS_LEAGUE:
                return context.getString(R.string.champions_league);
            case PRIMERA_DIVISION:
                return context.getString(R.string.primeradivison);
            case BUNDESLIGA:
                return context.getString(R.string.bundesliga);
            default:
                return context.getString(R.string.not_known_league_msg);
        }
    }

    public static String getMatchDay(Context context, int matchDay, int leagueNum) {
        if (leagueNum == CHAMPIONS_LEAGUE) {
            if (matchDay <= 6) {
                return context.getString(R.string.match_day_group_stages);
            } else if (matchDay == 7 || matchDay == 8) {
                return context.getString(R.string.match_day_first_knockout_round);
            } else if (matchDay == 9 || matchDay == 10) {
                return context.getString(R.string.match_day_quarter_final);
            } else if (matchDay == 11 || matchDay == 12) {
                return context.getString(R.string.match_day_semi_final);
            } else {
                return context.getString(R.string.match_day_finals);
            }
        } else {
            return context.getString(R.string.match_day_normal_prefix) + String.valueOf(matchDay);
        }
    }

    public static String getScores(int homeGoals, int awayGoals) {
        if (homeGoals < 0 || awayGoals < 0) {
            return " - ";
        } else {
            return String.valueOf(homeGoals) + " - " + String.valueOf(awayGoals);
        }
    }

    public static int getTeamCrestByTeamName(Context context, String teamName) {
        if (teamName == null) {
            return R.drawable.no_icon;
        } else if (teamName.equals(context.getString(R.string.team_name_arsenal_london))) {
            return R.drawable.arsenal;
        } else if (teamName.equals(context.getString(R.string.team_name_manchester_united))) {
            return R.drawable.manchester_united;
        } else if (teamName.equals(context.getString(R.string.team_name_swansea_city))) {
            return R.drawable.swansea_city_afc;
        } else if (teamName.equals(context.getString(R.string.team_name_leicester_city))) {
            return R.drawable.leicester_city_fc_hd_logo;
        } else if (teamName.equals(context.getString(R.string.team_name_everton))) {
            return R.drawable.everton_fc_logo1;
        } else if (teamName.equals(context.getString(R.string.team_name_west_ham_united))) {
            return R.drawable.west_ham;
        } else if (teamName.equals(context.getString(R.string.team_name_tottenham_hotspur))) {
            return R.drawable.tottenham_hotspur;
        } else if (teamName.equals(context.getString(R.string.team_name_west_bromwich_albion))) {
            return R.drawable.west_bromwich_albion_hd_logo;
        } else if (teamName.equals(context.getString(R.string.team_name_sunderland))) {
            return R.drawable.sunderland;
        } else if (teamName.equals(context.getString(R.string.team_name_stoke_city))) {
            return R.drawable.stoke_city;
        } else {
            return R.drawable.no_icon;
        }
    }
}
