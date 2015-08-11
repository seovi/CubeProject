package com.lhscdh.cubeproject;

import android.graphics.Bitmap;
import android.util.Log;

public class Cube {

	public float x, y;
	public boolean isDead;	
	public Bitmap img;

	public int figureNum;
	int i = 0;

	public Cube(float x, float y, int figureNum) {
		this.x = x;
		this.y = y;

		switch(figureNum) {
		case 0 : img = GameView.mFigure[0];break;
		case 1 : img = GameView.mFigure[1];break;
		case 2 : img = GameView.mFigure[2];break;
		case 3 : img = GameView.mFigure[3];break;
		case 4 : img = GameView.mFigure[4];break;
		default:
			img = GameView.mFigure[0];break;
		}		

	}
 
	public void Move(int alphaSpeed) {
		
		y = y + (2.0f * GameView.density) + (float)alphaSpeed * 0.2f;
	
	}

}
