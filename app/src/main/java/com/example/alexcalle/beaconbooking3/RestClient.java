package com.example.alexcalle.beaconbooking3;

import android.os.AsyncTask;

import com.example.alexcalle.beaconbooking3.RestClientListener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class RestClient extends AsyncTask<String, Void, RestClientResult> {
    private String _url;

    private String _method;

    private Map<String, String> _headers;

    private String _jsonBody;

    private RestClientListener _listener;

    public RestClient(String url, String method, Map<String, String> headers, RestClientListener listener) {
        this._url = url;
        this._method = method;
        this._headers = headers;
        this._jsonBody = null;
        this._listener = listener;
    }


    public RestClient(String url, String method, Map<String,String> headers, String jsonBody, RestClientListener listener) {
        this._url = url;
        this._method = method;
        this._headers = headers;
        this._jsonBody = jsonBody;
        this._listener = listener;
    }

    @Override
    protected RestClientResult doInBackground(String... params) {
        try {
            URL url = new URL(this._url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(this._method);

            if(this._headers != null) {
                for (Map.Entry<String, String> header : this._headers.entrySet()) {
                    connection.setRequestProperty(header.getKey(), header.getValue());
                }
            }

            if(this._jsonBody != null && this._jsonBody.length() > 0) {
                byte[] outputInBytes = this._jsonBody.getBytes("UTF-8");
                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(outputInBytes);
                outputStream.close();
            }

            int responseCode = connection.getResponseCode();

            if(responseCode == 200) {
                BufferedReader bufferReader = new BufferedReader(new InputStreamReader((connection.getInputStream())));

                String output = "";
                String line;

                while ((line = bufferReader.readLine()) != null) {
                    output += line;
                }

                connection.disconnect();

                return new RestClientResult(this._method, responseCode, output);
            }

            connection.disconnect();

            return new RestClientResult(this._method, responseCode, "");
        }
        catch (Exception ex) {
            return new RestClientResult(this._method, 401, "");
        }
    }

    @Override
    protected void onPostExecute(RestClientResult result) {
        if(this._listener != null) {
            this._listener.onResult(result);
        }
    }
}
