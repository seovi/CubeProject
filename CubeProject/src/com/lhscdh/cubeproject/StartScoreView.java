package com.lhscdh.cubeproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.Paint.Align;
import android.util.AttributeSet;
import android.view.View;


class StartScoreView extends View {

	Paint mPaint;	
	int mScore;
	
	public StartScoreView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		
		SharedPreferences pref = context.getSharedPreferences("PrefName", Context.MODE_PRIVATE);
		mScore = pref.getInt("key_high", 0);
		
		init();
	};
	
	private void init() {       
        
		mPaint = new Paint(Paint.FAKE_BOLD_TEXT_FLAG);
		mPaint.setColor(Color.BLACK);
		mPaint.setTypeface(Typeface.SANS_SERIF);
		mPaint.setTextSize(70);
		mPaint.setTextAlign(Align.CENTER);
		mPaint.setAntiAlias(true);
	}
	
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		
		// height 진짜 크기 구하기
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = 0;
        switch(heightMode) {
        case MeasureSpec.UNSPECIFIED:    // mode 가 셋팅되지 않은 크기가 넘어올때
            heightSize = heightMeasureSpec;
            break;
        case MeasureSpec.AT_MOST:        // wrap_content (뷰 내부의 크기에 따라 크기가 달라짐)
            heightSize = 20;
            break;
        case MeasureSpec.EXACTLY:        // fill_parent, match_parent (외부에서 이미 크기가 지정되었음)
            heightSize = MeasureSpec.getSize(heightMeasureSpec);
            break;
        }
        
        // width 진짜 크기 구하기
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = 0;
        switch(widthMode) {
        case MeasureSpec.UNSPECIFIED:    // mode 가 셋팅되지 않은 크기가 넘어올때
            widthSize = widthMeasureSpec;
            break;
        case MeasureSpec.AT_MOST:        // wrap_content (뷰 내부의 크기에 따라 크기가 달라짐)
            widthSize = 100;
            break;
        case MeasureSpec.EXACTLY:        // fill_parent, match_parent (외부에서 이미 크기가 지정되었음)
            widthSize = MeasureSpec.getSize(widthMeasureSpec);
            break;
        }       
        
        setMeasuredDimension(widthSize, heightSize);
		
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);	
								
		canvas.drawText(Integer.toString(mScore), getMeasuredWidth() / 2, getMeasuredHeight() / 2, mPaint);
	}

}