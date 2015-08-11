package com.lhscdh.cubeproject;

import com.google.android.gms.ads.*;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class StartActivity extends Activity {
		
	Button mStartBtn;	
	static StartScoreView mStartCoreView;
	
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
