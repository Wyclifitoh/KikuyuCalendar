package com.kyssanet.kikuyucalendar;

import androidx.appcompat.app.AppCompatActivity;

import com.kyssanet.kikuyucalendar.Api.RetrofitClient;
import com.kyssanet.kikuyucalendar.SessionManager;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivateActivity extends AppCompatActivity {
    private SessionManager prefManager;
    private Context mContext = ActivateActivity.this;
    private String email, firstname, lastname, phone;
    private AutoCompleteTextView phoneNumber, atvEmailReg, atvFirstNameReg, atvLastNameReg;
    private TextView signin;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activate);

        phoneNumber = findViewById(R.id.phoneNumber);
        atvEmailReg =  findViewById(R.id.atvEmailReg);
        atvFirstNameReg = findViewById(R.id.atvFirstNameReg);
        atvLastNameReg = findViewById(R.id.atvLastNameReg);
        progressDialog = new ProgressDialog(this);

    }

    @Override
    public void onStart() {
        super.onStart();
        if (SessionManager.getInstance(this).isLoggedIn()) {
            Intent intent = new Intent(mContext, MainActivity.class);
//            intent.setFlags(268468224);
            startActivity(intent);
        }
    }

    public void btnSignUp(View view) {
        email = atvEmailReg.getText().toString().trim();
        phone = phoneNumber.getText().toString().trim();
        firstname = atvFirstNameReg.getText().toString().trim();
        lastname = atvLastNameReg.getText().toString().trim();

        if(validateInput(email, phone, firstname, lastname)) {
            showCustomDialog(phone, email, firstname, lastname);
        }

    }


    //Function to display the custom dialog.
    public void showCustomDialog(String my_phone, String my_email, String first_name, String last_name) {
        final Dialog dialog = new Dialog(mContext);
        //We have added a title in the custom layout. So let's disable the default title.
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //The user will be able to cancel the dialog bu clicking anywhere outside the dialog.
        dialog.setCancelable(true);
        //Mention the name of the layout of your custom dialog.
        dialog.setContentView(R.layout.pay_dialog);

        //Initializing the views of the dialog.
        Button cancel = dialog.findViewById(R.id.cancel);
        Button payBtn = dialog.findViewById(R.id.payBtn);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SessionManager.getInstance(mContext).saveUserDetails(my_phone, my_email, first_name, last_name);
                startActivity(new Intent(mContext, PaymentActivity.class));
            }
        });

        dialog.show();
    }

    public void btnSignIn(View view) {

    }

    private boolean validateInput(String inEmail, String inPhone, String inFname, String inLname){
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if(inFname.isEmpty()){
            atvFirstNameReg.setError("Firstname is empty.");
            return false;
        }
        if(inLname.isEmpty()){
            atvLastNameReg.setError("Lastname is empty.");
            return false;
        }
        if(inEmail.isEmpty()){
            atvEmailReg.setError("Email is empty.");
            return false;
        }
        if(inPhone.isEmpty()){
            phoneNumber.setError("Phone number is empty.");
            return false;
        }
        if (!inEmail.matches(emailPattern)) {
            atvEmailReg.setError("Please enter correct email Address");
            return false;
        }
        if (inPhone.length() < 10) {
            phoneNumber.setError("Enter a correct mobile number");
            return false;
        }

        return true;
    }
}