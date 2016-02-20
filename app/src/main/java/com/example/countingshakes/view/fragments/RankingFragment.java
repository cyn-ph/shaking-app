package com.example.countingshakes.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.countingshakes.R;
import com.example.countingshakes.model.RankingItem;
import com.example.countingshakes.view.adapters.RankingItemAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static com.example.countingshakes.utils.Utils.TAG;

/**
 * Created by cyn on 02/19/2016.
 */
public class RankingFragment extends ListFragment {

    private RequestQueue requestQueue;
    public static final String URL = "http://pastebin.com/raw/2jMtSAk9";
    private RankingItemAdapter rankingItemAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //Initialize volley request queue
        requestQueue = Volley.newRequestQueue(context);
        getDataFromURL();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ranking, container, false);
        //Set the adapter and a empty list ranking list, because the information is obtained asynchronously
        rankingItemAdapter = new RankingItemAdapter(getContext(), new LinkedList<RankingItem>());
        ListView listViewCities = (ListView) view.findViewById(R.id.ranking_list);
        listViewCities.setAdapter(rankingItemAdapter);
        return view;
    }

    /**
     * Method that send the http request in order to get the JSON to parse
     */
    private void getDataFromURL() {
        final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.d(TAG, "Response: " + response);
                        List<RankingItem> resultList = gson.fromJson(response,
                                new TypeToken<List<RankingItem>>() {
                                }.getType());
                        //Update the content of the view because this call is asynchronous
                        Collections.sort(resultList);
                        rankingItemAdapter.updateRankingItemList(resultList);
                        Log.d(TAG, "List: " + resultList);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "ERROR!!!");
            }
        });
        // Add the request to the RequestQueue.
        requestQueue.add(stringRequest);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        requestQueue.stop();
    }
}
