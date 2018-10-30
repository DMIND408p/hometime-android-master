package au.com.realestate.hometime;

import android.os.AsyncTask;

import java.io.IOException;
import java.util.List;

import au.com.realestate.hometime.models.ApiResponse;
import au.com.realestate.hometime.models.Tram;
import retrofit2.Call;
import retrofit2.Response;

public class RequestTrams extends AsyncTask<String, Integer, List<Tram>> {
    private TramsApi api;
    private String token;

    RequestTrams(TramsApi api, String token) {
        this.api = api;
        this.token = token;
    }


    @Override
    protected List<Tram> doInBackground(String... stops) {
        Call<ApiResponse<Tram>> call = api.trams(stops[0], token);
        try {
            Response<ApiResponse<Tram>> resp = call.execute();
            return resp.body().responseObject;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
