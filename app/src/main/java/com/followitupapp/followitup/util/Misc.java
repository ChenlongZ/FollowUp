package com.followitupapp.followitup.util;

import android.util.Log;

import java.io.CharArrayReader;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by zic on 8/25/17.
 */

public final class Misc {

    private static final String TAG = "Toolkit";

    private static final long SECONDS_IN_MILLI = 1000;
    private static final long MINUTES_IN_MILLI = SECONDS_IN_MILLI * 60;
    private static final long HOURS_IN_MILLI = MINUTES_IN_MILLI * 60;
    private static final long DAYS_IN_MILLI = HOURS_IN_MILLI * 24;

    public static Zodiac getZodiac(Calendar dateOfBirth) {
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

    public static int getAge(Calendar dateOfBirth) {
        if (dateOfBirth == null) return Integer.MAX_VALUE;      //TODO: what should be set here
        long birthTime = dateOfBirth.getTimeInMillis();
        long nowTime = new Date().getTime();
        long difference = nowTime - birthTime;
        return (int) (difference / DAYS_IN_MILLI / 365);
    }

    public enum Zodiac {
        ARIES("Aries", 3.21f, 4.20f),
        TAURUS("Taurus", 4.21f, 5.21f),
        GEMINI("Gemini", 5.22f, 6.21f),
        CANCER("Cancer", 6.22f, 7.22f),
        LEO("Leo", 7.23f, 8.22f),
        VIRGO("Virgo", 8.23f, 9.22f),
        LIBRA("Libra", 9.23f, 10.22f),
        SCORPIO("Scorpio", 10.23f, 11.21f),
        SAGITTARIUS("Sagittarius", 11.22f, 12.21f),
        CAPRICORN("Capricorn", 12.22f, 1.20f),
        AQUARIUS("Aquarius", 1.21f, 2.19f),
        PISCES("Pisces", 2.20f, 3.20f);

        public String val;
        float start, end;

        Zodiac(String val, float start, float end) {
            this.val = val;
            this.start = start;
            this.end = end;
        }

        public boolean isMySign(Calendar date) {
            if (date == null) return false;
            float floatingDate = date.get(Calendar.MONTH) + ((float) date.get(Calendar.DATE)) / 100;
            if (floatingDate < 1) {
                floatingDate += 12;
            }
            if (val.equals("Capricorn")) {
                if (start <= floatingDate || end >= floatingDate) {
                    return true;
                }
            } else {
                if (start <= floatingDate && end >= floatingDate) {
                    return true;
                }
            }
            return false;
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
