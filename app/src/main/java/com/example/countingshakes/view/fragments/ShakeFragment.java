package com.example.countingshakes.view.fragments;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.countingshakes.R;
import com.example.countingshakes.model.RankingItem;
import com.example.countingshakes.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.countingshakes.utils.Utils.API_PASTE_EXPIRE_DATE_VALUE;
import static com.example.countingshakes.utils.Utils.API_PASTE_PRIVATE_PARAM;
import static com.example.countingshakes.utils.Utils.API_PASTE_PRIVATE_VALUE;
import static com.example.countingshakes.utils.Utils.TAG;

/**
 * Created by cyn on 02/19/2016.
 */
public class ShakeFragment extends Fragment implements SensorEventListener {

    private final static float ZERO = 0.0f;
    private static final int COUNTING_LENGTH_MS = 6000; //6s
    private int count = 0;
    private boolean init;
    private SensorManager sensorManager;
    private float x1, x2, x3;
    private static final float ERROR = 7.0f;
    private TextView txtCounter;
    private Handler countingHandler = new Handler();
    private TextView txtInstructions;
    private TextView txtTotal;
    private Button btnRank;
    private Button btnPost;
    private RequestQueue requestQueue;
    private final static String POST_URL = "http://pastebin.com/api/api_post.php";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //Initialize volley request queue
        requestQueue = Volley.newRequestQueue(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shaker, container, false);
        final FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCounting(view);
            }
        });
        txtInstructions = (TextView) view.findViewById(R.id.txt_instructions);
        txtTotal = (TextView) view.findViewById(R.id.txt_total);
        txtTotal.setText(String.format(getString(R.string.str_counter_total), "was"));
        txtCounter = (TextView) view.findViewById(R.id.txt_counter);
        txtCounter.setText("-");
        btnRank = (Button) view.findViewById(R.id.btn_post_to_ranking);
        btnRank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setEnabled(false);
                postShakesToRanking();
            }
        });
        btnPost = (Button) view.findViewById(R.id.btn_fb_post);
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareToFacebook();
            }
        });
        return view;
    }

    /**
     * Method that let you share your score on facebook
     */
    private void shareToFacebook() {
        //TODO share the score to FB
    }

    /**
     * Method that makes a POST request in order to register a ranking item
     */
    private void postShakesToRanking() {
        final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
        //Construct the RankingItem object using the user information
        RankingItem rankingItem = new RankingItem();
//        rankingItem.setDate(new Date());
        //Convert it to JSON
        final String json = gson.toJson(rankingItem);
        Log.d(TAG, "json: " + json);
        // Create Post request to URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, POST_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.d(TAG, "onResponse: " + response);
                        Snackbar.make(btnRank, R.string.str_post_successful, Snackbar.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: ERROR!");
                btnRank.setEnabled(true);
                Snackbar.make(btnRank, R.string.str_ranking_error, Snackbar.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put(Utils.API_DEV_KEY_PARAM, Utils.API_DEV_KEY_VALUE);
                params.put(Utils.API_OPTION_PARAM, Utils.API_OPTION_VALUE);
                params.put(Utils.API_PASTE_CODE_PARAM, json);
                params.put(Utils.API_PASTE_EXPIRE_DATE_PARAM, API_PASTE_EXPIRE_DATE_VALUE);
                params.put(API_PASTE_PRIVATE_PARAM, API_PASTE_PRIVATE_VALUE);
                return params;
            }
        };

        // Add the request to the RequestQueue.
        requestQueue.add(stringRequest);
    }

    private void startCounting(final View view) {
        Log.d(TAG, "onClick: start counting shakes");
        view.setEnabled(false);
        count = 0;
        txtInstructions.setVisibility(View.INVISIBLE);
        btnRank.setVisibility(View.INVISIBLE);
        btnPost.setVisibility(View.INVISIBLE);
        txtTotal.setText(String.format(getString(R.string.str_counter_total), "is"));
        txtCounter.setText(String.valueOf(count));
        registerAccelerometerListener();
        countingHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopCounting(view);
            }
        }, COUNTING_LENGTH_MS);
    }

    private void stopCounting(final View view) {
        Log.d(TAG, "run: count shakes total >>" + count);
        view.setEnabled(true);
        txtInstructions.setVisibility(View.VISIBLE);
        btnRank.setVisibility(View.VISIBLE);
        btnRank.setEnabled(true);
        btnPost.setVisibility(View.VISIBLE);
        txtTotal.setText(String.format(getString(R.string.str_counter_total), "was"));
        unregisterAccelerometerListener();
    }

    private void registerAccelerometerListener() {
        sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        final List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (sensorList.isEmpty()) {
            //There is no accelerometer, let the user know
            Snackbar.make(txtCounter, R.string.str_accelerometer_error, Snackbar.LENGTH_SHORT).show();
        } else {
            Log.d(TAG, "onCreate: there is accelerometer");
            init = false;
            Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d(TAG, "onSensorChanged: event >> " + Arrays.toString(event.values));
        //Get x,y,z values
        float x, y, z;
        x = event.values[0];
        y = event.values[1];
        z = event.values[2];

        if (!init) {
            x1 = x;
            x2 = y;
            x3 = z;
            init = true;
        } else {
            float diffX = Math.abs(x1 - x);
            float diffY = Math.abs(x2 - y);
            float diffZ = Math.abs(x3 - z);

            //Handling accelerometer noise
            if (diffX < ERROR) {
                diffX = ZERO;
            }
            if (diffY < ERROR) {
                diffY = ZERO;
            }
            if (diffZ < ERROR) {
                diffZ = ZERO;
            }
            x1 = x;
            x2 = y;
            x3 = z;

            //Detect horizontal shake
            if (diffX > diffY) {
                Log.d(TAG, "onSensorChanged: horizontal shake detected >> " + diffX + ", " + diffY);
                count++;
                txtCounter.setText(String.valueOf(count));
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //Nothing to do
    }

    private void unregisterAccelerometerListener() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        unregisterAccelerometerListener();
        requestQueue.stop();
    }
}
