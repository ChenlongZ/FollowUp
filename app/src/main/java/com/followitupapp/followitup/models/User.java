package com.followitupapp.followitup.models;

import android.util.Log;

import com.followitupapp.followitup.util.Misc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by zic on 8/25/17.
 */

public class User {

    public static final String TAG = User.class.getSimpleName();

    private static Locale loc = Locale.getDefault();
    public String email;
    public String userId;
    public int age;
    public boolean sex;
    public Misc.Zodiac zodiac;
    public Date dateOfBirth;

    public User() {}

    public User(String email, String userId, String dataOfBirth, boolean male) {
        this.email = email;
        this.userId = userId;
        try {
            this.dateOfBirth = new SimpleDateFormat("yyyy-MM-dd", loc).parse(dataOfBirth);
        } catch (ParseException pe) {
            Log.e(TAG, "Parse user data of birth error: ", pe);
            this.dateOfBirth = null;
        }
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

    public String getDateOfBirth() {
        return this.dateOfBirth.toString();
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
