package com.lhscdh.cubeproject;

import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Paint.Align;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.view.SurfaceHolder.Callback;

public class GameView extends SurfaceView implements Callback {

    public static final String TAG = "GameView";

    final static int PROCESS = 1;

    final static int GAMEOVER_PROCESS = 2; // Stage Clear

    final static int GAMEOVER = 3; // Game Over

    final static int ALL_CLEAR = 4; // All Clear    

    static GameThread mThread; // GameThread

    static SurfaceHolder mHolder; // SurfaceHolder

    static Context mContext; // Context

    static ArrayList<Cube> mCube;    

    static Bitmap mFigure[];     
    static Bitmap mWater[]; 

    static float density;

    static int mFigureTotalNum = 5;

    int mUpFigureNum;

    static int mBelowFigureNum = 1;
    static int mBelowFigureLeftNum = 0;
    static int mBelowFigureRightNum = 2;

    static int mFigureGoalNum;

    static int mBestScore = 0;
    static int mScore = 0;    

    static boolean mIsScored = false;
    
    static boolean mIsTouchPre = false;
    static boolean mIsTouchNext = false;
    
    int preColorTime = 0;
    int nextColorTime = 0;
    
    static int scoreStatus = 0;

    static int mFigureIndex = 0;

    static ArrayList<Integer> mFigureList;

    static int Width, Height; // View

    static int status = PROCESS;
    
    AudioClip sbBlop;
    AudioClip sbTick1;
    AudioClip sbTick2;
    AudioClip sbTick3;
    AudioClip sbFizzle;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        mHolder = holder;
        mContext = context;
        mThread = new GameThread(holder, context);

