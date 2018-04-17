package com.example.parakh.user_grievance;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;



public class VerificationFragment extends Fragment {
    String status,accessToken,secretKey;

    Bundle b;
    ProgressDialog progressDialog;
    TextInputLayout et_otp;
    Context context;


    public VerificationFragment() {
        // Required empty public constructor
    }

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
          }

       @Override
       public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.activity_verifiaction, container, false);
        context = getContext();
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
          // final EditText current = ((TextInputLayout) v.findViewById(R.id.editText2)).getEditText();
          // final TextInputLayout text = (TextInputLayout)v.findViewById(R.id.editText2);

        return v;
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
