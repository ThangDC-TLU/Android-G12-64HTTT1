package vn.edu.tlu.dinhcaothang.ezilish.utils;



import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import java.util.List;

public interface FreeDictionaryApi {
    @GET("api/v2/entries/en/{word}")
    Call<List<FreeDictionaryResponse>> getWord(@Path("word") String word);
}