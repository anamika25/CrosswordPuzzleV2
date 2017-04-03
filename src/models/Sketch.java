package models;

import java.util.ArrayList;
import java.util.List;
import Utilities.Constants;

public class Sketch {
	private int label;
	private List<Stroke> strokes;
	private int row;
	private int column;
	private int [][] imageMatrix;
	public Sketch(int row, int column){
		strokes = new ArrayList<Stroke>();
		this.row = row;
		this.column = column;
		this.label = 0;
		imageMatrix = new int[Constants.height][Constants.width];
	}
	
	public List<Stroke> getStrokes(){
		return this.strokes;
	}
	
	public void addStroke(Stroke str){
		this.strokes.add(str);
	}
	
	public int getRow(){
		return row;
	}
	
	public int getColumn(){
		return column;
	}
	
	public int getLabel(){
		return label;
	}
	
	public void setLabel(int label){
		this.label = label;
	}
	
	public void setMatrixIndex(int x, int y) {
		if(x >= 0 && y >=0 && x < Constants.height && y < Constants.width ) {
			imageMatrix[x][y] = 1;
		}
	}
	
	public int[][] getImageMatrix() {
		return imageMatrix;
	};
}
