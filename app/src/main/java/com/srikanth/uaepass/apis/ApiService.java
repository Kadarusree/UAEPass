package com.srikanth.uaepass.apis;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("getToken")
    Call<TokenResponse> getToken(@Query("authCode") String authCode);

    @GET("getUserInfo")
    Call<UserProfileResponse> getUserInfo(@Query("authToken") String accessToken);
}