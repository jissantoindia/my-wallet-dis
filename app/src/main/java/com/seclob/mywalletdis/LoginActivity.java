package com.seclob.mywalletdis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText inputMobile,inputPassword;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        inputMobile = findViewById(R.id.inputMobile);
        inputPassword = findViewById(R.id.inputPassword);
        sharedPreferences = getSharedPreferences("MYSCBCL", MODE_PRIVATE);
    }

    public void loginBtn(View view) {

        if (inputMobile.length() > 2 && inputPassword.length() > 2) {
            LoginApi(inputMobile.getText().toString(), inputPassword.getText().toString());
        } else {
            Toast.makeText(this, "Invalid Username or Password!", Toast.LENGTH_SHORT).show();
        }

    }

        public void LoginApi(final String username, final String password)
        {
            Loader(true);
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            String URL = getString(R.string.api_url)+"login";
            StringRequest request = new StringRequest(Request.Method.POST, URL,
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response) {


                            Log.i("VOLLEYES", response);

                            try {
                                Loader(false);
                                JSONObject Res=new JSONObject(response);
                                String sts    = Res.getString("sts");
                                String msg    = Res.getString("msg");

                                if(sts.equalsIgnoreCase("01"))
                                {

                                    if(Res.getString("login_id").equalsIgnoreCase("1")) {
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("username", username);
                                        editor.putString("password", password);
                                        editor.apply();

                                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(i);
                                        finish();
                                    }else
                                    {
                                        Toast.makeText(LoginActivity.this, "You are not authorized to Login!", Toast.LENGTH_SHORT).show();
                                    }

                                }

                                Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();

                            }catch (Exception e){
                                Log.e("catcherror",e+"d");

                                Toast.makeText(LoginActivity.this, "Catch Error :"+e, Toast.LENGTH_SHORT).show();

                            }


                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            NetworkResponse response = error.networkResponse;
                            String errorMsg = "";
                            if(response != null && response.data != null){
                                String errorString = new String(response.data);
                                Log.i("log error", errorString);
                                Loader(false);
                                Toast.makeText(LoginActivity.this, "Network Error :"+errorString, Toast.LENGTH_SHORT).show();

                            }
                        }
                    }
            ) {


                @Override
                protected Map<String, String> getParams()
                {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("username",username);
                    params.put("password",password);

                    Log.i("loginp ", params.toString());

                    return params;
                }

            };


            // Add the realibility on the connection.
            request.setShouldCache(false);
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));

            // Start the request immediately
            queue.add(request);

        }

        void Loader(Boolean status)
        {
            LinearLayout loader = findViewById(R.id.loader_Login);
            if(status)
                loader.setVisibility(View.VISIBLE);
            else
                loader.setVisibility(View.GONE);
        }


    }