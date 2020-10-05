package com.devnamme.stattracker;

import android.content.Context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;

class LeagueOfLegends {
    static final byte ID = 0;

    static String PATCH_NUMBER = "10.9";
    static short CURRENT_SEASON = 10;
    static byte CURRENT_SPLIT = 2;

    private static File externalDir = null;
    private static File dataDir = null;

    static String profileIconSpriteUrl = "img/sprite/profileicon0.png";

    static String ddAssets = "http://ddragon.leagueoflegends.com/cdn"; // dd link
    static String ddLink = "10.9.1"; // dd folder
    static String ddProfileIconDir = "img/profileicon";

    static class Account {
        String username = "?";
        int icon = 0;
        short level = 1;
        String club = "";
        String matchHistory = "";
        byte server = 0;
        byte emblemUpgrades = 0;
        
        Data_LeagueOfLegends LeagueOfLegends = new Data_LeagueOfLegends();
        Data_TeamfightTactics TeamfightTactics = new Data_TeamfightTactics();


        void setUsername(String username) { this.username = username; }
        void setIcon(int icon) { this.icon = icon; }
        void setLevel(short level) { this.level = level; }
        void setClub(String club) { this.club = club; }
        void setMatchHistory(String matchHistory) { this.matchHistory = matchHistory; }
        void setServer(byte server) { this.server = server; }
        void setEmblemUpgrades(byte emblemUpgrades) { this.emblemUpgrades = emblemUpgrades; }
        void setDataLeagueOfLegends(Data_LeagueOfLegends data) { this.LeagueOfLegends = data; }
        void setDataTeamfightTactics(Data_TeamfightTactics data) { this.TeamfightTactics = data; }

        
        static class Data_LeagueOfLegends {
            Data_ SoloDuo = new Data_();
            Data_ Flex = new Data_();

            Data_LeagueOfLegends() {}
            Data_LeagueOfLegends(Data_ soloDuoData, Data_ flexData) {
                this.SoloDuo = soloDuoData;
                this.Flex = flexData;
            }

            static class Data_ {
                Update[] data = {}; // old to current updates {season split date rank}
                Rank[] ranks = {};

                Data_() {}
                Data_(Update[] _data, Rank[] _ranks) {
                    this.data = _data;
                    this.ranks = _ranks;
                } // constructor

                Rank getCurrentRank() {
                    if(this.data.length == 0) return new Rank(); // return unranked
                    else return this.data[this.data.length - 1].rank; // get the latest update
                }

                void addData(Update update) {
                    ArrayList<Update> list = new ArrayList<>(Arrays.asList(this.data));
                    list.add(update);
                    this.data = list.toArray(this.data);
                }

                void addRank(short season, Rank rank) {
                    rank.setSeason(season);
                    ArrayList<Rank> list = new ArrayList<>(Arrays.asList(this.ranks));
                    list.add(rank);
                    this.ranks = list.toArray(this.ranks);
                }
            }

            Rank getCurrentRank() {
                Rank SOLO = this.SoloDuo.getCurrentRank();
                Rank FLEX = this.Flex.getCurrentRank();

                if(SOLO.tier > FLEX.tier) return SOLO;
                else if(SOLO.tier < FLEX.tier) return FLEX;
                // SOLO.tier == FLEX.tier
                if(SOLO.division > FLEX.division) return SOLO;
                else if(SOLO.division < FLEX.division) return FLEX;
                // SOLO.division == FLEX.division
                if(SOLO.lp > FLEX.lp) return SOLO;
                else if(SOLO.lp < FLEX.lp) return FLEX;
                // SOLO.lp == FLEX.lp
                return SOLO;
            }
        }

        static class Data_TeamfightTactics {
            Update[] data = {};
            Rank[] ranks = {};

            Data_TeamfightTactics() {}
            Data_TeamfightTactics(Update[] _data, Rank[] _ranks) {
                this.data = _data;
                this.ranks = _ranks;
            } // constructor

            Rank getCurrentRank() {
                if(this.data.length == 0) return new Rank();
                else return this.data[data.length - 1].rank;
            }

            void addData(Update update) {
                ArrayList<Update> list = new ArrayList<>(Arrays.asList(this.data));
                list.add(update);
                this.data = list.toArray(this.data);
            }
            
