package com.followitupapp.followitup.models;

import com.followitupapp.followitup.util.Misc;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by zic on 8/25/17.
 */

public class User {

    public static final String TAG = User.class.getSimpleName();

    private static Locale loc = Locale.getDefault();
    public String email;
    public String userId;
    public Calendar dateOfBirth;
    public boolean sex;
    public int age;
    public Misc.Zodiac zodiac;

    public User() {
    }

    public User(String email, String userId, String dataOfBirth, boolean male) {
        this.email = email;
        this.userId = userId;
        String[] dob = dataOfBirth.split("-");
        this.dateOfBirth = new GregorianCalendar(Integer.parseInt(dob[0]),
                Integer.parseInt(dob[1]),
                Integer.parseInt(dob[2]));
        this.age = Misc.getAge(this.dateOfBirth);
        this.zodiac = Misc.getZodiac(this.dateOfBirth);
        this.sex = male;
    }

    public String getEmail() {
        return this.email;
    }

    public String getUserId() {
        return this.userId;
    }

    public int getAge() {
        return this.age;
    }

    public String getZodiac() {
        if (this.zodiac != null) {
            return this.zodiac.val;
        } else {
            return "";
        }
    }

    public boolean getSex() {
        return this.sex;
    }

    public long getDateOfBirth() {
        return this.dateOfBirth.getTimeInMillis();
    }

    public void setEmail(String Email) {
        this.email = email;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setZodiac(String zodiac) {
        this.zodiac = Misc.getZodiac(zodiac);
    }

    public void setSex(boolean male) {
        this.sex = male;
    }
}
