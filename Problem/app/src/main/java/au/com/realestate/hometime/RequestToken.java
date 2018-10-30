package au.com.realestate.hometime;

import android.os.AsyncTask;

import java.io.IOException;

import au.com.realestate.hometime.models.ApiResponse;
import au.com.realestate.hometime.models.Token;
import retrofit2.Call;

public class RequestToken extends AsyncTask<String, Integer, String> {


    //object of tramsapi function to call
    TramsApi api;

    RequestToken(TramsApi api) {
        this.api = api;
    }
    //Background function to aysnctask
    @Override
    protected String doInBackground(String... params) {
        Call<ApiResponse<Token>> call = api.token();
        try {
            return call.execute().body().responseObject.get(0).value;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
