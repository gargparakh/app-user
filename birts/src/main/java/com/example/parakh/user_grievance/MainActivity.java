package com.example.parakh.user_grievance;
import android.support.design.widget.TextInputLayout;
import android.content.Context;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.facebook.CallbackManager;

import java.util.Map;

import android.util.Log;
import java.util.HashMap;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import android.widget.Toast;
import android.widget.Button;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import android.view.View;
import android.app.ProgressDialog;
import android.content.Intent;
import android.widget.TextView;

import com.android.volley.VolleyError;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity{

    Context context=this;
    TextInputLayout tv_email,tv_pass;
    String secretKey;
    String accessToken;
    String status;
    private static final String URL_FOR_LOGIN = Constants.SERVER+"/auth/user_login";

    private static final String TAG = "SignInActivity";
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        tv_email = (TextInputLayout)findViewById(R.id.textView4);
        tv_pass = (TextInputLayout)findViewById(R.id.textView5);

        TextView tv_signup = (TextView)findViewById(R.id.signup_link);
        tv_signup.setClickable(true);
        tv_signup.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        tv_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,SignUp.class);
                startActivity(intent);
            }
        });

        Button login = (Button)findViewById(R.id.login_button);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 String email = tv_email.getEditText().getText().toString();
                 String pass = tv_pass.getEditText().getText().toString();
                 loginUser(email,pass);

            }
        });
    }


    private void loginUser( final String email, final String password) {

        String cancel_req_tag = "login";
        progressDialog.setMessage("Logging you in...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_FOR_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();
                JSONObject jObj = null;
                try {
                    jObj = new JSONObject(response);
                    status = jObj.getString("status");
                    if (status != null && status.equals("success")) {
                        accessToken = null;
                        accessToken = jObj.getString("access_token");
                        secretKey = jObj.getString("secret_key");
                        String user = jObj.getString("user_name");
                        String enroll_no = jObj.getString("enroll_no");
                        //boolean verified = jObj.getBoolean("verified");
                        Intent intent = new Intent(
                                            MainActivity.this,
                                            HomePage.class);
                        Bundle b = new Bundle();
                        b.putString("name", user);
                        b.putString("email", email);
                        b.putString("enroll_no",enroll_no);
                        b.putString("accessToken",accessToken);
                        b.putString("secretKey",secretKey);
                        b.putString("password",password);
                       // b.putBoolean("verified",verified);
                        intent.putExtras(b);
                        startActivity(intent);
                        finish();
                    }

                    else {

                        String errorMsg = null;
                        try {
                            errorMsg = jObj.getString("error_message");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),"Please Check Your Internet Connection!", Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };
        // Adding request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(strReq,cancel_req_tag);
    }

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }
    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }
}
