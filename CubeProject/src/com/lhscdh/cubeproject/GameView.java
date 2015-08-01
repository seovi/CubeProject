package com.lhscdh.cubeproject;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

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
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.view.SurfaceHolder.Callback;
import android.view.ViewDebug.IntToString;

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

    static float density;

    static int mFigureTotalNum = 5;

    int mUpFigureNum;

    static int mBelowFigureNum = 1;
    static int mBelowFigureLeftNum = 0;
    static int mBelowFigureRightNum = 2;

    static int mFigureGoalNum;

    static int mScore = 0;

    static boolean mIsScored = false;
    
    static boolean mIsTouchPre = false;
    static boolean mIsTouchNext = false;
    
    int preColorTime = 0;
    int nextColorTime = 0;

    static int mFigureIndex = 0;

    static ArrayList<Integer> mFigureList;

    static int Width, Height; // View

    static int status = PROCESS;

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
       
        mFigureList = new ArrayList<Integer>();

    }

    private Rect rectPrev, rectNext;

    private Rect rectPlay;

    // canvas.drawBitmap(mPause, Width - 100 * density, 10 * density, null);
    public void InitTouchCor() { // init touch coordinate

        rectPrev = new Rect(0, (int) (90 * density), (int) (120 * density), Height - 350);

        rectNext = new Rect((int) (Width - 120 * density), (int) (90 * density), Width, Height - 350);
        
        rectPlay =
                new Rect((int) (Width - 100 * density), (int) (10 * density),
                    (int) (Width - 100 * density + 100), (int) (10 * density + 100));
            
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

    public static void GameOver() {
    	mThread.PauseNResume(true);
    	 
    	SharedPreferences sharedPref = mContext.getSharedPreferences("PrefName", Context.MODE_PRIVATE);    	
    	
    	if(sharedPref.getInt("key_high", 0) < mScore) {
    		SharedPreferences.Editor editor = sharedPref.edit();
    		editor.putInt("key_high", mScore);    		
    		editor.commit();
    	}        
    }

    class GameThread extends Thread {
        boolean canRun = true;

        boolean isWait = false;

        boolean mRunning = true;

        int circleColorTime = 0;
        int gameOverAlphaTime = 0;

        int loop;

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
        	scorePaint.setTypeface(Typeface.SANS_SERIF);
        	scorePaint.setAntiAlias(true);
        	
        	bottomCircle = new Paint();
        	bottomCircle.setAntiAlias(true);
        	bottomCircle.setStyle(Paint.Style.STROKE);
        	bottomCircle.setStrokeWidth(4);
        	bottomCircle.setColor(Color.GRAY);
        	
        	gameOverPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        	gameOverPaint.setColor(Color.WHITE);
        	gameOverPaint.setAlpha(175);
        }

        public void CheckCube() {
            Log.v(TAG, "CeckCube");

            for (Cube tmp : mCube) {

                if (tmp.y >= 475 * density && mFigureList.get(0) != mBelowFigureNum) {
                    status = GAMEOVER_PROCESS;
                    gameOverAlphaTime = 0;
                } else if (tmp.y >= 475 * density && mFigureList.get(0) == mBelowFigureNum) {

                    mScore = mScore + 1;
                    mIsScored = true;
                    mFigureList.remove(0);
                    tmp.isDead = true;
                }

            }
        }

        int pastNum = -1;

        public void DrawCube(Canvas canvas) {
            Random random = new Random();

            if (loop == 70) {
                while (true) {

                    mUpFigureNum = random.nextInt(mFigureTotalNum);

                    if (pastNum != mUpFigureNum) {
                        pastNum = mUpFigureNum;
                        break;
                    }

                }
                mCube.add(new Cube((Width - mFigure[mUpFigureNum].getWidth()) / 2, (int) (90 * density), mUpFigureNum));

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

                mCube.get(i).Move();

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
            	preColorTime--;
            }
            else {
            	prevTouchPaint.setColor(Color.GRAY);
            }
            canvas.drawLine((int) (60 * density) + 20 , (Height - 350 + 90 * density) / 2 - 100, (int) (60 * density) - 20 , (Height - 350 + 90 * density) / 2, prevTouchPaint);
            canvas.drawLine((int) (60 * density) - 20 , (Height - 350 + 90 * density) / 2, (int) (60 * density) + 20 , (Height - 350 + 90 * density) / 2 + 100, prevTouchPaint);
            
            // >
            if (nextColorTime > 0) {
            	nextTouchPaint.setColor(Color.MAGENTA);
            	nextColorTime--;
            }
            else {
            	nextTouchPaint.setColor(Color.GRAY);
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
                if (circleColorTime > 12) {                    
                    circleColorTime = 0;
                    mIsScored = false;
                    bottomCircle.setColor(Color.GRAY);
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
        
        	 if(status == GAMEOVER_PROCESS) {    
             	if(gameOverAlphaTime < 21 ) {             		
             		int alpha = 255 - 255 / 20 * gameOverAlphaTime;
             		gameOverPaint.setAlpha(alpha);
             		canvas.drawRect(0, 0, Width, Height, gameOverPaint);
             		
             	}
             	
             	if (33 <gameOverAlphaTime) {
             		gameOverPaint.setAlpha(255);
             		status = GAMEOVER;             		
             		return;
             	}
             	
             	gameOverAlphaTime++;
        	 }
        	 
        	 if (status == GAMEOVER) {
        		 
        		// gameOverAlphaTime 35 ~ 75
              	if(34 < gameOverAlphaTime && gameOverAlphaTime < 76) {
              		int size = gameOverAlphaTime - 35;
              		size = Height / 40 * size; 
              		
              		canvas.drawRect(0, 0, Width, size, gameOverPaint);
              	}
              	
              	if (76 < gameOverAlphaTime ) {
             		GameOver();
             		gameOverAlphaTime = 0;             		
             		return;
             	}
             	
        	 }
        	 
        	 gameOverAlphaTime++;        	 
        }

        public void DrawAll(Canvas canvas) {

        	DrawBackGround(canvas);        	
        	DrawScore(canvas);
        	
        	DrawCube(canvas);
        	DrawBottomCircle(canvas);
        	DrawBottomFigure(canvas);
        	DrawGameOver(canvas);
           
        }

        public void run() {
            Canvas canvas = null;
            while (canRun) {                
                try {
					super.sleep(5);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
                
                canvas = mHolder.lockCanvas();
                try {
                    synchronized (mHolder) {
                        switch (status) {
                            case PROCESS:
                                Log.v(TAG, "PROCESS");                                
                                MoveAll();
                                CheckCube();
                                DrawAll(canvas);
                                break;
                            case GAMEOVER_PROCESS:
                            	Log.v(TAG, "GAMEOVER_PROCESS");
                            	DrawAll(canvas);
                                break;
                            case ALL_CLEAR:
                                break;
                            case GAMEOVER:            
                            	Log.v(TAG, "GAMEOVER");
                            	DrawGameOver(canvas);
                                break;
                                }
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
    		if (rectPlay.contains(x, y)) {
    			Intent intent = new Intent();
    			intent.setAction("GAMEOVER");
    			mContext.sendBroadcast(intent);        		
         	}
    		
    		return true;
         }
    	 
    	 
        if (rectPrev.contains(x, y)) {
        
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
