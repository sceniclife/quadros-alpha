package com.example.quadros.alpha;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class BoardView extends View {

	static final int GRID_LINE_WIDTH = 6;
	
	private Bitmap mNotPressed;
	private Bitmap mPressed;
	private Paint mPaint;
	private QuadrosGame mGame;
	//Max boardsize = 9;
	private int boardsize = 3;


	public BoardView(Context context) {
		super(context);
		initialize();

	}
	public BoardView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialize();
	}
	public BoardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize();
	}


	public void initialize() {
		mNotPressed = BitmapFactory.decodeResource(getResources(), R.drawable.gray);
		mPressed = BitmapFactory.decodeResource(getResources(), R.drawable.red);
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	}


	public void setGame(QuadrosGame game) {
		mGame = game;
	}

	public void setBoard(int boardz) {
		boardsize = boardz;
	}

	public int getBoardSize(){
		return this.boardsize;
	}

	public int getBoardCellWidth() {
		return getWidth() / boardsize;
	}
	public int getBoardCellHeight() {
		return getHeight() / boardsize;
	}

	public void showAnswers(){

	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// Determine the width and height of the View
		int boardWidth = getWidth();
		int boardHeight = getHeight();
		
		// Draw the vertical and horixontal board lines
		int cellWidth = getBoardCellWidth();
		int cellHeight = getBoardCellHeight();
		
		mPaint.setColor(Color.LTGRAY);
		mPaint.setStrokeWidth(600);
		canvas.drawRect(0, 0, boardHeight, boardHeight, mPaint);
		/*
		for (int i = 0; i <= boardsize; i++){
			canvas.drawLine(cellWidth * i, 0, cellWidth * i, boardHeight, mPaint);
			canvas.drawLine(0, cellWidth * i, boardHeight, cellWidth * i, mPaint);
		}
		*/

		// Draw all
		for (int i = 0; i < (boardsize*boardsize); i++) {
			int col = i % boardsize;
			int row = i / boardsize;
			// Define the boundaries of a destination rectangle for the image
			int xTopLeft = col * cellWidth + GRID_LINE_WIDTH;
			int yTopLeft = row * cellHeight + GRID_LINE_WIDTH;
			int xBottomRight = xTopLeft + cellWidth - 10;
			int yBottomRight = yTopLeft + cellHeight - GRID_LINE_WIDTH - 6;
			if (mGame != null && mGame.getBoardLocation(i)) {
				canvas.drawBitmap(mPressed,
						null, // src
						new Rect(xTopLeft, yTopLeft, xBottomRight, yBottomRight), // dest
						null);
			}
			else if (mGame != null && !mGame.getBoardLocation(i)) {
				canvas.drawBitmap(mNotPressed,
						null, // src
						new Rect(xTopLeft, yTopLeft, xBottomRight, yBottomRight), // dest
						null);
			}

		}
	}
}