            void addRank(short season, Rank rank) {
                rank.setSeason(season);
                ArrayList<Rank> list = new ArrayList<>(Arrays.asList(this.ranks));
                list.add(rank);
                this.ranks = list.toArray(this.ranks);
            }
        }

        Account() {}
        
        Account(String username, int icon, short level, String club, byte server, String matchHistory, byte emblemUpgrades, Data_LeagueOfLegends lolData, Data_TeamfightTactics tftData) {
            setUsername(username);
            setIcon(icon);
            setLevel(level);
            setClub(club);
            setMatchHistory(matchHistory);
            setServer(server);
            setEmblemUpgrades(emblemUpgrades);
            setDataLeagueOfLegends(lolData);
            setDataTeamfightTactics(tftData);
        } // for adding new account
    }

    static class Rank {
        static final byte TIER_UNRANKED = 0;
        static final byte TIER_IRON = 1;
        static final byte TIER_BRONZE = 2;
        static final byte TIER_SILVER = 3;
        static final byte TIER_GOLD = 4;
        static final byte TIER_PLATINUM = 5;
        static final byte TIER_DIAMOND = 6;
        static final byte TIER_MASTER = 7;
        static final byte TIER_GRANDMASTER = 8;
        static final byte TIER_CHALLENGER = 9;

        short season = 0;
        byte tier = TIER_UNRANKED;
        byte division = 1;
        short lp = 0;
        byte[] promos = { 0, 0, 0, 0, 0 };
        byte emblemUpgrades = 0;

        Rank() {} // unranked

        Rank(short s, Rank r) {
            this.season = s;
            this.tier = r.tier;
            this.division = r.division;
            this.lp = r.lp;
            this.promos = r.promos;
        } // for loading only

        Rank(byte t, byte d) {
            this.tier = t;
            this.division = d;
        } // for emblem drawing only

        Rank(byte t, byte d, byte eu) {} // for new accounts only

        Rank(byte t, byte d, short lp) {
            this.tier = t;
            this.division = d;
            this.lp = lp;
        } // for tft

        Rank(byte t, byte d, short lp, byte[] p) {
            this.tier = t;
            this.division = d;
            this.lp = lp;
            this.promos = p;
        } // most common

        int getEmblem() {
            switch(tier) {
                case TIER_IRON:
                    if(division == 1) return R.drawable.iron_1;
                    if(division == 2) return R.drawable.iron_2;
                    if(division == 3) return R.drawable.iron_3;
                    if(division == 4) return R.drawable.iron_4;
                case TIER_BRONZE:
                    if(division == 1) return R.drawable.bronze_1;
                    if(division == 2) return R.drawable.bronze_2;
                    if(division == 3) return R.drawable.bronze_3;
                    if(division == 4) return R.drawable.bronze_4;
                case TIER_SILVER:
                    if(division == 1) return R.drawable.silver_1;
                    if(division == 2) return R.drawable.silver_2;
                    if(division == 3) return R.drawable.silver_3;
                    if(division == 4) return R.drawable.silver_4;
                case TIER_GOLD:
                    if(division == 1) return R.drawable.gold_1;
                    if(division == 2) return R.drawable.gold_2;
                    if(division == 3) return R.drawable.gold_3;
                    if(division == 4) return R.drawable.gold_4;
                case TIER_PLATINUM:
                    if(division == 1) return R.drawable.platinum_1;
                    if(division == 2) return R.drawable.platinum_2;
                    if(division == 3) return R.drawable.platinum_3;
                    if(division == 4) return R.drawable.platinum_4;
                case TIER_DIAMOND:
                    if(division == 1) return R.drawable.diamond_1;
                    if(division == 2) return R.drawable.diamond_2;
                    if(division == 3) return R.drawable.diamond_3;
                    if(division == 4) return R.drawable.diamond_4;
                case TIER_MASTER:       return R.drawable.master_0;
                case TIER_GRANDMASTER:  return R.drawable.grandmaster_0;
                case TIER_CHALLENGER:   return R.drawable.challenger_0;
                case TIER_UNRANKED:
                default:                return R.drawable.emblem_provisional;
            }
        } // returns base emblem ResId
        
