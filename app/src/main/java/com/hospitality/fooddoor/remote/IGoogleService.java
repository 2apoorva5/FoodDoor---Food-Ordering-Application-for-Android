package com.hospitality.fooddoor.remote;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface IGoogleService {
    @GET
    Call<String> getAddressName(@Url String url);
}
