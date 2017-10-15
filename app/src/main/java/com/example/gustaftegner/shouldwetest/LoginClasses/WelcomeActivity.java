package com.example.gustaftegner.shouldwetest.LoginClasses;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.gustaftegner.shouldwetest.BackgroundBitmapDrawable;
import com.example.gustaftegner.shouldwetest.MainActivity;
import com.example.gustaftegner.shouldwetest.MainActivity2;
import com.example.gustaftegner.shouldwetest.ParseConstants;
import com.example.gustaftegner.shouldwetest.R;
import com.example.gustaftegner.shouldwetest.Utils;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

/**
 * Activity which displays a registration screen to the user.
 */
public class WelcomeActivity extends Activity {
    public static final String TAG = WelcomeActivity.class.getSimpleName();

    Button facebookLoginButton;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Log.d(TAG, "Oncreate called, WELCOME");

        Resources res = this.getResources();
        Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.citybackground);
        BackgroundBitmapDrawable background = new BackgroundBitmapDrawable(res, bitmap);
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.relativeLayoutWelcome);
        layout.setBackground(background);
        layout.setBackgroundDrawable(background);

        //getWindow().setBackgroundDrawableResource(R.drawable.citybackground);

        facebookLoginButton = (Button) findViewById(R.id.login_facebook_button);

        final List<String> permissions = Arrays.asList("public_profile", "user_friends");


        facebookLoginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseFacebookUtils.logInWithReadPermissionsInBackground(WelcomeActivity.this, permissions, new LogInCallback() {
                    @Override
                    public void done(final ParseUser user, ParseException err) {
                        if(err != null){
                            Log.d(TAG, err.toString());
                        }
                        if (user == null) {
                            Log.d("MyApp", err.toString());
                            Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                        } else if (user.isNew()) {
                            //Get profile name/image and friends
                            //progressDialog = ProgressDialog.show(getApplicationContext(), "Setting up profile", "", true);
                            GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                                    try{
                                        String birth = "19/10/1989";
                                        String name = jsonObject.getString("name");
                                        String id = jsonObject.getString("id");
                                        String gender = jsonObject.getString("gender");
                                        try {
                                            birth = jsonObject.getString("birthday");
                                        }catch (JSONException kuk){
                                            Log.d(TAG, "KUK: " + kuk.toString());
                                            birth = "1/1/1337";
                                        }
                                        //String birth = jsonObject.getString("birthday");


                                        Log.d(TAG, name + " " + id + " " + gender + " " + birth);
                                        /**String birthday = jsonObject.getString("birthday");
                                        String[] parts = birthday.split("/");
                                        int year = Integer.valueOf(parts[2]);**/



                                        user.setUsername(name);
                                        user.put(ParseConstants.KEY_FACEBOOK_ID, id);
                                        user.put(ParseConstants.KEY_FACEBOOK_GENDER, gender);
                                        user.put("dateofbirth", birth);
                                        user.put("Birthyear", 9999);
                                        user.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                //progressDialog.dismiss();
                                                if (e == null) {

                                                    Intent intent = new Intent(WelcomeActivity.this, MainActivity2.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent);
                                                }else{
                                                    Log.d(TAG, e.toString());
                                                }
                                            }
                                        });
                                    }catch (JSONException e){
                                        Log.d(TAG,"Error " +  e.toString());
                                    }
                                }
                            });


                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "id,name,link,gender");
                            request.setParameters(parameters);
                            Log.d(TAG, "executeAsync called next");
                            request.executeAsync();

                            Log.d(TAG, "User signed up and logged in through Facebook!");
                        } else {
                            Intent intent = new Intent(WelcomeActivity.this, MainActivity2.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            Log.d(TAG, "User logged in through Facebook!");


                        }
                    }
                });
            }
        });

       /** // Log in button click handler
        Button loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Starts an intent of the log in activity
                startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
            }
        });

        // Sign up button click handler
        TextView signupButton = (TextView) findViewById(R.id.signup_button);
        signupButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Starts an intent for the sign up activity
                startActivity(new Intent(WelcomeActivity.this, SignUpActivity.class));
            }
        });**/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "OnActivityFor Result called, " + requestCode + ", " + resultCode + ", " + data.toString());
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }
}