        int getEmblemUpgrade(byte emblemUpgrades) {
            switch(tier) {
                case TIER_IRON:
                    if(division == 1 && emblemUpgrades == 0) return R.drawable.iron_1;
                    if(division == 2 && emblemUpgrades == 0) return R.drawable.iron_2;
                    if(division == 3 && emblemUpgrades == 0) return R.drawable.iron_3;
                    if(emblemUpgrades == 0) return R.drawable.iron_4;
                    if(emblemUpgrades == 1) return R.drawable.iron_upgrade_1;
                    if(emblemUpgrades == 2) return R.drawable.iron_upgrade_2;
                    if(emblemUpgrades == 3) return R.drawable.iron_upgrade_3;
                case TIER_BRONZE:
                    if(division == 1 && emblemUpgrades == 0) return R.drawable.bronze_1;
                    if(division == 2 && emblemUpgrades == 0) return R.drawable.bronze_2;
                    if(division == 3 && emblemUpgrades == 0) return R.drawable.bronze_3;
                    if(division == 4 && emblemUpgrades == 0) return R.drawable.bronze_4;
                    if(emblemUpgrades == 1) return R.drawable.bronze_upgrade_1;
                    if(emblemUpgrades == 2) return R.drawable.bronze_upgrade_2;
                    if(emblemUpgrades == 3) return R.drawable.bronze_upgrade_3;
                case TIER_SILVER:
                    if(division == 1 && emblemUpgrades == 0) return R.drawable.silver_1;
                    if(division == 2 && emblemUpgrades == 0) return R.drawable.silver_2;
                    if(division == 3 && emblemUpgrades == 0) return R.drawable.silver_3;
                    if(division == 4 && emblemUpgrades == 0) return R.drawable.silver_4;
                    if(emblemUpgrades == 1) return R.drawable.silver_upgrade_1;
                    if(emblemUpgrades == 2) return R.drawable.silver_upgrade_2;
                    if(emblemUpgrades == 3) return R.drawable.silver_upgrade_3;
                case TIER_GOLD:
                    if(division == 1 && emblemUpgrades == 0) return R.drawable.gold_1;
                    if(division == 2 && emblemUpgrades == 0) return R.drawable.gold_2;
                    if(division == 3 && emblemUpgrades == 0) return R.drawable.gold_3;
                    if(division == 4 && emblemUpgrades == 0) return R.drawable.gold_4;
                    if(emblemUpgrades == 1) return R.drawable.gold_upgrade_1;
                    if(emblemUpgrades == 2) return R.drawable.gold_upgrade_2;
                    if(emblemUpgrades == 3) return R.drawable.gold_upgrade_3;
                case TIER_PLATINUM:
                    if(division == 1 && emblemUpgrades == 0) return R.drawable.platinum_1;
                    if(division == 2 && emblemUpgrades == 0) return R.drawable.platinum_2;
                    if(division == 3 && emblemUpgrades == 0) return R.drawable.platinum_3;
                    if(division == 4 && emblemUpgrades == 0) return R.drawable.platinum_4;
                    if(emblemUpgrades == 1) return R.drawable.platinum_upgrade_1;
                    if(emblemUpgrades == 2) return R.drawable.platinum_upgrade_2;
                    if(emblemUpgrades == 3) return R.drawable.platinum_upgrade_3;
                case TIER_DIAMOND:
                    if(division == 1 && emblemUpgrades == 0) return R.drawable.diamond_1;
                    if(division == 2 && emblemUpgrades == 0) return R.drawable.diamond_2;
                    if(division == 3 && emblemUpgrades == 0) return R.drawable.diamond_3;
                    if(division == 4 && emblemUpgrades == 0) return R.drawable.diamond_4;
                    if(emblemUpgrades == 1) return R.drawable.diamond_upgrade_1;
                    if(emblemUpgrades == 2) return R.drawable.diamond_upgrade_2;
                    if(emblemUpgrades == 3) return R.drawable.diamond_upgrade_3;
                case TIER_MASTER:
                    if(emblemUpgrades == 0) return R.drawable.master_0;
                    if(emblemUpgrades == 1) return R.drawable.master_1;
                    if(emblemUpgrades == 2) return R.drawable.master_2;
                    if(emblemUpgrades == 3) return R.drawable.master_3;
                case TIER_GRANDMASTER:
                    if(emblemUpgrades == 0) return R.drawable.grandmaster_0;
                    if(emblemUpgrades == 1) return R.drawable.grandmaster_1;
                    if(emblemUpgrades == 2) return R.drawable.grandmaster_2;
                    if(emblemUpgrades == 3) return R.drawable.grandmaster_3;
                case TIER_CHALLENGER:
                    if(emblemUpgrades == 0) return R.drawable.challenger_0;
                    if(emblemUpgrades == 1) return R.drawable.challenger_1;
                    if(emblemUpgrades == 2) return R.drawable.challenger_2;
                    if(emblemUpgrades == 3) return R.drawable.challenger_3;
                case TIER_UNRANKED:
                default:    return R.drawable.emblem_provisional;
            }
        } // returns emblem upgrade (background) ResId

