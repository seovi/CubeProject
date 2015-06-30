package com.lhscdh.cubeproject;

import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class Cube {

	public int x, y;
	public boolean isDead;	
	public Bitmap img;

	public int figureNum;
	int i = 0;

	public Cube(int x, int y, int figureNum) {
		this.x = x;
		this.y = y;

		if (figureNum == 0)
			img = GameView.mFigure[0];
		else if (figureNum == 1)
			img = GameView.mFigure[1];
		else if (figureNum == 2)
			img = GameView.mFigure[2];
		else if (figureNum == 3)
			img = GameView.mFigure[3];
		// else if (number == 4)
		// img = BitmapFactory.decodeResource(
		// GameView.mContext.getResources(), R.drawable.diamond);
		// else if (number == 5)
		// img = BitmapFactory.decodeResource(
		// GameView.mContext.getResources(), R.drawable.smile);
		// else if (number == 6)
		// img = BitmapFactory.decodeResource(
		// GameView.mContext.getResources(), R.drawable.hexagon);
		//

	}

	//GameTread의 CheckCube()가 하는게 맞지 않나? 
	public boolean Move() {
		y = y + (int)(1 * GameView.density);

		if (y == 520 * GameView.density
				&& GameView.mFigureList.get(0) != GameView.mBelowFigureNum) {
			// GameView.mFigureList.remove(0);
			GameView.status = GameView.GAMEOVER;
			isDead = true;
			
			return false;
		} else if (y == 520 * GameView.density
				&& GameView.mFigureList.get(0) == GameView.mBelowFigureNum) {
			GameView.mScore = GameView.mScore + 1;
			GameView.mFigureList.remove(0);
			GameView.mCube.remove(0);
			
			return true;
		}		
	}

}
