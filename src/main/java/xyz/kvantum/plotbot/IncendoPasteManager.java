package xyz.kvantum.plotbot;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface IncendoPasteManager {

  @GET("paste/view/{paste}") Call<ResponseBody> getPaste(@Path("paste") String paste, @Query("raw") boolean raw);

}
