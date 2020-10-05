package com.devnamme.stattracker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

class Dev {
    static final String separator = "%#";
    static final String subSeparator = "@";

    private static File logFile = null;
    private static BufferedWriter logBufferedWriter = null;


//    static String ddAssets = null;



    private static void log(String message, boolean showTime) {
        try {
            if(logFile == null || logBufferedWriter == null || !logFile.exists()) return;

            if(showTime) message = "[" + Util.getCurrentTime() + "] " + message;

            logBufferedWriter.write(message);
            logBufferedWriter.newLine();
            logBufferedWriter.flush();
        } catch(Exception ignored) {}
    }
    static void log(String message) {
        log(message, true);
    }
    static void log(String func, Exception e) {
        log("Error in " + func + ": " + e.toString());
    }
    static void createLogFile(Context context) {
        try {
            File externalDir = context.getExternalFilesDir(null);

            if(externalDir == null) throw new Exception("An unexpected error has occurred.");
            File logDir = new File(externalDir.getPath() + "/logs");
            if(!logDir.exists()) if(!logDir.mkdir()) throw new Exception("An unexpected error has occurred while trying to create the log directory.");

            File[] logFiles = logDir.listFiles();
            int highestIndex = logFiles.length;

            if(logFiles.length > 5) {
                int numDeleted = 0;
                int numRenamed = 0;
                for(File file : logFiles) {
                    if(numDeleted < logFiles.length - 5) {
                        if(file.delete())
                            numDeleted++;
                    } else {
                        if(file.renameTo(new File(logDir.getPath() + "/" + numRenamed + ".txt")))
                            numRenamed++;
                    }
                }

                highestIndex = numRenamed;
            }

            logFile = new File(logDir.getPath() + "/" + highestIndex + ".txt");
            if(!logFile.exists()) if(!logFile.createNewFile()) throw new Exception("An unexpected error has occurred while trying to create the log file.");

            logBufferedWriter = new BufferedWriter(new FileWriter(logFile));

            log(context.getString(R.string.app_name) + " (" + context.getPackageName() + ") v" + BuildConfig.VERSION_NAME, false);
            log("created new log file, date: " + Util.getCurrentDate());
        } catch(Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    static void checkDataFile(final Context context) {
        try {
            String dataFileUrl = "https://dl.dropboxusercontent.com/s/ful1n02ca0ffm8o/data.txt";

            new Util.Download(context, dataFileUrl, "", "data", new Runnable() {
                @Override
                public void run() {
                    // check if file exists
                    File dataFile = new File(context.getExternalFilesDir(null).getPath() + "/data");
                    if(!dataFile.exists())
                        createBackupDataFile(dataFile);
                    // read data file
                    readDataFile(dataFile, context);
                }
            }).execute();
        } catch(Exception e) { Dev.log("Dev.downloadDataFile", e); }
    }
    private static void createBackupDataFile(File dataFile) {
        try {
            // this should only be called if there was an error in downloading the dataFile online (dropbox) and the file does not exist
            dataFile.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(dataFile));

            String assetsLink = "http://ddragon.leagueoflegends.com/cdn";

            writer.write("DATA@V" + BuildConfig.VERSION_NAME);
            writer.newLine(); // V = version number
            writer.write("DATA@Dno-downlooad-link");
            writer.newLine(); // D = download link of new version
            writer.write("DATA@A" + LeagueOfLegends.ddAssets);
            writer.newLine(); // A = link for assets = data dragon

            writer.write("ICON@I" + LeagueOfLegends.profileIconSpriteUrl);
            writer.newLine(); // I = directory of all icons (one file sprite)
            writer.write("ICON@L" + LeagueOfLegends.ddProfileIconDir);
            writer.newLine(); // L = directory of all icons ; separate file (num).png

            writer.write("LOL@V" + LeagueOfLegends.PATCH_NUMBER);
            writer.write("@L" + LeagueOfLegends.ddLink);
            writer.write("@S" + LeagueOfLegends.CURRENT_SEASON);
            writer.write("@s" + LeagueOfLegends.CURRENT_SPLIT);
            writer.newLine(); // V = patch version ; L = data dragon link ; S = season ; s = split

            writer.write("TFT@S" + TeamfightTactics.CURRENT_SET);
            writer.write("@s" + TeamfightTactics.CURRENT_SPLIT);
            writer.newLine(); // S = season ; s = split

            writer.flush();
            writer.close();
        } catch(Exception e) { Dev.log("Dev.createBackupDataFile", e); }
    }
    private static void readDataFile(File dataFile, final Context context) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(dataFile));

            String line;

            String appVersionNumber = null;
            String newVersionDownloadLink = null;



