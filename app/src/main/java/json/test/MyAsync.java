package json.test;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import json.test.model.MusicDTO;

/**
 * Created by KwonWoongKi on 2015-07-17.
 */
public class MyAsync extends AsyncTask<String, Void, String>{
    String TAG="MyAsync";
    MyAdapter adapter;
    ListView listView;

    public MyAsync(MyAdapter adapter, ListView listView){
       this.adapter = adapter;
        this.listView = listView;
    }

    //쓰레드 수행전 : main Thread - UI제어가능
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        /*주로 값의 초기화 및 웹연동 일경우 프로그래바의 시작에 적절..*/

    }

    //쓰레드 수행
    //가변형 인자란 ? 호출자가 인수의 갯수를 결정할 수 있는 매개변수 정의 기법
    //매개변수의 갯수는 호출시 결정된다.
    @Override
    protected String doInBackground(String... params) {
        String data = null;

        try {
            data = downloadUrl(params[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }

    //쓰레드 수행 중 UI제어
    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);

        /*프로그래스바의 진행 상태 적용에 적절*/
    }

    //쓰레드 수행 후
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        /*
            웹서버로부터 전달받은 데이터를 ListView에 출력해보자
         */
        try {
            JSONObject jsonObject = new JSONObject(s); //파싱
            JSONArray array = (JSONArray)jsonObject.get("music");

            ArrayList list = new ArrayList();

            for(int i=0; i<array.length(); i++){
                JSONObject obj = (JSONObject)array.get(i);

                MusicDTO dto = new MusicDTO();
                dto.setSinger(obj.getString("singer"));
                dto.setTitle(obj.getString("title"));
                dto.setFile(obj.getString("file"));

                list.add(dto);
            }

            //어댑터가 보유한 list와 현재 list를 대체
            adapter.list = list;
            //listView.invalidate();
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }



        /*JSONTest Activity클래스의 txt_list에 출력해보자*/


        //String result=parseData(s);


        /*프로그래스바의 완료에 적절*/
    }

    //웹서버에 접속하여 스트림을 연결한 후, 그 데이터 읽어오기
    private String downloadUrl(String myurl) throws IOException {
        InputStream is = null;


        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d(TAG, "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = readIt(is);
            return contentAsString;

        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    public String readIt(InputStream stream) throws IOException, UnsupportedEncodingException {
        BufferedReader buffr = null;
        String data = null;
        buffr = new BufferedReader(new InputStreamReader(stream, "UTF-8"));

        StringBuilder sb = new StringBuilder();

        while((data=buffr.readLine())!=null){
            sb.append(data);
        }

        return sb.toString();
    }

    public String parseData(String data){
        return null;
    }
}
