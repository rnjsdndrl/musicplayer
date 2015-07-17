package com.study.musicplayer;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by KwonWoongKi on 2015-07-16.
 */
public class PlayerActivity extends Activity implements AdapterView.OnItemClickListener {
    String TAG;
    ListView listView;
    String sd_path;


    ArrayAdapter<CharSequence> adapter; /* 보여질 데이터가 복합위젯이 아닐경우 단순한 Text
                                                                    TextView 하나일경우엔 굳이 Adapter를 재정희하지 말자*/

    ArrayList<CharSequence> list = new ArrayList<CharSequence>();
    /* 안드로이드에서는 음악, 동영상 등 미디어파일을 제어하기 위해 지원되는 객체*/
    MediaPlayer mediaPlayer;
    ImageView[] btn = new ImageView[4];
    String current_title;
    int current_position; //현재 재생중인 음악의 ArrayList내의 index
    TextView txt_title;

    // HttpURLConnection 객체를 이용하여, 웹서버에 접속을 시도하자!
    HttpURLConnection con;
    AsyncTask asyncTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        listView = (ListView) findViewById(R.id.listView);
        txt_title = (TextView) findViewById(R.id.txt_title);

        btn[0] = (ImageView) findViewById(R.id.bt_prev);
        btn[1] = (ImageView) findViewById(R.id.bt_stop);
        btn[2] = (ImageView) findViewById(R.id.bt_next);
        btn[3] = (ImageView) findViewById(R.id.bt_play);

        TAG = this.getClass().getName();
        /*
            스마트폰마다 SDCARD 의 위치가 틀릴 수 있으므로,
            본인의 스마트폰의 sdcard 디렉토리를 프로그래밍적으로 조사해보자
         */

        //각 스마트폰 환경에 맞게 알아서 SDCARD 저장소 디렉토리를 구함
        File dir = Environment.getExternalStorageDirectory();

        //SD카드 디렉토리의 경로 얻자
        sd_path = dir.getAbsolutePath();

        Log.d(TAG, "SD카드 디렉토리 경로는" + sd_path);

        //SD카드내의 하위 디렉토리 출력해보기
        File f = new File(sd_path + "/Music");
        File[] child = f.listFiles();
        //Log.d(TAG, "child is"+child);

        //improved for
        for (File file : child) {
            Log.d(TAG, file.getName());
            list.add(file.getName()); /*파일명을 리스트에 담자*/
        }

        adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this); //리스너와의 연결

        // Defalte 뮤직 제목
        current_title = (String) list.get(0);

        checkNetwork();
    }

    private class MyAsync extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            Toast.makeText(getApplicationContext(), "웹서버 접속 직전", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String[] params) {
            String data = null;

            try {
                data = downloadUrl(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return data;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            Toast.makeText(getApplicationContext(), "데이터 다운로드 중..", Toast.LENGTH_SHORT).show();

        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(getApplicationContext(), "데이터 수집완료" + s, Toast.LENGTH_SHORT);
        }
    }

    ;





    /*
        네트워크의 상태확인한다
        왜? 네트워크 상태가 유효할때만 데이터를 가져올 수 있으므로
     */

    public void checkNetwork() {

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            Toast.makeText(this, "네트워크상태 유효합니다", Toast.LENGTH_SHORT).show();
             /* 웹서버에 연동 시작*/
            MyAsync myAsync = new MyAsync();
            myAsync.execute("http://192.168.0.179:8080/music/music_list.jsp");
        } else {
            Toast.makeText(this, "네트워크상태에 문제가 있습니다", Toast.LENGTH_SHORT).show();
        }
    }


    private String downloadUrl(String myurl) throws IOException {
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 500;

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
            String contentAsString = readIt(is, len);
            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

    public void play() {

        if (mediaPlayer == null) {//최초 재생이라면
            mediaPlayer = new MediaPlayer();
            try {
                 /*실행할 파일 지정*/
                //mediaPlayer.setDataSource(this, Uri.parse(sd_path + "/Music" + "/"+current_title));
                mediaPlayer.setDataSource(this, Uri.parse("http://192.168.0.179:8080/music/LostStars.mp3"));
                mediaPlayer.prepare();//재생전 초기화 및 준비
                mediaPlayer.start();
                /* 정지 할 수 있도록 일시정지 버튼을 보여주자 */
                btn[3].setImageResource(R.drawable.pause);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {//멈춘후 다시 실행이라면
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                btn[3].setImageResource(R.drawable.play);
            } else {
                //플레이중이라면..
                mediaPlayer.start();
                btn[3].setImageResource(R.drawable.pause);
                //이미 멈춰있다면 다시 start()
            }
        }
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            btn[3].setImageResource(R.drawable.play);
        }
    }

    public void pause() {
        mediaPlayer.pause();
    }

    public void next() {
        current_position++;
        setTitle();
        stop();
        play();
    }

    public void prev() {
        current_position--;
        setTitle();
        stop();
        play();
    }

    public void btnClick(View view) {
        switch (view.getId()) {
            case R.id.bt_prev:
                prev();
                break;
            case R.id.bt_next:
                next();
                break;
            case R.id.bt_play:
                play();
                break;
            case R.id.bt_stop:
                stop();
                break;
        }
    }

    // 재생 제목교체 하기
    public void setTitle() {
        current_title = (String) list.get(current_position);
        txt_title.setText(current_title);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView textView = (TextView) view;
        current_title = textView.getText().toString();
        current_position = position;
        setTitle();
        stop();
        play();
        Toast.makeText(this, "선택한 아이템의 position은 :" + position + ", 제목은" + textView.getText(), Toast.LENGTH_SHORT).show();
    }
}
