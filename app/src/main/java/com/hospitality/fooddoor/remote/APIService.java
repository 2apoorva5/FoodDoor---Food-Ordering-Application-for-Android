package com.hospitality.fooddoor.remote;

import com.hospitality.fooddoor.model.MyResponse;
import com.hospitality.fooddoor.model.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAANK7CapU:APA91bGE6yrHFnH52kbgW67cI3bIKQnGIIqd03HJoctpGfUqD2FYETsVDgQ7C7no9irLMeIXrhZXT0ByHW-bhkgCr86cG9FEspdOzWFumJSCJMa24rgVbI3ZRVvRrawg8nuJDzJW_1aB"
            }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);

}
