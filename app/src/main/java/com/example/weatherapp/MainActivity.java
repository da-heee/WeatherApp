package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    // 전액변수로 만들기

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.btn_click);
        textView = findViewById(R.id.texthere);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = "https://api.openweathermap.org/data/2.5/weather?q=Seoul&appid=01d34bdef798e8549ff4fc63bf9f0cc8";
                callWeatherData(content);
            }
        });

    }

    static class Weather extends AsyncTask<String,Void,String> {

        protected String doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream streamIn = connection.getInputStream();
                InputStreamReader streamReader = new InputStreamReader(streamIn);

                int data = streamReader.read();
                // 하나를 읽어옴
                StringBuilder weatherContent = new StringBuilder();
                // String data + String data or char data + char data = new String

                while (data != -1) {
                    // 더이상 읽을 데이터가 없을 때 -1 나옴 -> 데이터가 없다!
                    char ch = (char) data;
                    weatherContent.append(ch);
                    data = streamReader.read();
                    // 또 하나를 읽어옴
                }
                return weatherContent.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void callWeatherData(String content) {
        Weather weather = new Weather();
        try {
            String data = weather.execute(content).get();
            JSONObject jsonObject = new JSONObject(data);
            textView.setText(jsonObject.toString());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}