package com.followitupapp.followitup.util;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by zic on 8/25/17.
 */

public final class Misc {

    public static final String TAG = "Toolkit";

    static final long SECONDS_IN_MILLI = 1000;
    static final long MINUTES_IN_MILLI = SECONDS_IN_MILLI * 60;
    static final long HOURS_IN_MILLI = MINUTES_IN_MILLI * 60;
    static final long DAYS_IN_MILLI = HOURS_IN_MILLI * 24;

    public static Zodiac getZodiac(Date dateOfBirth) {
        if (dateOfBirth == null) return null;
        for (Zodiac sign : Zodiac.signs) {
            if (sign.isMySign(dateOfBirth)) {
                return sign;
            }
        }
        return null;
    }

    public static Zodiac getZodiac(String zodiac) {
        for (Zodiac sign : Zodiac.signs) {
            if (sign.val.equals(zodiac)) {
                return sign;
            }
        }
        return null;
    }

    public static int getAge(Date dateOfBirth) {
        if (dateOfBirth == null) return Integer.MAX_VALUE;      //TODO: what should be set here
        Date now = new Date();
        long birthTime = dateOfBirth.getTime();
        long nowTime = now.getTime();
        long difference = nowTime - birthTime;
        return (int) (difference / DAYS_IN_MILLI % 365);
    }

    public enum Zodiac {
        ARIES("Aries", "3/21", "4/20"),
        TAURUS("Taurus", "4/21", "5/21"),
        GEMINI("Gemini", "5/22", "6/21"),
        CANCER("Cancer", "6/22", "7/22"),
        LEO("Leo", "7/23", "8/22"),
        VIRGO("Virgo", "8/23", "9/22"),
        LIBRA("Libra", "9/23", "10/22"),
        SCORPIO("Scorpio", "10/23", "11/21"),
        SAGITTARIUS("Sagittarius", "11/22", "12/21"),
        CAPRICORN("Capricorn", "12/22", "1/20"),
        AQUARIUS("Aquarius", "1/21", "2/19"),
        PISCES("Pisces", "2/20", "3/20");
        public String val, start, end;

        Zodiac(String val, String start, String end) {
            this.val = val;
            this.start = start;
            this.end = end;
        }

        public boolean isMySign(Date date) {
            if (date == null) return false;
            try {
                Date startDate = new SimpleDateFormat("MM/dd/YYYY").parse(start + "/" + date.getYear());
                Date endDate = new SimpleDateFormat("MM/dd/YYYY").parse(end + "/" + date.getYear());
                if (val.equals("Capricorn")) {
                    if (startDate.before(date) || endDate.after(date)) {
                        return true;
                    }
                } else {
                    if (startDate.before(date) && endDate.after(date)) {
                        return true;
                    }
                }
                return false;
            } catch (ParseException pe) {
                Log.e(TAG, "failed parsing date...");
                return false;
            }

        }
        public static List<Zodiac> signs = new LinkedList<>();

        static {
            signs.add(AQUARIUS);
            signs.add(PISCES);
            signs.add(ARIES);
            signs.add(TAURUS);
            signs.add(GEMINI);
            signs.add(CANCER);
            signs.add(LEO);
            signs.add(VIRGO);
            signs.add(LIBRA);
            signs.add(SCORPIO);
            signs.add(SAGITTARIUS);
            signs.add(CAPRICORN);
        }
    }
}
