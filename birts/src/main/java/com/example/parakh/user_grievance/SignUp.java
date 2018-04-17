package com.example.parakh.user_grievance;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.content.Context;
import android.widget.Toast;
import android.support.design.widget.TextInputLayout;

import android.app.ProgressDialog;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by parakh
 */

public class SignUp extends AppCompatActivity {
    Context context = this;
    Bundle parameters;
    ProgressDialog progressDialog;
    String email,pass,name,confirm_pass,contact,enroll_no;
    private static final String TAG = "SignUp";
    String URL_FOR_REGISTRATION = Constants.SERVER+"/users";
    int test = 0;
    TextInputLayout et_name,et_email,et_pass,et_confirm_pass,et_enroll_no;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        et_name = (TextInputLayout) findViewById(R.id.editText);
        et_email = (TextInputLayout) findViewById(R.id.editText2);
        et_pass = (TextInputLayout) findViewById(R.id.editText3);
        et_confirm_pass = (TextInputLayout) findViewById(R.id.editText5);
        et_enroll_no = (TextInputLayout) findViewById(R.id.editText6);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        Button register = (Button)findViewById(R.id.button6);
        parameters = new Bundle();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = et_name.getEditText().getText().toString();
                email = et_email.getEditText().getText().toString();
                pass = et_pass.getEditText().getText().toString();
                confirm_pass = et_confirm_pass.getEditText().getText().toString();
                enroll_no = et_enroll_no.getEditText().getText().toString();
                if(pass.equals(confirm_pass)) {
                    parameters.putString("name", name);
                    parameters.putString("email", email);
                    submitForm();

                }
                else
                {
                    Toast toast = Toast.makeText(context,"Passwords Don't Match!", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
    contact="1234567890";


    }
    private void submitForm() {



        registerUser(name,email,pass,contact,enroll_no);
    }

    private void registerUser(final String name,  final String email, final String password,final String contact, final String enroll_no) {
        // Tag used to cancel the request
        String cancel_req_tag = "register";

        progressDialog.setMessage("Adding you ...");
        showDialog();
       /* if(test == 0) {
            URL_FOR_REGISTRATION+="?name="+name+"&contact="+contact+"&email="+email+"&password="+password;
            test = 1;
        }*/
        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_FOR_REGISTRATION, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
              //  Toast.makeText(getApplicationContext(),"Register Response: " + response.toString(), Toast.LENGTH_SHORT).show();
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                 //   boolean error = jObj.getBoolean("error");
                    String status = jObj.getString("status");
                    if (status.equals("success")) {
                        Toast.makeText(getApplicationContext(), "Hi " + name +", You are successfully Added! Now goto your HOD for verification", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(context, MainActivity.class);
                        startActivity(intent);
                        /*// Launch login activity
                        Intent intent = new Intent(
                                RegisterActivity.this,
                                LoginActivity.class);
                        startActivity(intent);
                        finish();*/
                    } else {

                        String errorMsg = jObj.getString("error_message");
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
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),"Error:"+
                        "Please Check your Internet Connection!", Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", name);
                params.put("contact",contact);
                params.put("email", email);
                params.put("password", password);
                params.put("enroll_no", enroll_no);
                return params;
            }
        };
        // Adding request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(strReq, cancel_req_tag);
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

