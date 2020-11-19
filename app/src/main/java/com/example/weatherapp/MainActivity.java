package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
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


    // UI
    private Button searchButton;
    private EditText targetEditText;
    private ImageView weatherImageView;
    private TextView textViewTemperature;
    private TextView textViewTrivials;
    private TextView textViewCity;
    // 전액변수로 만들기

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 위젯들 연결시켜줌
        searchButton = findViewById(R.id.button_search);
        targetEditText = findViewById(R.id.edit_text_target_city);
        weatherImageView = findViewById(R.id.image_view_target_icon);
        textViewCity = findViewById(R.id.text_view_target_city);
        textViewTemperature = findViewById(R.id.text_view_target_temp);
        textViewTrivials = findViewById(R.id.text_view_others);

        searchButton.setOnClickListener((View.OnClickListener) this);

        String content = "https://openweathermap.org/data/2.5/weather?q=Seoul&appid=01d34bdef798e8549ff4fc63bf9f0cc8";
        callWeatherData(content);

//        searchButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String content = "https://api.openweathermap.org/data/2.5/weather?q=Seoul&appid=01d34bdef798e8549ff4fc63bf9f0cc8";
//                callWeatherData(content);
//            }
//        });

    }

    static class Weather extends AsyncTask<String,Void,String> {

        protected String doInBackground(String... address) {
            try {
                URL url = new URL(address[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection(); //URL Connection
                // Connect to URL
                connection.connect(); // CONNECT

                // Retrieving Data
                InputStream streamIn = connection.getInputStream();
                InputStreamReader streamReader = new InputStreamReader(streamIn);

                int data = streamReader.read();
                // Read one letter
                StringBuilder weatherContent = new StringBuilder();
                // String data + String data or char data + char data = new String

                while (data != -1) { // Read until EOF
                    // 더이상 읽을 데이터가 없을 때 -1 나옴 -> 데이터가 없다!
                    char ch = (char) data;
                    weatherContent.append(ch);
                    data = streamReader.read();
                    // 또 하나를 읽어옴
                }
                return weatherContent.toString(); //Return Result

            } catch (MalformedURLException e) { //Exception for mal-formed url
                e.printStackTrace();

            } catch (IOException e) { //Exception for Open connection to http url
                e.printStackTrace();
            }

            return null; //ERROR CASE : return null
        }
    }

    //Implemented Methods
    public void onClick(View v) { //Input from user
        if (v.getId() == R.id.button_search) {
            //Get target city data
            if (targetEditText.getText().toString().trim().length() > 0) { //Trim : case for input nothing inside
                String targetData = "https://openweathermap.org/data/2.5/weather?q=" + targetEditText.getText().toString() + "&appid=01d34bdef798e8549ff4fc63bf9f0cc8";
                callWeatherData(targetData);
            } else {
                Toast.makeText(this,"You haven't enter anything!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //User-Defined Methods
    private void callWeatherData(String content) {
        Weather weather = new Weather();
        try {

            // Default : Seoul
            String dataReceived = weather.execute(content).get();

            // JSON
            JSONObject jsonObject = new JSONObject(dataReceived);


            //City Info
            String cityInfo = jsonObject.getString("name");
            String weatherInfo = jsonObject.getString("weather");

            JSONArray arrayInfo = new JSONArray(weatherInfo);

            JSONObject mainInfo = jsonObject.getJSONObject("main");
            String tempData = mainInfo.getString("temp");

            setMainInfo(cityInfo,tempData);

            String iconInfo = "";

            for(int i = 0; i < arrayInfo.length(); i++){
                JSONObject dataFromArray = arrayInfo.getJSONObject(i);
                // index 값으로도 접근 가능
                iconInfo = dataFromArray.getString("icon");
                // 사실 이거 되게 비효율적
            }

            setMainInfo(cityInfo,tempData);
            setIconInfo(iconInfo);
            setTrivial(mainInfo);


        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setMainInfo(String city, String temp){
        textViewCity.setText(city.trim());
        temp+='\u2103'; //섭씨기호
        textViewTemperature.setText(temp.trim()); // .trim() : 양끝쪽의 쓸데없는 공백 없애줌

    }

    private void setIconInfo (String iconData) {
        String targetIcon = "http://openweathermap.org/img/wn/" + iconData + "@2x.png";
        Uri uri = Uri.parse(targetIcon);

        Glide.with(this)
                .load(uri)
                .centerCrop()
                // 이미지 너무 크면 지금 설정돼있는 사이즈에 맞춰서 줄이고 늘리고 자동으로 해줌
                .into(weatherImageView);
    }

    private void setTrivial (JSONObject mainObj) throws JSONException {
        String tempMax = mainObj.getString("temp_max");
        String humidity = mainObj.getString("humidity");
        String trivial = tempMax + "/" + humidity + "%";
        textViewTrivials.setText(trivial.toString());
    }

}