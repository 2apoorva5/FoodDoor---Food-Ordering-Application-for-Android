package com.hospitality.fooddoor.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hospitality.fooddoor.model.Category;
import com.hospitality.fooddoor.remote.APIService;
import com.hospitality.fooddoor.remote.IGoogleService;
import com.hospitality.fooddoor.remote.RetrofitClient;

public class Common {

    public static String EMAIL_TEXT = "userEmail";

    public static Category currentCategory;

    public static final String BASE_URL = "https://fcm.googleapis.com/";

    public static final String GOOGLE_API_URL = "https://maps.googleapis.com/";

    public static APIService getFCMService()
    {
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }

    public static IGoogleService getGoogleMapAPI()
    {
        return RetrofitClient.getGoogleClient(GOOGLE_API_URL).create(IGoogleService.class);
    }

    public static boolean isConnectedToInternet(Context context)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(connectivityManager != null)
        {
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if(info != null)
            {
                for(int i = 0; i < info.length; i++)
                {
                    if(info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}

