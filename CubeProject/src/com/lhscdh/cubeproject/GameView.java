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

public class GameView extends SurfaceView implements Callback {

    public static final String TAG = "GameView";

    final static int PROCESS = 1;

    final static int STAGE_CLEAR = 2; // Stage Clear

    final static int GAMEOVER = 3; // Game Over

    final static int ALL_CLEAR = 4; // All Clear

    final static int REPLAY = 5;

    final static int PAUSE = 6;

    static GameThread mThread; // GameThread

    static SurfaceHolder mHolder; // SurfaceHolder

    static Context mContext; // Context

    static ArrayList<Cube> mCube;

    static Bitmap mPause, mPlay;

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
        mFigure[0] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.circle), 100, 100, true);
        mFigure[1] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.love), 100, 100, true);
        mFigure[2] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.triangle), 100, 100, true);        
        mFigure[3] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.diamond), 100, 100, true);
        mFigure[4] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.pentagon), 100, 100, true);
        
        mPause = BitmapFactory.decodeResource(getResources(), R.drawable.pause);
        mPlay = BitmapFactory.decodeResource(getResources(), R.drawable.play);        
       
        mFigureList = new ArrayList<Integer>();

    }

    private Rect rectPrev, rectNext;

    private Rect rectPause, rectPlay;

    // canvas.drawBitmap(mPause, Width - 100 * density, 10 * density, null);
    public void InitTouchCor() { // init touch coordinate

        rectPrev = new Rect(0, (int) (90 * density), (int) (120 * density), Height - 350);

        rectNext = new Rect((int) (Width - 120 * density), (int) (90 * density), Width, Height - 350);

        rectPause =
            new Rect((int) (Width - 100 * density), (int) (10 * density),
                (int) (Width - 100 * density + mPause.getWidth()), (int) (10 * density + mPause.getHeight()));

        rectPlay =
            new Rect((int) (Width - 100 * density), (int) (10 * density),
                (int) (Width - 100 * density + mPlay.getWidth()), (int) (10 * density + mPlay.getHeight()));
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

    public static void PauseGame() {
        mThread.PauseNResume(true);
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

    public void GameOver() {
    	SharedPreferences sharedPref = mContext.getSharedPreferences("PrefName", Context.MODE_PRIVATE);    	
    	
    	if(sharedPref.getInt("key_high", 0) < mScore) {
    		SharedPreferences.Editor editor = sharedPref.edit();
    		editor.putInt("key_high", mScore);    		
    		editor.commit();
    	}
    	
        Intent intent = new Intent();
        intent.setAction("GAMEOVER");
        mContext.sendBroadcast(intent);
    }

    class GameThread extends Thread {
        boolean canRun = true;

        boolean isWait = false;

        boolean mRunning = true;

        int circleColorTime = 0;

        int loop;

        int mTextWidth;

        TimerTask mTimerTask;

        Timer mTimer = new Timer();

        final Handler mHandler = new Handler();

        Paint paint = new Paint();

        public GameThread(SurfaceHolder holder, Context context) {
            paint.setColor(Color.WHITE);
            paint.setAntiAlias(true);
        }

        public void CheckCube() {
            Log.v(TAG, "CeckCube");

            for (Cube tmp : mCube) {

                if (tmp.y >= 475 * density && mFigureList.get(0) != mBelowFigureNum) {
                    status = GAMEOVER;
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
            paint.setColor(Color.WHITE);
            canvas.drawRect(0, 0, Width, Height, paint);

            paint.setColor(Color.GRAY);
            paint.setStrokeWidth(4.0f);
         
            canvas.drawLine(0, 90 * density, Width, 90 * density, paint);

            canvas.drawLine((int) (120 * density), (int) (90 * density), (int) (120 * density), Height - 350, paint);

            canvas.drawLine(Width - 120 * density, (int) (90 * density), Width - 120 * density, Height - 350, paint);

            canvas.drawLine(0, Height - 350, 120 * density, Height - 350, paint);

            canvas.drawLine(Width - 120 * density, Height - 350, Width, Height - 350, paint);

            canvas.drawLine(0, Height - 150, Width, Height - 150, paint);

            canvas.drawBitmap(mPause, Width - 100 * density, 10 * density, null);
        }

        public void DrawScore(Canvas canvas) {
            paint.setColor(Color.BLUE);
            paint.setTextSize(50 * density);
            mTextWidth = (int) Math.ceil(paint.measureText(Integer.toString(mScore)));
                        
            paint.setTypeface(Typeface.MONOSPACE);
            canvas.drawText(Integer.toString(mScore), (Width - mTextWidth) / 2, 60 * density, paint);            

        }

        public void DrawBottomCircle(Canvas canvas) {

            Paint circle = new Paint();
            circle.setAntiAlias(true);
            circle.setStyle(Paint.Style.STROKE);
            circle.setStrokeWidth(4);
            circle.setColor(Color.GRAY);

            if (mIsScored) {
                circle.setColor(Color.MAGENTA);
                if (circleColorTime > 10) {
                    Log.v(TAG, Integer.toString(circleColorTime));
                    circleColorTime = 0;
                    mIsScored = false;
                }
                circleColorTime++;
            }

            canvas.drawCircle(Width / 2, (480 * density) +  mFigure[mBelowFigureNum].getWidth() / 2, (40 * density), circle);

        }

        public void DrawBottomFigure(Canvas canvas) {
        	
        	canvas.drawBitmap(mFigure[mBelowFigureLeftNum], 50, (int) (480 * density), null);

            canvas.drawBitmap(mFigure[mBelowFigureNum], (Width - mFigure[mBelowFigureNum].getWidth()) / 2,
                (int) (480 * density), null);            
           
            canvas.drawBitmap(mFigure[mBelowFigureRightNum], Width - 150, (int) (480 * density), null);
        }

        public void DrawAll(Canvas canvas) {

        	DrawBackGround(canvas);
        	DrawScore(canvas);
            
            if(status == PROCESS) {
            	DrawCube(canvas);
            	DrawBottomCircle(canvas);
            	DrawBottomFigure(canvas);}
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
                            case STAGE_CLEAR:
                                break;
                            case ALL_CLEAR:
                                break;
                            case GAMEOVER:
                                // StopGame();
                                // PauseGame();
                                // DrawAll(canvas);
                                // PauseGame();
                                GameOver();
                                break;
                            case REPLAY:
                                mPause = BitmapFactory.decodeResource(getResources(), R.drawable.pause);
                                DrawAll(canvas);
                                status = PROCESS;
                                break;
                            case PAUSE:
                                mPause = BitmapFactory.decodeResource(getResources(), R.drawable.play);
                                DrawAll(canvas);
                                PauseGame();
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
        if (rectPrev.contains(x, y)) {
        	
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
            
        }
        
        if (rectNext.contains(x, y)) {
        	
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
        	 
        	
        }
        if (rectPause.contains(x, y) || rectPlay.contains(x, y)) {
            if (status == PROCESS)
                status = PAUSE;
            else if (status == PAUSE) {
                status = REPLAY;
                ResumeGame();

            }
        }
        if (rectPlay.contains(x, y)) {

        }
        return true;
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

// public void doTimerTask() {
// mTimerTask = new TimerTask() {
// public void run() {
// mHandler.post(new Runnable() {
// public void run() {
// if (!mRunning)
// return;
// // mCube.add(new Cube((int) (150 * density),
// // (int) (90 * density)));
// }
// });
// }
// };
// mTimer.schedule(mTimerTask, 0, 3000);
// }
//
// public void stopTask() {
// if (mTimerTask != null) {
// mRunning = false;
// mTimerTask.cancel();
// mTimerTask = null;
// }
// }
//
// public void doTimerPause() {
// mRunning = !mRunning;
// }