package com.lhscdh.cubeproject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
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
	// 프로그램 상태에 관한 상수
	final static int PROCESS = 1; // 게임 중
	final static int STAGE_CLEAR = 2; // Stage Clear
	final static int GAMEOVER = 3; // Game Over
	final static int ALL_CLEAR = 4; // All Clear

	// SurfaceView의 변수들
	static GameThread mThread; // GameThread
	static SurfaceHolder mHolder; // SurfaceHolder
	static Context mContext; // Context

	static ArrayList<Cube> mCube;
	static Bitmap mFigure[];
	static float density;

	static int mFigureTotalNum = 4;
	int mUpFigureNum;
	static int mBelowFigureNum = 0; // 변수명 헷갈림;; 잘 지어야하는데.. 일단 이건 아래에 있는 도형 숫자

	static int mFigureGoalNum;
	static int mScore = 0;

	static int mFigureIndex = 0;

	static ArrayList<Integer> mFigureList;

	// Game의 변수들
	static int Width, Height; // View

	static int status = PROCESS; // 현재 진행 상태

	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);

		mHolder = holder;
		mContext = context;
		mThread = new GameThread(holder, context);

		InitGame();
		InitPrevNext();
		setFocusable(true);
	}

	private void InitGame() {
		Display display = ((WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		Width = display.getWidth();
		Height = display.getHeight();
		density = mContext.getResources().getDisplayMetrics().density;

		mCube = new ArrayList<Cube>();
		mFigure = new Bitmap[mFigureTotalNum];
		mFigure[0] = BitmapFactory.decodeResource(getResources(),
				R.drawable.circle);
		mFigure[1] = BitmapFactory.decodeResource(getResources(),
				R.drawable.triangle);
		mFigure[2] = BitmapFactory.decodeResource(getResources(),
				R.drawable.love);
		mFigure[3] = BitmapFactory.decodeResource(getResources(),
				R.drawable.square);
		// mFigure[4] = BitmapFactory.decodeResource(getResources(),
		// R.drawable.diamond);
		// mFigure[5] = BitmapFactory.decodeResource(getResources(),
		// R.drawable.smile);
		// mFigure[6] = BitmapFactory.decodeResource(getResources(),
		// R.drawable.hexagon);

		mFigureList = new ArrayList<Integer>();

	}

	private Rect rectPrev, rectNext;

	public void InitPrevNext() {

		rectPrev = new Rect(0, (int) (90 * density), (int) (100 * density),
				Height);

		rectNext = new Rect((int) (Width - 100 * density),
				(int) (90 * density), Width, Height);

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
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		StopGame();
	}

	// -------------------------------------
	// 스레드 완전 정지
	// -------------------------------------
	public static void StopGame() {
		Log.v(TAG, "StopGame");
		mThread.StopThread();
	}

	// -------------------------------------
	// 스레드 일시 정지
	// -------------------------------------
	public static void PauseGame() {
		mThread.PauseNResume(true);
	}

	// -------------------------------------
	// 스레드 재기동
	// -------------------------------------
	public static void ResumeGame() {
		mThread.PauseNResume(false);
	}

	// -------------------------------------
	// 게임 초기화
	// -------------------------------------
	public void RestartGame() {
		mThread.StopThread(); // 스레드 중지

		// 현재의 스레드를 비우고 다시 생성
		mThread = null;
		mThread = new GameThread(mHolder, mContext);
		mThread.start();
	}

	public void GameOver() {
		// StopGame();
		Intent intent = new Intent();
		intent.setAction("GAMEOVER");
		mContext.sendBroadcast(intent);
		// PauseGame(); // Thread 정지
		// StartGame Activity 실행
		// mContext.startActivity(new Intent(mContext, StartActivity.class));
		// 자신(MainActuivity)은 종료
		// MainActivity.GameOver();
		// ((Activity) getContext()).finish();
	}

	class GameThread extends Thread {
		boolean canRun = true; // Thread 제어용
		boolean isWait = false; // Thread 제어용
		boolean mRunning = true;
		
		boolean isScored = false;
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
			for (Cube tmp : mCube) {
				
				Log.v(TAG, "CeckCube");
				if (mFigureList.size() > 0
						&& mFigureList.get(0) == mBelowFigureNum
						&& tmp.y > 520 * density) {
					mScore = mScore + 100;
					// tmp.isDead = true;
					mFigureList.remove(0);
					break;
				}
			}
		}

		int pastNum = -1;

		public void DrawCube() {
			Random random = new Random();

			if (loop == 90) {
				while (true) {

					mUpFigureNum = random.nextInt(mFigureTotalNum);

					if (pastNum != mUpFigureNum) {
						pastNum = mUpFigureNum;
						break;
					}

				}
				mCube.add(new Cube(
						(Width - mFigure[mUpFigureNum].getWidth()) / 2,
						(int) (90 * density), mUpFigureNum));

				mFigureList.add(mUpFigureNum);
				mFigureIndex++;
				loop = 0;
			}
		}

		public void DrawFigure() { // 밑에 보여지는 도형

		}

		public void MoveAll() {
			loop++;			
			
			for (int i = mCube.size() - 1; i >= 0; i--) {
				
				if(i == 0) {
					isScored = mCube.get(i).Move();
				}
				else {
					mCube.get(i).Move();
				}
				
				if (mCube.get(i).isDead == true) {
					mCube.remove(i);
				}
			}
		}

		public void DrawAll(Canvas canvas) {
			paint.setColor(Color.WHITE);

			canvas.drawRect(0, 0, Width, Height, paint);

			paint.setColor(Color.BLUE);
			paint.setTextSize(50 * density);
			mTextWidth = (int) Math.ceil(paint.measureText(Integer
					.toString(mScore)));
			canvas.drawText(Integer.toString(mScore), (Width - mTextWidth) / 2,
					60 * density, paint);

			paint.setColor(Color.BLACK);
			canvas.drawLine(0, 90 * density, Width, 90 * density, paint);

			for (Cube tmp : mCube) {
				canvas.drawBitmap(tmp.img, tmp.x, tmp.y, null);
			}
			canvas.drawLine((int) (100 * density), (int) (90 * density),
					(int) (100 * density), Height, paint);
			canvas.drawLine(Width - 100 * density, (int) (90 * density), Width
					- 100 * density, Height, paint);
			
			Paint circle = new Paint();
			circle.setAntiAlias(true);
			circle.setStyle(Paint.Style.STROKE);
			circle.setStrokeWidth(3);

			if(isScored) {
				circle.setColor(Color.LTGRAY);	
				if(circleColorTime > 100) {
					Log.v(TAG, Integer.toString(circleColorTime));
					circleColorTime = 0;
					isScored = false;					
				}
				circleColorTime++;
			}
			
			canvas.drawCircle(Width / 2, (550 * density), (50 * density),
					circle);
			canvas.drawBitmap(mFigure[mBelowFigureNum],
					(Width - mFigure[mBelowFigureNum].getWidth()) / 2,
					(int) (520 * density), null);
		}

		// -------------------------------------
		// 스레드 본체
		// -------------------------------------
		public void run() {
			Canvas canvas = null;
			while (canRun) {
				canvas = mHolder.lockCanvas();
				try {
					synchronized (mHolder) {
						switch (status) {
						case PROCESS:
							Log.v(TAG, "PROCESS");
							// if (mTimerTask == null) {
							// mRunning = true;
							// doTimerTask();
							// }
							// CheckCube();
							DrawCube();
							MoveAll();
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
						}
					} // sync
				} finally {
					if (canvas != null)
						mHolder.unlockCanvasAndPost(canvas);
				} // try

				// 스레드 일시 정지
				synchronized (this) {
					if (isWait) // Pause 모드이면
						try {
							wait(); // 스레드 대기
						} catch (Exception e) {
							// nothing
						}
				} // sync

			} // while
		} // run

		// -------------------------------------
		// 스레드 완전 정지
		// -------------------------------------
		public void StopThread() {
			canRun = false;
			synchronized (this) {
				this.notify();
			}
		}

		// -------------------------------------
		// 스레드 일시정지 / 재기동
		// -------------------------------------
		public void PauseNResume(boolean wait) {
			isWait = wait;
			synchronized (this) {
				this.notify();
			}
		}
	} // GameThread 끝

	public boolean TouchEvent(int x, int y) {
		if (rectPrev.contains(x, y)) {
			if (mBelowFigureNum == 0)
				mBelowFigureNum = mFigureTotalNum - 1;
			else
				mBelowFigureNum--;
		}
		if (rectNext.contains(x, y)) {
			if (mBelowFigureNum == mFigureTotalNum - 1)
				mBelowFigureNum = 0;
			else
				mBelowFigureNum++;
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