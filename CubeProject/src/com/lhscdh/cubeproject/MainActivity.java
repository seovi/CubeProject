package com.lhscdh.cubeproject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

    static GameView mGameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGameView = (GameView) findViewById(R.id.view_ids_gameview);
    }

    protected void performAction(View view, int actionId) {
        if (actionId == R.id.view_ids_start) {

        }
    }

    protected View.OnClickListener mViewClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            performAction(v, v.getId());
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        finish();
        System.exit(0);	 
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        System.exit(0);
    }

    BroadcastReceiver mBRGameOver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //			GameView.PauseGame();
//            if (GameView.status == GameView.PROCESS)
//                GameView.status = GameView.BACK;

            System.exit(0);
        }
    };

    public void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter();
        filter.addAction("GAMEOVER");
        registerReceiver(mBRGameOver, filter);
    }

    public void onPause() {
        super.onPause();

        unregisterReceiver(mBRGameOver);
        System.exit(0);
    }
    // public static void GameOver() {
    // GameView.PauseGame();
    // finish();
    // }

}
