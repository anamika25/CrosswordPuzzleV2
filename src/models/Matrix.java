package models;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import Utilities.*;

public class Matrix{
	private
	int orig[][];
	int comp[][];
	ArrayList<Double> neuralNetworkInput ;
	String[] letters = 
		{"A", "B", "C", "D", "E", "F", "G", "H", "I",
		"J", "K", "L", "M", "N", "O", "P", "Q", "R",
		"S", "T", "U", "V", "W", "X", "Y", "Z"};
	
	public Matrix(int[][] inp){
		orig = inp;
		comp = new int[Constants.com_height][Constants.com_width];	
		neuralNetworkInput = new ArrayList<Double>();
	}
	
	public void matrixCompression(int index) throws IOException{
		
		//FileWriter fw = new FileWriter("train.txt", true);
		int flag = 0;
		for(int i=0;i<Constants.com_height;i++){
			for(int j=0;j<Constants.com_width;j++){
				flag = 0;
				for(int k=0;k<Constants.mat_comp;k++){
					for(int l=0;l<Constants.mat_comp;l++){
						if(orig[i*Constants.mat_comp + k][j*Constants.mat_comp + l]==1)
							{
								flag = 1;
								break;
							}	
					}
					if(flag==1)
						break;
				}
				if(flag==1)
					comp[i][j]=1;
				else
					comp[i][j] = 0;
				neuralNetworkInput.add((double)comp[i][j]);
				//fw.write(comp[i][j]+" ");
			}
		}
		//fw.write(index+ "\n");
		//fw.close();
	}
	public int[][] getComp(){
		return comp;
	}
	
	public ArrayList<Double> getNeuralNetworkInput() {
		return neuralNetworkInput;
	}
	
	
}