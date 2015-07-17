package com.study.musicplayer;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /*
        안드로이드의 4대컴포넌트는 모두 시스템이 관리하므로,
        무언가 요청을 하려면 시스템이 이해할 수 있는 형식으로
        의도나 요청을 시도해야 한다.
        그게 바로 Intent(원하는 의도)
     */

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Intent intent = new Intent(this, PlayerActivity.class); //나에서 다른애에게로
        startActivity(intent); /* 다른 엑티비티 호출 */
        return super.onTouchEvent(event);
    }
}
