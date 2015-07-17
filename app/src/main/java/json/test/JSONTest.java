package json.test;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.study.musicplayer.R;

import org.w3c.dom.Text;

import json.test.model.MusicDTO;

/**
 * Created by KwonWoongKi on 2015-07-17.
 */
public class JSONTest extends Activity {
    MyAsync async;
    EditText txt_url;
    ListView listView;
    MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_json);
        txt_url = (EditText) findViewById(R.id.txt_url);
        listView = (ListView)findViewById(R.id.listView);

        //리스트뷰에 어댑터 적용
        adapter = new MyAdapter(this);
        /*
        for(int i = 0; i<30; i++) {
            MusicDTO dto = new MusicDTO();
            dto.setTitle("제목"+i);
            dto.setSinger("가수"+i);
            dto.setFile("파일"+i);
            adapter.list.add(dto);
        }
        */
        listView.setAdapter(adapter);
    }

    /*웹서버 접속 전, 네트워크 상태 확인한다다*/
    public void checkNetwork() {

        ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            loadURL();
        } else {
            Toast.makeText(this, "접속할수 없습니다. 네트워크 상태를 확인하세요", Toast.LENGTH_SHORT).show();
        }

    }

    public void loadURL() {
        String url = txt_url.getText().toString();
        async = new MyAsync(adapter, listView);
        async.execute(url);/* doInbackground() 호출 */
    }

    public void btnClick(View view) {
        checkNetwork();
    }

    /*웹서버에서 가져온 데이터자체는 아직, 해석된 상태가 아니므로,
    * 파싱을 통해 원하는 데이터만 추출해보자
    * */

}
