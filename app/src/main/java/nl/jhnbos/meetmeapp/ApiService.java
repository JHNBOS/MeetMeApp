package nl.jhnbos.meetmeapp;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Johan Bos on 30-1-2017.
 */

public interface ApiService {

    @GET("/")
    Call<User> getMyJSON();
}
