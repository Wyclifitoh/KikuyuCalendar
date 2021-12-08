package com.kyssanet.kikuyucalendar.Api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface Api {
    @FormUrlEncoded
    @POST("createuser")
    Call<LoginResponse> createUser(
            @Field("account_number") String account_number,
            @Field("business_name") String business_name,
            @Field("country") String country,
            @Field("user_name") String user_name,
            @Field("email_address") String email_address,
            @Field("phone_number") String phone_number,
            @Field("address") String address,
            @Field("last_payment_date") String last_payment_date,
            @Field("next_payment_date") String next_payment_date
    );

    @FormUrlEncoded
    @POST("makepayment")
    Call<ResponseBody> makePayment(
            @Field("amount") int amount,
            @Field("email") String email,
            @Field("first_name") String first_name,
            @Field("last_name") String last_name,
            @Field("phone") String phone
    );

    @FormUrlEncoded
    @POST("getpaymentbyaccountnumber")
    Call<LoginResponse> getPaymentByAccountNumber(@Field("account_number") String str);

    @FormUrlEncoded
    @POST("userlogin")
    Call<LoginResponse> userLogin(@Field("number") String str, @Field("email") String str2);
}