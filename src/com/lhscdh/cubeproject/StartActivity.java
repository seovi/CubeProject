package com.lhscdh.cubeproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends Activity {

	Button mStartBtn;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        
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
