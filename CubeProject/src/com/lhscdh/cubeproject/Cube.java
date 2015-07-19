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
		else if (figureNum == 4)
			img = GameView.mFigure[4];

	}
 
	public void Move() {
		y = y + (int)(2 * GameView.density);
	
	}

}
