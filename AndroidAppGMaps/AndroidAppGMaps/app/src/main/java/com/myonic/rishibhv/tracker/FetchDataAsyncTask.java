package com.myonic.rishibhv.tracker;

import android.content.Context;
import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FetchDataAsyncTask extends AsyncTask<String, Void, String> {
    /* variable to hold context*/
    private Context applciationContext;

    /*save the context received via constructor in a local variable*/

    public FetchDataAsyncTask(Context context) {
        this.applciationContext = context;
    }

    /* Fetches data from url passed*/
    @Override
    protected String doInBackground(String... url) {

        /*For storing data from web service*/
        String data = "";
        try {

            /* Fetching the data from web service*/
            data = downloadUrl(url[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        ParserAsyncTask parserTask = new ParserAsyncTask(applciationContext);

        /*Invokes the thread for parsing the JSON data*/
        parserTask.execute(result);

    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            /* Creating an http connection to communicate with url*/
            urlConnection = (HttpURLConnection) url.openConnection();

            /* Connecting to url*/
            urlConnection.connect();
            urlConnection.setReadTimeout(15000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setDoInput(true);

            /*Reading data from url*/
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
}