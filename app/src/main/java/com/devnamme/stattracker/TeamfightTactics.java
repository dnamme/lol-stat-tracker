package com.devnamme.stattracker;

class TeamfightTactics {
    static final byte ID = 1;
    static short CURRENT_SET = 3;
    static byte CURRENT_SPLIT = 1;

    // TODO

    static class Update extends LeagueOfLegends.Update {
        short season = TeamfightTactics.CURRENT_SET;
        byte split = TeamfightTactics.CURRENT_SPLIT;
    }
}
