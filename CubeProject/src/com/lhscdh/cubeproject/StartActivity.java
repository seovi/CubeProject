package com.lhscdh.cubeproject;

import com.google.android.gms.ads.*;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

public class StartActivity extends Activity {
		
	Button mStartBtn;	
	static StartScoreView mStartCoreView;
	
Handler mHandler;
Runnable mRunnable;

	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
                
        mStartCoreView = (StartScoreView)findViewById(R.id.view_ids_startview);
                
        AdView adView = (AdView)findViewById(R.id.view_ids_adview);        
        AdRequest adRequest = new AdRequest.Builder().build();
        
        adView.loadAd(adRequest);
                
        mStartBtn = (Button) findViewById(R.id.view_ids_start);
        mStartBtn.setOnClickListener(mViewClickListener);
        
        mRunnable = new Runnable() {
        	public void run() {
        		mStartBtn.setText("START");
        		mStartBtn.setEnabled(true);
        	}
        };        
        
        mHandler = new Handler();
        mHandler.postDelayed(mRunnable, 2000);
    }
    
    protected void performAction(View view, int actionId) {
    	if(actionId == R.id.view_ids_start) {
    		startActivity(new Intent(StartActivity.this, MainActivity.class));
    	}
    }    
    
    protected View.OnClickListener mViewClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            performAction(v, v.getId());
        }
    };
}
