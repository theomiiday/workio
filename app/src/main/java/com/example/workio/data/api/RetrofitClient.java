package com.example.workio.data.api;

import android.content.Context;
import android.content.SharedPreferences;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

public class RetrofitClient {

    private static final String BASE_URL = "https://emsbackend123-fhame9c6dhaue7cc.southeastasia-01.azurewebsites.net/api/v1/";
    // For local development (Emulator): "http://10.0.2.2:5000/api/v1/"
    // For local development (Physical Device): "http://YOUR_PC_IP:5000/api/v1/"
    // For production (Deployed): "https://emsbackend-enh5aahkg4dcfkfs.southeastasia-01.azurewebsites.net/api/v1/"

    private static RetrofitClient instance;
    private Retrofit retrofit;
    private Context context;

    private RetrofitClient(Context context) {
        this.context = context.getApplicationContext();

        // Logging Interceptor - shows API requests/responses in Logcat
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Auth Interceptor - automatically adds token to every request
        Interceptor authInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();

                // Get token from SharedPreferences
                SharedPreferences prefs = context.getSharedPreferences("EMS", Context.MODE_PRIVATE);
                String token = prefs.getString("accessToken", null);

                // Add Authorization header if token exists
                if (token != null && !token.isEmpty()) {
                    Request newRequest = originalRequest.newBuilder()
                            .header("Authorization", "Bearer " + token)
                            .header("Content-Type", "application/json")
                            .build();
                    return chain.proceed(newRequest);
                }

                return chain.proceed(originalRequest);
            }
        };

        // OkHttp Client with interceptors
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .addInterceptor(loggingInterceptor)
                .build();

        // Retrofit Instance
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    // Singleton pattern
    public static synchronized RetrofitClient getInstance(Context context) {
        if (instance == null) {
            instance = new RetrofitClient(context);
        }
        return instance;
    }

    // Get API service
    public ApiService getApiService() {
        return retrofit.create(ApiService.class);
    }
}
