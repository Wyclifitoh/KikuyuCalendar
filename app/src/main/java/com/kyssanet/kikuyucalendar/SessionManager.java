package com.kyssanet.kikuyucalendar;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    public static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String PREFER_NAME = "InvoiceAndEstimate";
    private static SessionManager instance;
    public final String KEY_PASSCODE = "AppPasscode";
    public final String PAYMENT_SUBSCRIPTION_STATUS = "PaymentStatus";
    int PRIVATE_MODE = 0;
    Context _context;
    SharedPreferences.Editor editor;
    SharedPreferences pref;

    private SessionManager(Context context) {
        this._context = context;
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFER_NAME, 0);
        this.pref = sharedPreferences;
        this.editor = sharedPreferences.edit();
    }

    public static SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context);
        }
        return instance;
    }

    public int getPasscode() {
        return this.pref.getInt("AppPasscode", -1);
    }

    public void setPasscode(int i) {
        this.editor.putInt("AppPasscode", i);
        this.editor.commit();
    }

    public int getPaymentStatus() {
        return this.pref.getInt("PaymentStatus", 0);
    }

    public void setPaymentStatus(int i) {
        this.editor.putInt("PaymentStatus", i);
        this.editor.commit();
    }

    public void savePaymentDate(String str, String str2) {
        this.editor.putString("next_payment_date", str2);
        this.editor.putString("last_payment_date", str);
        this.editor.commit();
    }

    public void saveUserDetails(String phone, String email, String fname, String lname) {
        this.editor.putString("phone_number", phone);
        this.editor.putString("email_address", email);
        this.editor.putString("first_name", fname);
        this.editor.putString("last_name", lname);
        this.editor.commit();
    }

    public String getPhone() {
        return this.pref.getString("phone_number", null);
    }

    public String getEmail() {
        return this.pref.getString("email_address", null);
    }

    public String getFirstName() {
        return this.pref.getString("first_name", null);
    }

    public String getLastName() {
        return this.pref.getString("last_name", null);
    }

    public String getNextPaymentDate() {
        return this.pref.getString("next_payment_date", null);
    }

    public String getLastPaymentDate() {
        return this.pref.getString("last_payment_date", null);
    }

    public void setFirstTimeLaunch(boolean z) {
        this.editor.putBoolean(IS_FIRST_TIME_LAUNCH, z);
        this.editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return this.pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public boolean isLoggedIn() {
        return this.pref.getBoolean(IS_LOGIN, false);
    }

    public void createLoginSession() {
        this.editor.putBoolean(IS_LOGIN, true);
        this.editor.commit();
    }

}
