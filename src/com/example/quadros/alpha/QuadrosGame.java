package com.example.quadros.alpha;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class QuadrosGame {

	public int level;
	public int rows;
	public int cols;
	public Set<Integer> selectedCells;
	public boolean[] board;
	public int boardSize;

	public QuadrosGame(int tier, int lvl) {
		level = lvl;
		rows = tier + 2;
		cols = tier + 2;
		boardSize = rows*cols;
		board = new boolean[boardSize];
		generateCells(this.level);
	}

	public void generateCells(int level) {
		Random rand = new Random();
		selectedCells = new HashSet<Integer>();
		while (this.selectedCells.size() < level+2) {
			int r = rand.nextInt(this.rows * this.cols);
			this.selectedCells.add(r);
		}
	}

	public boolean setMove(int location) {
		if(location < 0 || location > this.boardSize)
			throw new IllegalArgumentException("location must be between 0 and board size inclusive: " + location);

		if(!this.board[location] && selectedCells.contains(location)) {
			this.board[location] = true;
			//this.score += 10;

			return true;
		} 

		// selected an already correct cell 
		else if(this.board[location] && selectedCells.contains(location)) {
			// return false to not pla
			return false;
		}

		else {
			//this.life--;
			return false;
		}
	}

	public HashSet<Integer> getSelectedCells() {
		return (HashSet<Integer>) this.selectedCells;
	}

	public boolean getBoardLocation(int i) {
		return this.board[i];
	}

	public void setBoard(int i, boolean value) {
		this.board[i] = value;
	}

	public int getBoardSize() {
		return this.boardSize;
	}

}
