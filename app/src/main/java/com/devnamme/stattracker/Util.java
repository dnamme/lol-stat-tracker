package com.devnamme.stattracker;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.DisplayMetrics;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

class Util {
    static class Date {
        Calendar calendar;
        int year;
        int month;
        int day;

        Date() {
            this.year = 1000;
            this.month = 10;
            this.day = 10;
        }

        Date(String dateString) {
            // YYYYMMDD
            // 01234567

            this.year = Integer.parseInt(dateString.substring(0, 4));
            this.month = Integer.parseInt(dateString.substring(4, 6));
            this.day = Integer.parseInt(dateString.substring(6));
        }

        Date(int year, int month, int day) {
            this.year = year;
            this.month = month;
            this.day = day;
        }

        String toDateString() {
            return year + addPreZeroes(Integer.toString(month), 2) + addPreZeroes(Integer.toString(day), 2);
        }

        static Util.Date getCurrentDateObj() {
            Calendar cal = Calendar.getInstance();

            return new Util.Date(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        }
        static String getCurrentDate() { return getCurrentDate(true, true, true); }
        static String getCurrentDate(boolean includeYear, boolean includeMonth, boolean includeDay) {
            Calendar calendar = Calendar.getInstance();

            String YY = Util.addPreZeroes(String.valueOf(calendar.get(Calendar.YEAR)), 4);
            String MM = Util.addPreZeroes(String.valueOf(calendar.get(Calendar.MONTH) + 1), 2);
            String DD = Util.addPreZeroes(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)), 2);

            StringBuilder dateString = new StringBuilder();

            if(includeYear) dateString.append(YY);
            if(includeMonth) dateString.append(MM);
            if(includeDay) dateString.append(DD);

            return dateString.toString();
        }

        static String getCurrentTime() { return getCurrentTime(true, true, true, true); }
        static String getCurrentTime(boolean includeHour, boolean includeMinute, boolean includeSeconds, boolean includeMilliseconds) {
            Calendar calendar = Calendar.getInstance();

            String HR = Util.addPreZeroes(String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)), 2);
            String MIN = Util.addPreZeroes(String.valueOf(calendar.get(Calendar.MINUTE)), 2);
            String SEC = Util.addPreZeroes(String.valueOf(calendar.get(Calendar.SECOND)), 2);
            String MS = Util.addPreZeroes(String.valueOf(calendar.get(Calendar.MILLISECOND)), 3);

            StringBuilder timeString = new StringBuilder();

            if(includeHour) timeString.append(timeString.length() != 0 ? ":" : "").append(HR);
            if(includeMinute) timeString.append(timeString.length() != 0 ? ":" : "").append(MIN);
            if(includeSeconds) timeString.append(timeString.length() != 0 ? ":" : "").append(SEC);
            if(includeMilliseconds) timeString.append(timeString.length() != 0 ? ":" : "").append(MS);

            return timeString.toString();
        }
    }

    static class Download extends AsyncTask<Void, Void, Void> {
        String _url = "";
        File newFile;

        Runnable postExecuteRunnable = null;
        boolean hasError = false;
        boolean isNewFile = true;

        Download(Context context, String url, String location, String fileName, Runnable postExecuteRunnable) {
            try {
                this._url = url;

                File dir = new File(context.getExternalFilesDir(null).getPath() + "/" + location);
                if(!dir.exists()) dir.mkdirs();

                newFile = new File(dir.getPath() + "/" + fileName);
                if(newFile.exists()) isNewFile = false;

                this.postExecuteRunnable = postExecuteRunnable;
            } catch(Exception e) { Dev.log("Util.Download", e); }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Dev.log("downloading file [" + newFile.getPath() + "] from [" + _url + "]");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL url = new URL(_url);
                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                c.setRequestMethod("GET");
//                c.setDoOutput(true);
                c.connect();

                newFile.createNewFile();

                FileOutputStream fos = new FileOutputStream(newFile);

                InputStream is = c.getInputStream();

                byte[] buffer = new byte[1024];
                int len1 = 0;
                while((len1 = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len1);
                }

                fos.close();
                is.close();
            } catch(Exception e) {
                Dev.log("Util.Download.doInBackground", e);

                hasError = true;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(hasError)
                Dev.log("Util.Download.onPostExecute: finished with error");
            else
                Dev.log("Util.Download.onPostExecute: finished downloading file [" + newFile.getName() + "]");

            if(hasError && newFile.exists())
                newFile.delete();

            if(postExecuteRunnable != null)
                postExecuteRunnable.run();
        }
    }

    static int convertDpToPx(Context ctx, float dp) {
        Resources res = ctx.getResources();
        DisplayMetrics displayMetrics = res.getDisplayMetrics();
        return (int) (dp * (displayMetrics.densityDpi) / 160.0F);
    }

    static float convertPxToDp(Context ctx, float px) {
        Resources res = ctx.getResources();
        DisplayMetrics displayMetrics = res.getDisplayMetrics();
        return px / (displayMetrics.densityDpi / 160.0F);
    }

    static int getWindowHeightPx(Context ctx) {
        Resources res = ctx.getResources();
        DisplayMetrics displayMetrics = res.getDisplayMetrics();

        return displayMetrics.heightPixels;
    }

    static final class ScaledDimensions {
        static final byte GIVEN_WIDTH = 0;
        static final byte GIVEN_HEIGHT = 1;

        int width = 0;
        int height = 0;

        ScaledDimensions(int width, int height) {
            this.width = width;
            this.height = height;
        }

        int getScaledDimension(byte mode, int dimension) {
            if(mode != GIVEN_WIDTH && mode != GIVEN_HEIGHT) return 0;
            return dimension * (mode == GIVEN_WIDTH ? height : width) / (mode == GIVEN_WIDTH ? width : height);
        }
    }

    static String addPreZeroes(String string, int length) {
        if(string.length() >= length) return string;

        StringBuilder returnString = new StringBuilder();
        for(int i = 1; i <= length - string.length(); i++) returnString.append("0");

        returnString.append(string);
        return returnString.toString();
    }

    static String getCurrentDate() {
        return Util.Date.getCurrentDate();

//        Calendar calendar = Calendar.getInstance();
//
//        String YY = Util.addPreZeroes(String.valueOf(calendar.get(Calendar.YEAR)), 4);
//        String MM = Util.addPreZeroes(String.valueOf(calendar.get(Calendar.MONTH) + 1), 2);
//        String DD = Util.addPreZeroes(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)), 2);
//
//        return YY + " " + MM + " " + DD;
    }

    static String getCurrentTime() {
        return Util.Date.getCurrentTime();

//        Calendar calendar = Calendar.getInstance();
//
//        String HR = Util.addPreZeroes(String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)), 2);
//        String MIN = Util.addPreZeroes(String.valueOf(calendar.get(Calendar.MINUTE)), 2);
//        String SEC = Util.addPreZeroes(String.valueOf(calendar.get(Calendar.SECOND)), 2);
//        String MS = Util.addPreZeroes(String.valueOf(calendar.get(Calendar.MILLISECOND)), 3);
//
//        return HR + ":" + MIN + ":" + SEC + ":" + MS;
    }
}
