package com.example.stocklookup;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {
RecyclerView recyclerView;
SearchView searchView;
ArrayList<Stock> stocks=new ArrayList<>();
RecyclerAdapter recyclerAdapter=new RecyclerAdapter(MainActivity.this,stocks);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(),3));
        searchView=findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                recyclerAdapter.getFilter().filter(newText);
                return true;
            }
        });

        ApiCall();

    }

    private void ApiCall() {
        AndroidNetworking.initialize(getApplicationContext());
        AndroidNetworking
                .get("https://yahoo-finance15.p.rapidapi.com/api/v1/markets/options/most-active?type=STOCKS")
                .addHeaders("x-rapidapi-key", "7b954fcb12msh229db1f90919780p10c655jsn5e40f3bcd1d7")
                .addHeaders("x-rapidapi-host", "yahoo-finance15.p.rapidapi.com")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response",response.toString());
                        try {
                            JSONArray arr=response.getJSONArray("body");
                            for (int i=0;i<arr.length();i++)
                            {
                                JSONObject stock=arr.getJSONObject(i);
                                stocks.add(new Stock(i,stock.getString("symbol")));
                                recyclerAdapter.notifyDataSetChanged();
                            }
//                            new Handler().postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    RecyclerAdapter recyclerAdapter=new RecyclerAdapter(getApplicationContext(),stocks);
//                                    recyclerView.setAdapter(recyclerAdapter);
//                                }
//                            },10000);
//                            RecyclerAdapter recyclerAdapter=new RecyclerAdapter(MainActivity.this,stocks);
                            recyclerView.setAdapter(recyclerAdapter);

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });
    }
}