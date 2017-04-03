package Utilities;

public class Constants {
	// each cell.
	public static int width = 80;
	public static int height = 80;
	
	//  final compresses matrix. 
	public static int com_width = 10;
	public static int com_height = 10; 
	public static int mat_comp=8; 
	
	//grid params
	public static int gridHeight = 9;
	public static int gridWidth = 9; 
	
	//subpanel params.
	public static int subpanelHeight = 1;
	public static int subpanelWidth = 1;
	
	//neural network params
	public static int numAttributes = 101;
	public static double eta = 0.05;
	public static int hiddenLayers = 2;
	public static int[] hiddenNodes = {80, 20};
	public static int outputNodes = 26;
	
	// puzzle related constants
	public static String solutionPath = "solution_1.txt";
	public static String puzzlePath = "puzzle_1.txt";
	public static String puzzleRulesPath = "Puzzle1_rules.txt";

}
