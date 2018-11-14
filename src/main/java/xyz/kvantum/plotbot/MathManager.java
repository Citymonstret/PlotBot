package xyz.kvantum.plotbot;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MathManager
{

	@GET("http://api.mathjs.org/v4/") Call<ResponseBody> mathify(@Query("expr") String expression);

}