            while((line = reader.readLine()) != null) {
                line = line.trim();

                String[] item = line.split("@");
                String label = item[0];

                if(label.equals("DATA")) {
                    for(int i = 1; i < item.length; i++) {
                        char prefix = item[i].charAt(0);
                        String data = item[i].substring(1);

                        if(prefix == 'V') appVersionNumber = data;
                        else if(prefix == 'D') newVersionDownloadLink = data;
                        else if(prefix == 'A') LeagueOfLegends.ddAssets = data;
                    }
                } else if(label.equals("ICON")) {
                    for(int i = 1; i < item.length; i++) {
                        char prefix = item[i].charAt(0);
                        String data = item[i].substring(1);

                        if(prefix == 'I') LeagueOfLegends.profileIconSpriteUrl = data;
                        else if(prefix == 'L') LeagueOfLegends.ddProfileIconDir = data;
                    }
                } else if(label.equals("LOL")) {
                    for(int i = 1; i < item.length; i++) {
                        char prefix = item[i].charAt(0);
                        String data = item[i].substring(1);

                        if(prefix == 'V') LeagueOfLegends.PATCH_NUMBER = data;
                        else if(prefix == 'L') LeagueOfLegends.ddLink = data;
                        else if(prefix == 'S')
                            LeagueOfLegends.CURRENT_SEASON = Short.parseShort(data);
                        else if(prefix == 's') LeagueOfLegends.CURRENT_SPLIT = Byte.parseByte(data);
                    }
                } else if(label.equals("TFT")) {
                    for(int i = 1; i < item.length; i++) {
                        char prefix = item[i].charAt(0);
                        String data = item[i].substring(1);

                        if(prefix == 'S') TeamfightTactics.CURRENT_SET = Short.parseShort(data);
                        else if(prefix == 's')
                            TeamfightTactics.CURRENT_SPLIT = Byte.parseByte(data);
                    }
                }
            }

            // grab data (using)

            String using_lol_patch_number = LeagueOfLegends.PATCH_NUMBER;
            short using_lol_season = LeagueOfLegends.CURRENT_SEASON;
            byte using_lol_split = LeagueOfLegends.CURRENT_SPLIT;


            File usingDataFile = new File(context.getExternalFilesDir(null).getPath() + "/using");
            if(!usingDataFile.exists()) {
                usingDataFile.createNewFile();

                BufferedWriter usingBW = new BufferedWriter(new FileWriter(usingDataFile));

                usingBW.write("LOL@V" + using_lol_patch_number);
                usingBW.write("@S" + using_lol_season);
                usingBW.write("@s" + using_lol_split);
                usingBW.newLine();

//                usingBW.write("TFT@S" + using_tft_set);
//                usingBW.write("@s" + using_tft_split);
//                usingBW.newLine();

                usingBW.flush();
                usingBW.close();
            }

            BufferedReader usingBR = new BufferedReader(new FileReader(usingDataFile));
            String usingLine;

            while((usingLine = usingBR.readLine()) != null) {
                usingLine = usingLine.trim();

                String[] item = usingLine.split("@");
                String label = item[0];

                if(label.equals("LOL")) {
                    for(int i = 1; i < item.length; i++) {
                        char prefix = item[i].charAt(0);
                        String data = item[i].substring(1);

                        if(prefix == 'V') using_lol_patch_number = data;
                        else if(prefix == 'S') using_lol_season = Short.parseShort(data);
                        else if(prefix == 's') using_lol_split = Byte.parseByte(data);
                    }
                }/* else if(label.equals("TFT")) {
                    for(int i = 1; i < item.length; i++) {
                        char prefix = item[i].charAt(0);
                        String data = item[i].substring(1);

                        if(prefix == 'S') using_tft_set = Short.parseShort(data);
                        else if(prefix == 's') using_tft_split = Byte.parseByte(data);
                    }
                }*/
            }

            // compare version of app
            if(!BuildConfig.VERSION_NAME.equals(appVersionNumber)) {
//                final String finalNewVersionDownloadLink = newVersionDownloadLink;
                new AlertDialog.Builder(context)
                        .setTitle("Update")
                        .setMessage("A new version of the app has been found. Would you like to download it?\n\nInstalled: " + BuildConfig.VERSION_NAME + "\nUpdate: " + appVersionNumber)
                        .setNegativeButton("No", null)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(context, "TODO - download new update", Toast.LENGTH_SHORT).show();
                                // TODO
                            }
                        }).create().show();
            }

            // check if has update (league)
            if(!using_lol_patch_number.equals(LeagueOfLegends.PATCH_NUMBER)) {
                File profileIconFile = new File(context.getExternalFilesDir(null).getPath() + "/assets/lol/profileicon0.png");
                if(profileIconFile.exists()) {
                    Toast.makeText(context, "TODO - dialog", Toast.LENGTH_SHORT).show();
                    // TODO
                }
            }
        } catch(Exception e) { Dev.log("Dev.readDataFile", e); }
    }

    static String IDToString(byte id) {
        switch(id) {
            case LeagueOfLegends.ID:    return "LeagueOfLegends";
            case TeamfightTactics.ID:   return "TeamfightTactics";
            case LegendsOfRuneterra.ID: return "LegendsOfRuneterra";
            case Valorant.ID:           return "Valorant";
            default:    return "?";
        }
    }

    static String BottomSheetStateToString(int state) {
        switch(state) {
            case BottomSheetBehavior.STATE_DRAGGING:    return "STATE_DRAGGING";
            case BottomSheetBehavior.STATE_SETTLING:    return "STATE_SETTLING";
            case BottomSheetBehavior.STATE_EXPANDED:    return "STATE_EXPANDED";
            case BottomSheetBehavior.STATE_COLLAPSED:   return "STATE_COLLAPSED";
            case BottomSheetBehavior.STATE_HIDDEN:      return "STATE_HIDDEN";
            case BottomSheetBehavior.STATE_HALF_EXPANDED:   return "STATE_HALF_EXPANDED";
            default:    return "?";
        }
    }
}