        int getEmblemUpgrade(byte emblemUpgrades, boolean useBlack) {
            int id = getEmblemUpgrade(emblemUpgrades);
            return useBlack ? (id == R.drawable.emblem_provisional ? R.drawable.emblem_unranked : id) : id;
        } // use black texture or white

        boolean backgroundUpgradeNeeded(byte emblemUpgrades) {
            return emblemUpgrades != 0 && tier != TIER_UNRANKED && tier != TIER_MASTER && tier != TIER_GRANDMASTER && tier != TIER_CHALLENGER;
        } // returns whether background resource is needed or not

        static boolean backgroundUpgradeNeeded(byte t, byte eu) {
            return eu != 0 && t != TIER_UNRANKED && t != TIER_MASTER && t != TIER_GRANDMASTER && t != TIER_CHALLENGER;
        } // returns whether background resource is needed or not

        String toDataString() {
            StringBuilder str = new StringBuilder("R");

            str.append(Dev.subSeparator + "t").append(tier);
            str.append(Dev.subSeparator + "d").append(division);
            str.append(Dev.subSeparator + "l").append(lp);
            str.append(Dev.subSeparator + "p");
            for(byte i : promos) str.append(i);
            str.append(Dev.separator + "E").append(emblemUpgrades);

            return str.toString();
        } // returns data string for saving
        
        static Rank parseRank(String dataString) {
            String[] item = dataString.split(Dev.subSeparator);

            byte T = TIER_UNRANKED;
            byte D = 1;
            short L = 0;
            byte[] P = { 0, 0, 0, 0, 0 };
            byte EU = 0;
            
            for(int i = 1; i < item.length; i++) {
                char prefix = item[i].charAt(0);
                String data = item[i].substring(1);

                if(prefix == 't')  T = Byte.parseByte(data);
                else if(prefix == 'd')  D = Byte.parseByte(data);
                else if(prefix == 'l')  L = Byte.parseByte(data);
                else if(prefix == 'p') {
                    for(int ii = 0; ii < data.length(); ii++)
                        P[ii] = Byte.parseByte(data.substring(ii, ii + 1));
                } else if(prefix == 'E') EU = Byte.parseByte(data);
            }
            
            return new Rank(T, D, L, P);
        } // returns Rank object from loading

        void setSeason(short s) {
            this.season = s;
        } // only for old seasons
    }

    static class Update {
        short season = LeagueOfLegends.CURRENT_SEASON;
        byte split = LeagueOfLegends.CURRENT_SPLIT;
        Util.Date date = null;
        Rank rank;

        Update() {}

        Update(short season, byte split, Util.Date date, Rank rank) {
            this.season = season;
            this.split = split;
            this.date = date;
            this.rank = rank;
        }
        
        String toDataString() {
            String str = "";

            str += "S" + season;
            str += Dev.separator + "s" + split;
            if(date != null)
                str += Dev.separator + "z" + date.toDateString();
            str += Dev.separator + rank.toDataString();

            return str;
        }
    }

    static Account[] accounts = {};
//    static Account[] accounts = {
//            new Account("Oreon Flux", (short) 315, (byte) 1, new Rank(Rank.TIER_DIAMOND, (byte) 4), new Rank(Rank.TIER_PLATINUM, (byte) 1)),
//            new Account("HatdogSaTinapay", (short) 3, (byte) 0, new Rank(Rank.TIER_UNRANKED, (byte) 0), new Rank(Rank.TIER_SILVER, (byte) 3)),
//            new Account("Chester Riven", (short) 100, (byte) 0, new Rank(Rank.TIER_PLATINUM, (byte) 3), new Rank(Rank.TIER_BRONZE, (byte) 4))
//    };
//     TODO

