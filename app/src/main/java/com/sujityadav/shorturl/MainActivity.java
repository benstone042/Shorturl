package com.sujityadav.shorturl;

import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String API_URL_SHORTEN = "https://www.googleapis.com/urlshortener/v1/url?key=AIzaSyBMaEnhNt4G3PZyXI1IhWGkqYOZdS8CQIw";

    EditText urlEdit;
    TextView shorturl,longurl;
    TextView eMessage;
    Button goButton;
    ClipboardManager cm;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        urlEdit = (EditText) findViewById(R.id.urlEdit);
        shorturl = (TextView) findViewById(R.id.shorturl);
        goButton = (Button) findViewById(R.id.goShort);
        goButton.setOnClickListener(this);
        cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        checkUrlInCm();
    }

    void checkUrlInCm(){
        if(cm.getText().toString().startsWith("http://")||cm.getText().toString().startsWith("https://")){
            urlEdit.setText(cm.getText());
        }
    }
    @Override
    public void onClick(View v) {
        if(urlEdit.getText().toString().trim().equals("")) {
            Toast.makeText(MainActivity.this,"Please enter url",Toast.LENGTH_SHORT).show();
            return;
        }
        String temp=getfromCache(urlEdit.getText().toString(),MainActivity.this);
        if (temp != null) {
            shorturl.setVisibility(View.VISIBLE);
            shorturl.setMovementMethod(LinkMovementMethod.getInstance());
            shorturl.setText(temp);
            Toast.makeText(MainActivity.this,"Fetched from cache",Toast.LENGTH_SHORT).show();
        }
        else{
            getShortUrl();
        }


    }
 public String getfromCache(String s,Context context){
     SharedPreferences prefs = context.getSharedPreferences("MyShortUrls", MODE_PRIVATE);
     String restoredText = prefs.getString(s, null);
     return restoredText;
 }
 public void getShortUrl(){

     dialog = new ProgressDialog(this);
     dialog.setIndeterminate(true);
     dialog.setMessage("Shortening! Please wait...");
     dialog.show();
     HashMap<String, String> params = new HashMap<String, String>();
     params.put("longUrl",urlEdit.getText().toString());
     JsonObjectRequest jsObjRequest = new JsonObjectRequest
             (Request.Method.POST, API_URL_SHORTEN,new JSONObject(params), new Response.Listener<JSONObject>() {

                 @Override
                 public void onResponse(JSONObject response) {
                     if(dialog != null && dialog.isShowing()){
                         dialog.dismiss();
                     }
//logic
                     SharedPreferences.Editor editor = getSharedPreferences("MyShortUrls", MODE_PRIVATE).edit();
                     try {
                         editor.putString(urlEdit.getText().toString(), response.getString("id"));
                         shorturl.setVisibility(View.VISIBLE);
                         shorturl.setMovementMethod(LinkMovementMethod.getInstance());
                         shorturl.setText(response.getString("id"));
                     } catch (JSONException e) {
                         e.printStackTrace();
                     }
                     editor.apply();


                 }
             }, new Response.ErrorListener() {

                 @Override
                 public void onErrorResponse(VolleyError error) {
                     // TODO Auto-generated method stub
                     if(dialog != null && dialog.isShowing()){
                         dialog.dismiss();
                     }

                 }
             }){
         @Override
         public Map<String, String> getHeaders() throws AuthFailureError {
             Map<String,String> params = new HashMap<String, String>();
             params.put("Content-Type","application/json");
             return params;
         }
     };

// Access the RequestQueue through your singleton class.
     MySingleton.getInstance(this).addToRequestQueue(jsObjRequest);

 }
}
