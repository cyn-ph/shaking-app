package com.example.countingshakes.view.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.countingshakes.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import java.util.Set;

import static com.example.countingshakes.utils.Utils.TAG;

/**
 * Created by cyn on 02/01/2016.
 */
public class SignInFragment extends Fragment {

    private CallbackManager mCallbackManager;
    private LoginButton btnFbSignIn;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Create the CallbackManager
        mCallbackManager = CallbackManager.Factory.create();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        btnFbSignIn = (LoginButton) view.findViewById(R.id.btn_fb_sign_in);
        btnFbSignIn.setReadPermissions("email");
        btnFbSignIn.setReadPermissions("public_profile");
        btnFbSignIn.setFragment(this);

        btnFbSignIn.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookSignIn(loginResult);
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "FB login result >> onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "FB login result >> onError");
            }
        });

        return view;
    }

    private void handleFacebookSignIn(LoginResult loginResult) {
        Log.d(TAG, "FB login result >> onSuccess");
        AccessToken accessToken = loginResult.getAccessToken();
        Set<String> recentlyGrantedPermissions = loginResult.getRecentlyGrantedPermissions();
        Set<String> recentlyDeniedPermissions = loginResult.getRecentlyDeniedPermissions();

        // App code
        Log.d(TAG, "onSuccess -------- " + accessToken);
        Log.d(TAG, "Token -------- " + accessToken.getToken());
        Log.d(TAG, "Granted Permision -------- " + recentlyGrantedPermissions);
        Log.d(TAG, "Denied Permision -------- " + recentlyDeniedPermissions);

        //Profile profile = Profile.getCurrentProfile();
        //Log.d(PSConstants.TAG, "ProfileDataNameF -- " + profile.getFirstName());
        //Log.d(PSConstants.TAG, "ProfileDataNameL -- " + profile.getLastName());

        //Log.d(PSConstants.TAG, "Image URI -- " + profile.getLinkUri());

        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        // Application code
                        Log.d(TAG, "GraphResponse ------------- " + response.toString());
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "email,name");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }


}