    static void addNewAccount(Account newAccount) {
        ArrayList<Account> list = new ArrayList<>(Arrays.asList(accounts));
        list.add(newAccount);
//        Dev.log("SD" + list.size());
        accounts = list.toArray(accounts);
//        Dev.log("F" + accounts.length);
    }

    private static void getDataDir(Context context) {
        try {
            externalDir = context.getExternalFilesDir(null);
            if(externalDir == null) throw new Exception("An unexpected error has occurred.");

            dataDir = new File(externalDir.getPath() + "/lol");

            if(!dataDir.exists()) if(!dataDir.mkdir()) throw new Exception("An unexpected error has occurred while trying to create the data directory.");
        } catch(Exception e) { Dev.log("LeagueOfLegends.getDataDir", e); }
    }

    static void loadData(Context context) {
        try {
            if (externalDir == null || dataDir == null) getDataDir(context);

            Dev.log("loading lol data");
            
            File[] savedAccounts = dataDir.listFiles();
            
            for(final File savedAccount : savedAccounts) {
                BufferedReader reader = new BufferedReader(new FileReader(savedAccount));
                
                Account newAccount = new Account();

                ArrayList<Update> s_up = new ArrayList<>();
                ArrayList<Update> f_up = new ArrayList<>();
                ArrayList<Update> t_up = new ArrayList<>();

                ArrayList<Rank> s_rank = new ArrayList<>();
                ArrayList<Rank> f_rank = new ArrayList<>();
                ArrayList<Rank> t_rank = new ArrayList<>();

                String line;
                while((line = reader.readLine()) != null) {
                    line = line.trim();
                    
                    String[] item = line.split(Dev.separator);
                    if(item[0].equals("ACCOUNT")) {
                        // expected
                        // ACCOUNT U(username) L(level) C(club) M(match history url) S(server) E(emblem upgrade)

                        for(int i = 1; i < item.length; i++) {
                            char prefix = item[i].charAt(0);
                            String data = item[i].substring(1);

                            if(prefix == 'U')      newAccount.setUsername(data);
                            else if(prefix == 'L') newAccount.setLevel(Short.parseShort(data));
                            else if(prefix == 'C') newAccount.setClub(data);
                            else if(prefix == 'M') newAccount.setMatchHistory(data);
                            else if(prefix == 'S') newAccount.setServer(Byte.parseByte(data));
                            else if(prefix == 'E') newAccount.setEmblemUpgrades(Byte.parseByte(data));
                        }
                    } else {
                        // expected
                        // SD/FD/TD S(season) s(split) R(rank) => t(tier) d(division) l(lp) p(promos)
                        // SR/FR/TR S(season) R(rank) => t(tier) d(division) l(lp)

                        final byte SOLO_DUO = 0, FLEX = 1, TEAMFIGHT_TACTICS = 2,
                                DATA = 5, RANK = 6;
                        
                        byte mode = -1;
                        byte type = -1;
                        
                        if(item[0].charAt(0) == 'S')        mode = SOLO_DUO;
                        else if(item[0].charAt(0) == 'F')   mode = FLEX;
                        else if(item[0].charAt(0) == 'T')   mode = TEAMFIGHT_TACTICS;
                        
                        if(item[0].charAt(1) == 'D')        type = DATA;
                        else if(item[0].charAt(1) == 'R')   type = RANK;


                        short season = 0;
                        byte split = 0;
                        Util.Date date = null;
                        Rank rank = new LeagueOfLegends.Rank();

                        for(int i = 1; i < item.length; i++) {
                            char prefix = item[i].charAt(0);
                            String data = item[i].substring(1);

                            if(prefix == 'S') season = Short.parseShort(data);
                            if(prefix == 's') split = Byte.parseByte(data);
                            if(prefix == 'z') date = new Util.Date(data);
                            else if(prefix == 'R') rank = Rank.parseRank(data);
                        }
                        
                        if(mode == SOLO_DUO && type == DATA)                s_up.add(new Update(season, split, date, rank)); //newAccount.LeagueOfLegends.SoloDuo.addData(new Update(season, split, date, rank));
                        else if(mode == SOLO_DUO && type == RANK)           s_rank.add(new Rank(season, rank)); //newAccount.LeagueOfLegends.SoloDuo.addRank(season, rank);
                        else if(mode == FLEX && type == DATA)               f_up.add(new Update(season, split, date, rank)); //newAccount.LeagueOfLegends.Flex.addData(new Update(season, split, date, rank));
                        else if(mode == FLEX && type == RANK)               f_rank.add(new Rank(season, rank)); //newAccount.LeagueOfLegends.Flex.addRank(season, rank);
                        else if(mode == TEAMFIGHT_TACTICS && type == DATA)  t_up.add(new Update(season, split, date, rank)); //newAccount.TeamfightTactics.addData(new Update(season, split, date, rank));
                        else if(mode == TEAMFIGHT_TACTICS && type == RANK)  t_rank.add(new Rank(season, rank)); //newAccount.TeamfightTactics.addRank(season, rank);
                    }
                }

                newAccount.LeagueOfLegends.SoloDuo.data = s_up.toArray(newAccount.LeagueOfLegends.SoloDuo.data);
                newAccount.LeagueOfLegends.SoloDuo.ranks = s_rank.toArray(newAccount.LeagueOfLegends.SoloDuo.ranks);
                newAccount.LeagueOfLegends.Flex.data = f_up.toArray(newAccount.LeagueOfLegends.Flex.data);
                newAccount.LeagueOfLegends.Flex.ranks = f_rank.toArray(newAccount.LeagueOfLegends.Flex.ranks);
                newAccount.TeamfightTactics.data = t_up.toArray(newAccount.TeamfightTactics.data);
                newAccount.TeamfightTactics.ranks = t_rank.toArray(newAccount.TeamfightTactics.ranks);

                LeagueOfLegends.addNewAccount(newAccount);
            }

            Dev.log("finished loading league of legends data");
        } catch(Exception e) { Dev.log("LeagueOfLegends.loadData", e); }
    }

