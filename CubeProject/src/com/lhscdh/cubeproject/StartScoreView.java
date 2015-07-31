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
		
		// height ��¥ ũ�� ���ϱ�
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = 0;
        switch(heightMode) {
        case MeasureSpec.UNSPECIFIED:    // mode �� ���õ��� ���� ũ�Ⱑ �Ѿ�ö�
            heightSize = heightMeasureSpec;
            break;
        case MeasureSpec.AT_MOST:        // wrap_content (�� ������ ũ�⿡ ���� ũ�Ⱑ �޶���)
            heightSize = 20;
            break;
        case MeasureSpec.EXACTLY:        // fill_parent, match_parent (�ܺο��� �̹� ũ�Ⱑ �����Ǿ���)
            heightSize = MeasureSpec.getSize(heightMeasureSpec);
            break;
        }
        
        // width ��¥ ũ�� ���ϱ�
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = 0;
        switch(widthMode) {
        case MeasureSpec.UNSPECIFIED:    // mode �� ���õ��� ���� ũ�Ⱑ �Ѿ�ö�
            widthSize = widthMeasureSpec;
            break;
        case MeasureSpec.AT_MOST:        // wrap_content (�� ������ ũ�⿡ ���� ũ�Ⱑ �޶���)
            widthSize = 100;
            break;
        case MeasureSpec.EXACTLY:        // fill_parent, match_parent (�ܺο��� �̹� ũ�Ⱑ �����Ǿ���)
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