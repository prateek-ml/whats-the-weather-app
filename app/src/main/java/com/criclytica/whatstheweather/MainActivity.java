package com.criclytica.whatstheweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import java.util.*;
import java.text.*;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    TextView resultTextView;

    public void getWeatherData(View view) {
        String city = editText.getText().toString();

        try {
            DownloadJSON downloadJSON = new DownloadJSON();
            downloadJSON.execute("http://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=c7aa93237ee434d01cb1c8695e44e2fb");

            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        } catch(Exception e) {
            Toast.makeText(getApplicationContext(), "Could not find weather :(", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.cityEditText);
        resultTextView = findViewById(R.id.resultTextView);
    }
    public class DownloadJSON extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL (urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                int data = inputStreamReader.read();

                while(data != -1) {
                    char current = (char) data;
                    result += current;

                    data = inputStreamReader.read();
                }

                return result;

            } catch(Exception e) {
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);

                String mainInfo = jsonObject.getString("main");
                String weatherInfo = jsonObject.getString("weather");

                JSONArray weatherArray = new JSONArray(weatherInfo);
                JSONArray mainArray = new JSONArray(mainInfo);

                String message = "";



                for(int i=0; i<weatherArray.length(); i++) {
                    JSONObject object = weatherArray.getJSONObject(i);
                    message += object.getString("main") + " (" + object.getString("description") + ")\n";
                    resultTextView.setText(message);
                }

            } catch(Exception e) {
                Toast.makeText(getApplicationContext(), "Could not find weather :(", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }
}