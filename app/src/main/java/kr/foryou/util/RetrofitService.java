package kr.foryou.util;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;

/**
 * Created by kim on 2018-04-17.
 */

public interface RetrofitService {
    @FormUrlEncoded
    @POST("/adm/json/query.php")
    Call<ServerPost> getPush(
            @FieldMap Map<String, String> option
    );
    @Multipart
    @POST("/adm/json/query.php")
    Call<ServerPost> FileUpload(

            @PartMap Map<String, RequestBody> params
    );
}