    static void saveData(Context context) {
        try {
            Dev.log("saving league of legends data");

            if (externalDir == null || dataDir == null) getDataDir(context);

            for(int i = 0; i < accounts.length; i++) {
                final Account account = accounts[i];

                File dataFile = new File(dataDir.getPath() + "/" + Util.addPreZeroes(String.valueOf(i), 2) + ".account");
                if(!dataFile.exists()) if(!dataFile.createNewFile()) throw new Exception("An error has occurred while trying to create the data file");

                BufferedWriter writer = new BufferedWriter(new FileWriter(dataFile));

                // BASIC INFORMATION
                writer.write("ACCOUNT");
                writer.write(Dev.separator + "U" + account.username);
                writer.write(Dev.separator + "L" + account.level);
                writer.write(Dev.separator + "C" + account.club);
                writer.write(Dev.separator + "M" + account.matchHistory);
                writer.write(Dev.separator + "S" + account.server);
                writer.write(Dev.separator + "E" + account.emblemUpgrades);

                writer.newLine();
                writer.flush();
                
                // SOLO DUO DATA
                for(final Update update : account.LeagueOfLegends.SoloDuo.data) {
                    writer.write("SD" + Dev.separator + update.toDataString());
                    writer.newLine();
                    writer.flush();
                }

                // SOLO DUO RANK
                for(final Rank rank : account.LeagueOfLegends.SoloDuo.ranks) {
                    writer.write("SR" + Dev.separator + rank.toDataString());
                    writer.newLine();
                    writer.flush();
                }

                // FLEX DATA
                for(final Update update : account.LeagueOfLegends.Flex.data) {
                    writer.write("FD" + Dev.separator + update.toDataString());
                    writer.newLine();
                    writer.flush();
                }

                // FLEX RANK
                for(final Rank rank : account.LeagueOfLegends.Flex.ranks) {
                    writer.write("FR" + Dev.separator + rank.toDataString());
                    writer.newLine();
                    writer.flush();
                }

                // TEAMFIGHT TACTICS DATA
                for(final Update update : account.TeamfightTactics.data) {
                    writer.write("TD" + Dev.separator + update.toDataString());
                    writer.newLine();
                    writer.flush();
                }

                // TEAMFIGHT TACTICS RANK
                for(final Rank rank : account.TeamfightTactics.ranks) {
                    writer.write("TR" + Dev.separator + rank.toDataString());
                    writer.newLine();
                    writer.flush();
                }


                writer.close();
            }

            Dev.log("finished saving league of legends data");
        } catch(Exception e) { Dev.log("LeagueOfLegends.saveData", e); }
    }
}