        InitGame();
        InitTouchCor();
        setFocusable(true);
    }

    private void InitGame() {
        Display display = ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Width = display.getWidth();
        Height = display.getHeight();
        density = mContext.getResources().getDisplayMetrics().density;

        mCube = new ArrayList<Cube>();
        mFigure = new Bitmap[mFigureTotalNum];
        mFigure[0] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.circle), 80, 80, true);
        mFigure[1] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.love), 80, 80, true);
        mFigure[2] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.triangle), 80, 80, true);        
        mFigure[3] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.diamond), 80, 80, true);
        mFigure[4] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.pentagon), 80, 80, true);
        
               
        mWater = new Bitmap[20];
        mWater[0] = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.water_001), 0, 0, 128, 128);
        mWater[1] = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.water_001), 0, 0, 128, 128);
        mWater[2] = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.water_001), 128, 0, 128, 128);
        mWater[3] = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.water_001), 128, 0, 128, 128);
        mWater[4] = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.water_001), 256, 0, 128, 128);
        mWater[5] = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.water_001), 256, 0, 128, 128);
        mWater[6] = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.water_001), 384, 0, 128, 128);
        mWater[7] = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.water_001), 384, 0, 128, 128);
        mWater[8] = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.water_001), 512, 0, 128, 128);
        mWater[9] = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.water_001), 512, 0, 128, 128);
        mWater[10] = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.water_001), 0, 128, 128, 128);
        mWater[11] = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.water_001), 0, 128, 128, 128);
        mWater[12] = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.water_001), 128, 128, 128, 128);
        mWater[13] = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.water_001), 128, 128, 128, 128);
        mWater[14] = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.water_001), 256, 128, 128, 128);
        mWater[15] = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.water_001), 256, 128, 128, 128);
        mWater[16] = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.water_001), 384, 128, 128, 128);
        mWater[17] = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.water_001), 384, 128, 128, 128);
        mWater[18] = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.water_001), 512, 128, 128, 128);
        mWater[19] = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.water_001), 512, 128, 128, 128);
       
        for (int i = 0; i < 20; i++) {
        	mWater[i] = Bitmap.createScaledBitmap(mWater[i], 384, 384, true);	
        }        
        
        mFigureList = new ArrayList<Integer>();
        
        SharedPreferences sharedPref = mContext.getSharedPreferences("PrefName", Context.MODE_PRIVATE);       
        mBestScore = sharedPref.getInt("key_high", 0);
        
        sbBlop = new AudioClip(mContext, R.raw.sb_blop);
        sbTick1 = new AudioClip(mContext, R.raw.sb_tick);
        sbTick2 = new AudioClip(mContext, R.raw.sb_tick);
        sbTick3 = new AudioClip(mContext, R.raw.sb_tick);
        sbFizzle = new AudioClip(mContext, R.raw.sb_fizzle);

    }

    private Rect rectPrev, rectNext;

    private Rect rectGameoverOk;

    // canvas.drawBitmap(mPause, Width - 100 * density, 10 * density, null);
    public void InitTouchCor() { // init touch coordinate

        rectPrev = new Rect(0, (int) (90 * density), (int) (120 * density), Height - 350);

        rectNext = new Rect((int) (Width - 120 * density), (int) (90 * density), Width, Height - 350);
        
        rectGameoverOk =
                new Rect((int) (Width / 2 - 50 * density), (int) (420 * density),
                    (int) (Width / 2 + 50 * density), (int) (420 * density + 100 * density));
            
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mThread.start();
        } catch (Exception e) {
            RestartGame();
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        StopGame();
    }

    public static void StopGame() {
        Log.v(TAG, "StopGame");
        mThread.StopThread();
    }
   
    public static void ResumeGame() {
        mThread.PauseNResume(false);
    }

    public void RestartGame() {
        mThread.StopThread();

        mThread = null;
        mThread = new GameThread(mHolder, mContext);       
        mThread.start();
    }
    
    public void RecordScore() {
    	
    	SharedPreferences sharedPref = mContext.getSharedPreferences("PrefName", Context.MODE_PRIVATE);
    	
    	if(mBestScore < mScore || mScore == 300) {
    		mBestScore = mScore;
    		SharedPreferences.Editor editor = sharedPref.edit();
    		editor.putInt("key_high", mScore);    		
    		editor.commit();
    		
    		if (mScore == 300) { scoreStatus = 2; }
    		else {scoreStatus = 1;}
    		
    	}        
    	
    }

    public static void GameOver() {
    	mThread.PauseNResume(true);    	
    }

    class GameThread extends Thread {
        boolean canRun = true;

        boolean isWait = false;

        boolean mRunning = true;

        int circleColorTime = 0;        
        int gameOverProcessCount = 0;
        int gameOverCount = 0;

        int loop;
        int alphaSpeed = 0;

        int mTextWidth;        

        final Handler mHandler = new Handler();

        Paint backGroundFillPaint;
        Paint backGroudnLinePaint;
        Paint prevTouchPaint;
        Paint nextTouchPaint;
        Paint scorePaint;
        Paint bottomCircle;
        Paint gameOverPaint;

        public GameThread(SurfaceHolder holder, Context context) {
        	backGroundFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            backGroudnLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            prevTouchPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            nextTouchPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            scorePaint = new Paint(Paint.FAKE_BOLD_TEXT_FLAG);
            
            backGroudnLinePaint.setColor(Color.GRAY);
            backGroudnLinePaint.setStrokeWidth(4.0f);
            
            prevTouchPaint.setColor(Color.GRAY);
            prevTouchPaint.setStrokeWidth(4.0f);
            
            nextTouchPaint.setColor(Color.GRAY);
            nextTouchPaint.setStrokeWidth(4.0f);
            
        	backGroundFillPaint.setColor(Color.WHITE);     
        	        	
        	scorePaint.setColor(Color.BLACK);       	
        	scorePaint.setTypeface(Typeface.MONOSPACE);
        	scorePaint.setAntiAlias(true);
        	
        	bottomCircle = new Paint();
        	bottomCircle.setAntiAlias(true);
        	bottomCircle.setStyle(Paint.Style.STROKE);
        	bottomCircle.setStrokeWidth(4);
        	bottomCircle.setColor(Color.GRAY);
        	
        	gameOverPaint = new Paint(Paint.FAKE_BOLD_TEXT_FLAG);
        	gameOverPaint.setAntiAlias(true);
        	gameOverPaint.setColor(Color.WHITE);        	
        }

        public void CheckCube() {
           // Log.v(TAG, "CeckCube");

            for (Cube tmp : mCube) {

                if (tmp.y >= 475 * density && mFigureList.get(0) != mBelowFigureNum) {
                    status = GAMEOVER_PROCESS;
                    gameOverProcessCount = 0;
                    
                    sbFizzle.play();
                	 
                } else if (tmp.y >= 475 * density && mFigureList.get(0) == mBelowFigureNum) {
                	
                    mScore = mScore + 1;
                    mIsScored = true;
                    sbBlop.play();
                    mFigureList.remove(0);
                    tmp.isDead = true;
                    
                    alphaSpeed = mScore / 10;
                    
                    if (mScore == 300) {
                    	status = GAMEOVER_PROCESS;
                        gameOverProcessCount = 0;
                    }
                    
                }

            }
        }

        int pastNum = -1;

        public void DrawCube(Canvas canvas) {
            Random random = new Random();
                       
            int loopSpeed = 85 - alphaSpeed * 3;
            
            if (loopSpeed < 55) {
            	loopSpeed = 55;
            }
            
            if (loop > loopSpeed) {            	
            	 
                while (true) {

                    mUpFigureNum = random.nextInt(mFigureTotalNum);

                    if (pastNum != mUpFigureNum) {
                        pastNum = mUpFigureNum;
                        break;
                    }

                }
                mCube.add(new Cube((Width - mFigure[mUpFigureNum].getWidth()) / 2, 90 * density, mUpFigureNum));

                mFigureList.add(mUpFigureNum);
                mFigureIndex++;
                
                loop = 0;
            }

            for (Cube tmp : mCube) {
                canvas.drawBitmap(tmp.img, tmp.x, tmp.y, null);
            }
        }

        public void MoveAll() {
            loop++;

            for (int i = mCube.size() - 1; i >= 0; i--) {

                mCube.get(i).Move(alphaSpeed);

                if (mCube.get(i).isDead == true) {
                    mCube.remove(i);
                }
            }
        }
        
        public void DrawBackGround(Canvas canvas) {
            
        	//DrawBackGroudFill
            canvas.drawRect(0, 0, Width, Height, backGroundFillPaint);
                     
            //DrawBackGroundLine
            canvas.drawLine(0, 90 * density, Width, 90 * density, backGroudnLinePaint);

            canvas.drawLine((int) (120 * density), (int) (90 * density), (int) (120 * density), Height - 350, backGroudnLinePaint);
                        
            canvas.drawLine(Width - 120 * density, (int) (90 * density), Width - 120 * density, Height - 350, backGroudnLinePaint);           

            canvas.drawLine(0, Height - 350, 120 * density, Height - 350, backGroudnLinePaint);

            canvas.drawLine(Width - 120 * density, Height - 350, Width, Height - 350, backGroudnLinePaint);

            canvas.drawLine(0, Height - 150, Width, Height - 150, backGroudnLinePaint);
            
            // <
            if (preColorTime > 0) {
            	prevTouchPaint.setColor(Color.MAGENTA);
            	prevTouchPaint.setStrokeWidth(6);
            	preColorTime--;
            }
            else {
            	prevTouchPaint.setColor(Color.GRAY);
            	prevTouchPaint.setStrokeWidth(4);
            }
            canvas.drawLine((int) (60 * density) + 20 , (Height - 350 + 90 * density) / 2 - 100, (int) (60 * density) - 20 , (Height - 350 + 90 * density) / 2, prevTouchPaint);
            canvas.drawLine((int) (60 * density) - 20 , (Height - 350 + 90 * density) / 2, (int) (60 * density) + 20 , (Height - 350 + 90 * density) / 2 + 100, prevTouchPaint);
            
            // >
            if (nextColorTime > 0) {
            	nextTouchPaint.setColor(Color.MAGENTA);
            	nextTouchPaint.setStrokeWidth(6);
            	nextColorTime--;
            }
            else {
            	nextTouchPaint.setColor(Color.GRAY);
            	nextTouchPaint.setStrokeWidth(4);
            }
            
            canvas.drawLine(Width - 60 * density - 20 , (Height - 350 + 90 * density) / 2 - 100, (int) Width - 60 * density + 20 , (Height - 350 + 90 * density) / 2, nextTouchPaint);
            canvas.drawLine(Width - 60 * density + 20 , (Height - 350 + 90 * density) / 2, (int) Width - 60 * density - 20 , (Height - 350 + 90 * density) / 2 + 100, nextTouchPaint);               

        }
        
        

        public void DrawScore(Canvas canvas) {
        	
        	scorePaint.setTextSize(50 * density);
        	mTextWidth = (int) Math.ceil(scorePaint.measureText(Integer.toString(mScore)));
            
        	canvas.drawText(Integer.toString(mScore), (Width - mTextWidth) / 2, 60 * density, scorePaint);           

        }

        public void DrawBottomCircle(Canvas canvas) {
            

            if (mIsScored) {
            	bottomCircle.setColor(Color.MAGENTA);
            	bottomCircle.setStrokeWidth(6);
            	
            	canvas.drawBitmap(mWater[circleColorTime], (Width - mWater[0].getWidth()) / 2,
                        (int) (400 * density), null);            	
            	
                if (circleColorTime > 18) {                    
                    circleColorTime = 0;
                    mIsScored = false;
                    bottomCircle.setColor(Color.GRAY);
                    bottomCircle.setStrokeWidth(4);
                }
                circleColorTime++;
            }

            canvas.drawCircle(Width / 2, (480 * density) +  mFigure[mBelowFigureNum].getWidth() / 2, (40 * density), bottomCircle);

        }

        public void DrawBottomFigure(Canvas canvas) {
        	
        	canvas.drawBitmap(mFigure[mBelowFigureLeftNum], 50, (int) (480 * density), null);

            canvas.drawBitmap(mFigure[mBelowFigureNum], (Width - mFigure[mBelowFigureNum].getWidth()) / 2,
                (int) (480 * density), null);            
           
            canvas.drawBitmap(mFigure[mBelowFigureRightNum], Width - 150, (int) (480 * density), null);
        }
        
        public void DrawGameOver(Canvas canvas) {
        	
          	if(gameOverCount < 41) {
          		int size = Height / 40 * gameOverCount;
          		canvas.drawRect(0, 0, Width, size, gameOverPaint);
          	}
         	
          	if (42 < gameOverCount ) {
          		RecordScore();
          		
          		gameOverPaint.setColor(Color.BLACK);       	
          		gameOverPaint.setTypeface(Typeface.MONOSPACE);
          		gameOverPaint.setTextSize(30 * density);
          		          		
          		String bestScore = "BEST";
            	canvas.drawText(bestScore, 20 * density, 160 * density, gameOverPaint);            	
          		String score = "SCORE";            	                
            	canvas.drawText(score, 20 * density, 220 * density, gameOverPaint);
            	
            	gameOverPaint.setTextAlign(Align.CENTER);
            	String gameOver = "GAME OVER";
          		canvas.drawText(gameOver, Width/2, 80 * density, gameOverPaint);
          		          		
            	canvas.drawText(Integer.toString(mBestScore), Width - 60 * density, 160 * density, gameOverPaint);            	
            	canvas.drawText(Integer.toString(mScore), Width - 60 * density, 220 * density, gameOverPaint);
            	
            	//canvas.drawRect(rectGameoverOk, gameOverPaint);
            	canvas.drawText("OK", Width / 2, 450 * density, gameOverPaint);
            	
            	if(scoreStatus == 1) {
            		gameOverPaint.setColor(Color.rgb(23, 108, 237));
            		canvas.drawText("NEW RECORD", Width / 2, 330 * density, gameOverPaint);
            	}
            	else if(scoreStatus == 2) {
            		Rect perfect = new Rect((int) (Width / 2 - 80 * density), (int) (300 * density),
                            (int) (Width / 2 + 80 * density), (int) (300 * density + 40 * density));
            		
            		gameOverPaint.setColor(Color.rgb(245, 037, 037));
            		canvas.drawRect(perfect, gameOverPaint);
            		
            		gameOverPaint.setColor(Color.YELLOW);
            		canvas.drawText("PERFECT", Width / 2, 330 * density, gameOverPaint);
            	}
            	
            	
         		GameOver();
         		gameOverCount = 0;             		
         		return;
         	}
          	
            gameOverCount++;
             	
       	 }
        	 
        	 
      

        public void DrawAll(Canvas canvas) {

        	if (status == PROCESS || status == GAMEOVER_PROCESS) {
	        	DrawBackGround(canvas);        	
	        	DrawScore(canvas);
	        	
	        	DrawCube(canvas);	        	
	        	DrawBottomFigure(canvas);
	        	DrawBottomCircle(canvas);
        	}
        	
        	if (status == GAMEOVER_PROCESS) {    
             	if(gameOverProcessCount < 21 ) {             		
             		int alpha = 255 - 255 / 20 * gameOverProcessCount;             		
             		if (alpha < 40) { alpha = 0; }
             		backGroundFillPaint.setAlpha(alpha);
             		canvas.drawRect(0, 0, Width, Height, backGroundFillPaint);
             		
             	}
             	
             	if (33 < gameOverProcessCount) {             		
             		status = GAMEOVER;             		
             		return;
             	}
             	
             	gameOverProcessCount++;             	
        	}
        	
        	if (status == GAMEOVER) {
        		DrawGameOver(canvas);
        	}
           
        }

        public void run() {
            Canvas canvas = null;
            while (canRun) {                
                try {
					super.sleep(2);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
                
                canvas = mHolder.lockCanvas();
                try {
                    synchronized (mHolder) {
                        switch (status) {
                            case PROCESS:                                                                
                                MoveAll();
                                CheckCube();                                
                                break;
                            case GAMEOVER_PROCESS:                            	                            	
                                break;
                            case ALL_CLEAR:
                                break;
                            case GAMEOVER:                            	                            	
                                break;
                                }
                        
                        DrawAll(canvas);
                    } // sync
                } finally {
                    if (canvas != null)
                        mHolder.unlockCanvasAndPost(canvas);
                } // try

                synchronized (this) {
                    if (isWait)
                        try {
                            wait();
                        } catch (Exception e) {
                            // nothing
                        }
                } // sync

            } // while
        } // run

        public void StopThread() {
            canRun = false;
            synchronized (this) {
                this.notify();
            }
        }

        public void PauseNResume(boolean wait) {
            isWait = wait;
            synchronized (this) {
                this.notify();
            }
        }
    } // GameThread ��

    public boolean TouchEvent(int x, int y) {
    	
    	if (status ==  GAMEOVER_PROCESS)
    		return true;
    	
    	if (status == GAMEOVER) {
    		if (rectGameoverOk.contains(x, y)) {
    			Intent intent = new Intent();
    			intent.setAction("GAMEOVER");
    			mContext.sendBroadcast(intent);        		
         	}
    		
    		return true;
         }
    	 
    	 
        if (rectPrev.contains(x, y)) {
        	       
        	if (sbTick1.isPlayng()) {
        		sbTick2.play();  
        	}
        	else if (sbTick2.isPlayng()){
        		sbTick3.play();
        	}else{
        		sbTick1.play();
        	}
        	
        	preColorTime = 12;
        	
        if (mBelowFigureNum == mFigureTotalNum - 1)
        	mBelowFigureNum = 0;
        else
        	mBelowFigureNum++;
                 	
       	mBelowFigureLeftNum = mBelowFigureNum - 1;        	
       	mBelowFigureRightNum = mBelowFigureNum + 1;
       	
       	if (mBelowFigureLeftNum < 0)
       		mBelowFigureLeftNum = mFigureTotalNum - 1;
       	
       	if (mBelowFigureRightNum > mFigureTotalNum - 1)
       		mBelowFigureRightNum = 0; 
       	
       		return true;
        }
        
        if (rectNext.contains(x, y)) {
        	
        	if (sbTick1.isPlayng()) {
        		sbTick2.play();  
        	}
        	else if (sbTick2.isPlayng()){
        		sbTick3.play();
        	}else{
        		sbTick1.play();
        	}
        	
        	nextColorTime = 12;
        	
        	if (mBelowFigureNum == 0)
                mBelowFigureNum = mFigureTotalNum - 1;
            else
                mBelowFigureNum--;
        	        	
        	mBelowFigureLeftNum = mBelowFigureNum - 1;        	
        	mBelowFigureRightNum = mBelowFigureNum + 1;
        	        	
        	if (mBelowFigureLeftNum < 0)
        		mBelowFigureLeftNum = mFigureTotalNum - 1;
        	
        	if (mBelowFigureRightNum > mFigureTotalNum - 1)
        		mBelowFigureRightNum = 0;           	
        	 
        	return true;
        	
        }       
        
        return false;
    }

    // -------------------------------------
    // onTouch Event
    // -------------------------------------
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_DOWN)
            return true;
        synchronized (mHolder) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            return TouchEvent(x, y);
        }
        // return true;
    }
} // SurfaceView
