package au.com.realestate.hometime;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import au.com.realestate.hometime.models.ApiResponse;
import au.com.realestate.hometime.models.Token;
import au.com.realestate.hometime.models.Tram;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class MainActivity extends AppCompatActivity {


    private List<Tram> southTrams;
    private List<Tram> northTrams;

    private ListView northListView;
    private ListView southListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        northListView = (ListView) findViewById(R.id.northListView);
        southListView = (ListView) findViewById(R.id.southListView);
    }

    private interface TramsApi {

        @GET("/TramTracker/RestService/GetDeviceToken/?aid=TTIOSJSON&devInfo=HomeTimeAndroid")
        Call<ApiResponse<Token>> token();

        @GET("/TramTracker/RestService//GetNextPredictedRoutesCollection/{stopId}/78/false/?aid=TTIOSJSON&cid=2")
        Call<ApiResponse<Tram>> trams(
                @Path("stopId") String stopId,
                @Query("tkn") String token
        );
    }
    ////////////
    // API
    ////////////


    //Base url of api to get data
    private TramsApi createApiClient() {

        String BASE_URL = "http://ws3.tramtracker.com.au";
    //using retrofit Http Api library
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .build();

        return retrofit.create(TramsApi.class);
    }

    //getting token for api and adding it to base url
    private class RequestToken extends AsyncTask<String, Integer, String> {

        //object of tramsapi function to call
        TramsApi api;

        RequestToken(TramsApi api) {
            this.api = api;
        }

        //override method to return the result from api and check exceptions
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
    //verification checking token and id to get data
    private class RequestTrams extends AsyncTask<String, Integer, List<Tram>> {

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
    //OnRefresh button click
    public void refreshClick(View view) {
        //On refresh create api client to get data
        TramsApi tramsApi = createApiClient();

        try {
            String token = new RequestToken(tramsApi).execute("").get();
            //getting north side and south side tram size
            this.northTrams = new RequestTrams(tramsApi, token).execute("4055").get();
            this.southTrams = new RequestTrams(tramsApi, token).execute("4155").get();
            showTrams();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
    //On Clear it clears the previous results
    public void clearClick(View view) {
        northTrams = new ArrayList<>();
        southTrams = new ArrayList<>();
        showTrams();
    }

    //function to show trams list data
    private void showTrams() {
        //List to store values of north in north list and south in south list
        List<String> northValues = new ArrayList<>();
        List<String> southValues = new ArrayList<>();

        //getting current time using calendar import
        Calendar c = Calendar.getInstance();
        Date currentTime = c.getTime();
        //for loop max values of elements receive from api
        for (Tram tram : northTrams) {
            //Need Date format to calculate the difference
            Date dateDifferenceNorth = dateFromDotNetDate(tram.predictedArrival);
            //Converting into string to add into list
            String date = dateFromDotNetDate(tram.predictedArrival).toString();
            //Calculating difference in time to find out remaining time
            long diff = dateDifferenceNorth.getTime() - currentTime.getTime();
            //changing difference into days, hours and minutes
           int days = (int) (diff / (1000*60*60*24));
           //into hours
            int hours = (int)((diff-(1000*60*60*24*days)) / (1000*60*60));
            //into minutes
            int min = (int) (diff - (1000*60*60*24*days) - (1000*60*60*hours)) / (1000*60);
            //I can easily shows days and hours if the tram is not there and next is on next date
            northValues.add(date + "\nRemaining Time" + " " + min + " " + "Minutes" );

        }
        //for loop max values of elements receive from api
        for (Tram tram : southTrams) {
            //Need Date format to calculate the difference
            Date dateDifferenceSouth = dateFromDotNetDate((tram.predictedArrival));
            //Converting into string to add into list
            String date = dateFromDotNetDate(tram.predictedArrival).toString();
            //Calculating difference in time to find out remaining time
            long diffTime = dateDifferenceSouth.getTime() - currentTime.getTime();
            //changing difference into days, hours and minutes
            int days = (int) (diffTime / (1000*60*60*24));
            int hours = (int)((diffTime-(1000*60*60*24*days)) / (1000*60*60));
            int min = (int) (diffTime - (1000*60*60*24*days) - (1000*60*60*hours)) / (1000*60);
            //I can easily shows days and hours if the tram is not there and next is on next date
            southValues.add(date + "\nRemaining Time" + " " + min + " " + "Minutes" );
        }
        //adapter for adapter view we need to use it when using listview, grid view or spinner
        northListView.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                northValues));
//adapter for adapter view we need to use it when using listview, grid view or spinner
        southListView.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                southValues));
    }

    /////////////
    // Convert .NET Date to Date
    ////////////
    private Date dateFromDotNetDate(String dotNetDate) {

        int startIndex = dotNetDate.indexOf("(") + 1;
        int endIndex = dotNetDate.indexOf("+");
        String date = dotNetDate.substring(startIndex, endIndex);

        Long unixTime = Long.parseLong(date);
        return new Date(unixTime);
    }
}
