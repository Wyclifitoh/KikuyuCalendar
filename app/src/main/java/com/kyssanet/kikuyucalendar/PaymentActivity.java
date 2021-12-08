package com.kyssanet.kikuyucalendar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.kyssanet.kikuyucalendar.Api.RetrofitClient;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity extends AppCompatActivity implements View.OnClickListener {
    Button payWithPayPal;
    CardView cardMpesa;
    CardView cardPaypal;
    Button confirmPayment;
    LinearLayout layMpesa;
    LinearLayout layPaypal;
    LinearLayout linInstruction;
    private Context mContext = PaymentActivity.this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        initLayout();
    }

    private void initLayout() {
        Toolbar toolbar2 = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar2);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Payment");
      //  new CheckInternetConnection(this).checkConnection();
        payWithPayPal = (Button)findViewById(R.id.payWithPayPal);
        cardMpesa = (CardView)findViewById(R.id.cardMpesa);
        cardPaypal = (CardView)findViewById(R.id.cardPaypal);
        confirmPayment = (Button)findViewById(R.id.confirmPayment);
        layMpesa = (LinearLayout)findViewById(R.id.layMpesa);
        layPaypal = (LinearLayout)findViewById(R.id.layPaypal);

        layMpesa.setVisibility(View.GONE);
        cardMpesa.setOnClickListener(this);
        cardPaypal.setOnClickListener(this);
        confirmPayment.setOnClickListener(this);
        payWithPayPal.setOnClickListener(this);
    }

    public void onClick(View view2) {
        switch (view2.getId()) {
            case R.id.cardMpesa:
                this.layMpesa.setVisibility(View.VISIBLE);
                this.layPaypal.setVisibility(View.GONE);
                return;
            case R.id.cardPaypal:
                this.layMpesa.setVisibility(View.GONE);
                this.layPaypal.setVisibility(View.VISIBLE);
                return;
            case R.id.confirmPayment:
                //confirmMpesaPayment();
                return;
            case R.id.payWithPayPal:
               // makeCardPayment();
                return;
            default:
                return;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void makeCardPayment() {
        ProgressDialog dialog = new ProgressDialog(mContext);
        dialog.setCancelable(false);
        dialog.setMessage("Please Wait...");
        dialog.show();

        String email = SessionManager.getInstance(mContext).getEmail();
        String firstname = SessionManager.getInstance(mContext).getFirstName();
        String lastname = SessionManager.getInstance(mContext).getLastName();
        String phone = SessionManager.getInstance(mContext).getPhone();

        RetrofitClient.getInstance().getApi().makePayment(299, email, firstname, lastname, phone).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                dialog.hide();
                ResponseBody responseBody = response.body();
                String str;
                try {
                    if (response.code() == 201) {
                        str = response.body().string();
                        if (str != null) {
                            try {
                                JSONObject jSONObject = new JSONObject(str);
                                String url = jSONObject.getString("url");

                                if (url != null || url.equals("")) {
                                    Intent intent = new Intent(mContext, PaymentActivity.class);
                                    intent.putExtra("url", url);
                                    startActivity(intent);
                                }

                            } catch (Exception e2) {
                                e2.printStackTrace();
                            }
                        }
                    } else {
                        // Toasty.error(mContext, "Something went wrong...please try again later", Toast.LENGTH_LONG).show();
                    }

                } catch (IOException | NullPointerException e) {
                    e.printStackTrace();
                    str = null;
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dialog.hide();
                t.printStackTrace();
                // Toasty.error(mContext, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}