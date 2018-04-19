package com.example.parakh.user_grievance;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.UiThread;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ChatActivity extends AppCompatActivity {

    private EditText messageET;
    private ListView messagesContainer;
    private ImageButton sendBtn;
    private ChatAdapter adapter;
    private ArrayList<ChatMessage> chatHistory;
    ProgressDialog progressDialog;
    Bundle b;

    private static final String URL_FOR_CHAT_HISTORY = Constants.SERVER+"/complaint_chats/show_complaint_chats";
    private static final String URL_FOR_CREATE_CHAT = Constants.SERVER+"/complaint_chats/create_chat";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        b = getIntent().getExtras();
        initControls();
    }

    private void initControls() {
        messagesContainer = (ListView) findViewById(R.id.messagesContainer);
        messageET = (EditText) findViewById(R.id.messageEdit);
        sendBtn = (ImageButton) findViewById(R.id.chatSendButton);

        loadChatHistory();

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageET.getText().toString();
                if (TextUtils.isEmpty(messageText)) {
                    return;
                }

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setChats(messageText);
                chatMessage.setCreatedAt(new Date());
                chatMessage.setRole("user");

                messageET.setText("");

                displayMessage(chatMessage);

                sendNewMessageToServer(chatMessage);
            }
        });

        refreshChat();
    }

    @UiThread
    public void refreshChat() {
        Timer timer = new Timer ();
        TimerTask hourlyTask = new TimerTask () {
            @Override
            public void run () {
                getNewChats();
            }
        };
        timer.schedule (hourlyTask, 0l, 10000);

    }
    public void displayMessage(ChatMessage message) {
        adapter.add(message);
        adapter.notifyDataSetChanged();
        scroll();
    }

    private void scroll() {
        messagesContainer.setSelection(messagesContainer.getCount() - 1);
    }

    private void loadChatHistory(){

        String cancel_req_tag = "register";
        chatHistory = new ArrayList<>();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_FOR_CHAT_HISTORY, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("Form", "Register Response: " + response.toString());
                if(response!=null) {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for(int i=0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            ChatMessage chatMessage = new ChatMessage();
                            chatMessage.setId(jsonObject.getInt("id"));
                            chatMessage.setChats(jsonObject.getString("chats"));
                            chatMessage.setRole(jsonObject.getString("role"));
                            chatMessage.setComplaintsId(jsonObject.getInt("complaint_id"));
                            String createdAt = jsonObject.getString("created_at");
                            String createdAt1 = createdAt.replace("T", " ");
                            String createdAt2 = createdAt1.replace("+05:30", "");
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                            try {
                                chatMessage.setCreatedAt(formatter.parse(createdAt2));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            chatHistory.add(chatMessage);
                        }
                        adapter = new ChatAdapter(ChatActivity.this, new ArrayList<ChatMessage>());
                        messagesContainer.setAdapter(adapter);

                        for(int i=0; i<chatHistory.size(); i++) {
                            ChatMessage message = chatHistory.get(i);
                            displayMessage(message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Form", "Registration Error: " + error.getMessage());
                Toast.makeText(ChatActivity.this,"Error:"+
                        "Please Check your Internet Connection!", Toast.LENGTH_LONG).show();
//                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("complaint_id", Constants.CURRENT_COMPLAINT_OBJECT.getId());
                return params;
            }
            @Override
            public Map<String, String> getHeaders() {
                Map <String,String> params  = new HashMap<String, String>();
                params.put("access_token", Constants.ACCESS_TOKEN);
                params.put("secret_key", Constants.SECRET_KEY);
                return params;
            }
        };
        // Adding request to request queue
        AppSingleton.getInstance(ChatActivity.this).addToRequestQueue(strReq, cancel_req_tag);


    }
    private void sendNewMessageToServer(final ChatMessage chatMessage){
        String cancel_req_tag = "register";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_FOR_CREATE_CHAT, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("Form", "Register Response: " + response.toString());
                //Toast.makeText(getApplicationContext(),"Register Response: " + response.toString(), Toast.LENGTH_SHORT).show();
//                hideDialog();
                if(response!=null) {
                    try {
                        JSONObject jObj = new JSONObject(response);
                        String status = jObj.getString("status");
                        if (!status.equals("success")) {
                            String errorMsg = jObj.getString("error_message");
                            Toast.makeText(ChatActivity.this,
                                    errorMsg, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Form", "Registration Error: " + error.getMessage());
                Toast.makeText(ChatActivity.this,"Error:"+
                        "Please Check your Internet Connection!", Toast.LENGTH_LONG).show();
//                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("complaint_id", Constants.CURRENT_COMPLAINT_OBJECT.getId());
                params.put("chats", chatMessage.getChats());
                params.put("role", chatMessage.getRole());
                params.put("created_at", DateFormat.getDateTimeInstance().format(chatMessage.getCreatedAt()));
                return params;
            }
            @Override
            public Map<String, String> getHeaders() {
                Map <String,String> params  = new HashMap<String, String>();
                params.put("access_token", Constants.ACCESS_TOKEN);
                params.put("secret_key", Constants.SECRET_KEY);
                return params;
            }
        };
        // Adding request to request queue
        AppSingleton.getInstance(ChatActivity.this).addToRequestQueue(strReq, cancel_req_tag);

    }

    private void getNewChats(){

        String cancel_req_tag = "register";


        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_FOR_CHAT_HISTORY, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("Form", "Register Response: " + response.toString());
                if(response!=null) {
                    try {
                        ArrayList<ChatMessage> chatHistoryNew = new ArrayList<>();
                        JSONArray jsonArray = new JSONArray(response);
                        for(int i=0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            ChatMessage chatMessage = new ChatMessage();
                            chatMessage.setId(jsonObject.getInt("id"));
                            chatMessage.setChats(jsonObject.getString("chats"));
                            chatMessage.setRole(jsonObject.getString("role"));
                            chatMessage.setComplaintsId(jsonObject.getInt("complaint_id"));
                            String createdAt = jsonObject.getString("created_at");
                            String createdAt1 = createdAt.replace("T", " ");
                            String createdAt2 = createdAt1.replace("+05:30", "");
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                            try {
                                chatMessage.setCreatedAt(formatter.parse(createdAt2));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            chatHistoryNew.add(chatMessage);
                        }

                        if(chatHistory.size() != chatHistoryNew.size()) {
                            chatHistory = chatHistoryNew;
                            for(int i=0; i<chatHistory.size(); i++) {
                                ChatMessage message = chatHistory.get(i);
                                displayMessage(message);
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Form", "Registration Error: " + error.getMessage());
                Toast.makeText(ChatActivity.this,"Error:"+
                        "Please Check your Internet Connection!", Toast.LENGTH_LONG).show();
//                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("complaint_id", Constants.CURRENT_COMPLAINT_OBJECT.getId());
                return params;
            }
            @Override
            public Map<String, String> getHeaders() {
                Map <String,String> params  = new HashMap<String, String>();
                params.put("access_token", Constants.ACCESS_TOKEN);
                params.put("secret_key", Constants.SECRET_KEY);
                return params;
            }
        };
        // Adding request to request queue
        AppSingleton.getInstance(ChatActivity.this).addToRequestQueue(strReq, cancel_req_tag);


    }
}
