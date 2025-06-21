package vn.edu.tlu.dinhcaothang.ezilish.activities;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

import vn.edu.tlu.dinhcaothang.ezilish.activities.OxfordResponse;

public interface OxfordApi {
    @Headers({
            "Accept: application/json",
            "app_id: 80ccccb7", // ID của bạn
            "app_key: 207bf2b85f06cc7ccd06e5030f33697f" // KEY của bạn
    })
    @GET("entries/en-gb/{word}")
    Call<OxfordResponse> getEnglishWord(@Path("word") String word);
}
