package controller;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.SwingConstants;
import javax.swing.event.MouseInputAdapter;
import com.sun.javafx.collections.MappingChange.Map;

import Utilities.Constants;
import learner.NeuralNetwork;
import learner.Neuron;
import learner.Utilities;
import learner.Attribute;
import learner.FileReaderUtility;
import learner.Replacement;
import learner.Data;


import models.*;


import models.Grid;
import models.Sketch;
import models.Stroke;
import puzzle.Puzzle;
import view.CrosswordPanel;
import view.Field;
import view.RulesPanel;
import view.CrosswordPanel.SubPanel;


public class CrosswordController extends MouseInputAdapter {
	private CrosswordPanel crosswordPanel;
	private RulesPanel rulesPanel;
    private Grid grid;                  
    private Stroke currentStroke;
    private Sketch currentSketch;
    private Timer timer;
    private TimerTask timerTask;
    private Field currentField;
    private HashMap<Field, ArrayList<Sketch>> fieldSketchMap;
    private SubPanel currentPanel;
	private int count=0;
	private static int index = 0;
	private NeuralNetwork neuralNetwork;
	private Puzzle puzzle;
	public ArrayList<Field> wrongFields;
	public int game ;
	
    public CrosswordController(CrosswordPanel panel, Grid grid, Puzzle puzzle, RulesPanel rulesPanel, int game) {
        this.crosswordPanel = panel;
        this.rulesPanel = rulesPanel;
        this.grid = grid;
        this.timer = new Timer();
        this.fieldSketchMap = new HashMap<Field, ArrayList<Sketch>>();
        this.puzzle = puzzle;
        this.wrongFields = new ArrayList<Field>();
        this.game = game;
    }
    
    public CrosswordController(CrosswordPanel panel, Grid grid) {
        this.crosswordPanel = panel;
        this.grid = grid;
        this.timer = new Timer();
        this.fieldSketchMap = new HashMap<Field, ArrayList<Sketch>>();
        this.wrongFields = new ArrayList<Field>();
    }

    public void mousePressed(MouseEvent e) {
        SubPanel panel = (SubPanel)e.getSource();
        Component component = panel.getComponentAt(e.getPoint());
        if (component instanceof Field) {
        	Field field = (Field)component;
            int x = field.getFieldX();
            int y = field.getFieldY();
	        if(timerTask != null)
	        	timerTask.cancel();
	        if(_changedField(e.getX(), e.getY(), panel) && currentSketch != null){
        		try {
					_startNewSketch();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
        	}
	        currentStroke = new Stroke(new ArrayList<Point>());
	        currentStroke.addPoint(e.getX(), e.getY());        		
	        this.currentField = field;
            
        }
    }
    
    public void mouseDragged(MouseEvent e) {
    	SubPanel panel = (SubPanel)e.getSource();
    	currentStroke.addPoint(e.getX(), e.getY());
        panel.setOngoingStroke(currentStroke);
        panel.repaint();
    }
    
    public void mouseReleased(MouseEvent e) {
    	SubPanel panel = (SubPanel)e.getSource();
    	panel.resetPoints();
    	if(currentSketch == null)
    	{
    		int row = _getStrokeRow()+(panel.getYOffset());
    		int column = _getStrokeColumn()+(panel.getXOffset());
    		
    		currentSketch = new Sketch(row, column);
    		//System.out.println("the row is " + panel.getYOffset());
    		//currentSketch = new Sketch(row, column);
    		//System.out.println("the column is " + panel.getXOffset());
    	}
    	
    	currentSketch.addStroke(currentStroke);
    	currentField = (Field)panel.getComponentAt(e.getPoint());
    	for(Point p : currentStroke.getPoints()) {
    		System.out.println(p.getX() + " , " + p.getY());
    		currentSketch.setMatrixIndex((int)(p.getY()), (int)(p.getX()));
    	}
 
    	
    	if(!panel.getSketches().contains(currentSketch)) {
    		panel.addSketch(currentSketch);
    	}
    	else {
    		List<Sketch> sketches = panel.getSketches();
    		int idx = sketches.indexOf(currentSketch);
    		sketches.set(idx, currentSketch);	
    	}
    	
    	currentPanel = panel;
    	ArrayList<Sketch> sketchList;
    	if(fieldSketchMap.containsKey(currentField))
    		sketchList = fieldSketchMap.get(currentField);
    	else sketchList = new ArrayList<Sketch>();
    	
    	this.currentPanel = panel;
    	
    	if(!sketchList.contains(currentSketch))
    		sketchList.add(currentSketch);
		fieldSketchMap.put(currentField, sketchList);
    	
    	timerTask = new TimerTask() {
  		  @Override
  		  public void run() {
  		    try {
				_startNewSketch();
			} catch (IOException e) {
				// Auto-generated catch block
				e.printStackTrace();
			}
  		  }
  		};
  		timer.schedule(timerTask, 1*1000);
    }
    
    public void mouseClicked(MouseEvent e) {
    	SubPanel panel = (SubPanel)e.getSource();
        Component component = panel.getComponentAt(e.getPoint());
        if (component instanceof Field) {
            Field field = (Field)component;
            int x = field.getFieldX(), y = field.getFieldY();
            //game.setNumber(x, y, "A");
            List<Sketch> sketches = currentPanel.getSketches();
            List<Sketch> fieldSketch = fieldSketchMap.get(field);
            for(Sketch s: fieldSketch) {
            	int idx = sketches.indexOf(s);
            	if(idx != -1)
            		sketches.remove(idx);
            	panel.repaint();
            }            	           
        }
    }
    
    public void mouseEntered(MouseEvent e) { }
    public void mouseExited(MouseEvent e) { }
    
    public void trainNeuralNetwork() {
    	ArrayList<Attribute> attributes = Utilities.constructAttributes();
    	
    	 FileReaderUtility fileData = new FileReaderUtility("train_all_10.txt"); //TO DO
         ArrayList<String> fileDataRows = fileData.ReadFile();
         Utilities.randomizeData(fileDataRows);

         ArrayList<Attribute> attrs = new ArrayList<Attribute>();
         
         for(int i = 0 ; i < (Constants.numAttributes - 1);  ++i) {
        	 attrs.add(attributes.get(i));
         }
         
         int start = 0;
         double pruneDataSetSize = 10/3.0;
         
         Data rawData  = Utilities.preProcessData(fileDataRows, attributes.get(attributes.size()-1), attributes);
         Utilities.addInputNodes(rawData,attributes, Replacement.LOG);
         double eta = Constants.eta;
         
         int outputNodes =attributes.get(attributes.size()-1).getValues().size();
         ArrayList<Integer> hidden = new ArrayList<Integer>();
         int numOfHiddenLayers = Constants.hiddenLayers;
         int argumentNum = 5;
         for(int i = 0; i < numOfHiddenLayers; ++i) {
             hidden.add(Constants.hiddenNodes[i]);
             ++argumentNum;
         }
         
         neuralNetwork = new NeuralNetwork(hidden, outputNodes, rawData.inputVectors.get(0).size(), attributes.get(attributes.size()-1));
         int split =  (int)(rawData.inputVectors.size()/pruneDataSetSize);
         Data trainData = new Data();
         Data validationData = new Data();

         for(int j = 0 ; j < split ; ++j) {
             validationData.rawData.add(rawData.rawData.get(j));
             validationData.inputVectors.add(rawData.inputVectors.get(j));
         }

         for(int j = split; j < rawData.inputVectors.size(); ++j){
             trainData.rawData.add(rawData.rawData.get(j));
             trainData.inputVectors.add(rawData.inputVectors.get(j));
         }
         neuralNetwork.train(trainData, attributes.get(attributes.size()-1), eta,validationData);
         neuralNetwork.updateToBestWeights();
         System.out.println();
    }
    
    private int _getStrokeRow(){
		return _getStrokeRow(currentStroke.getFirstPoint().y);
    }
    
    private int _getStrokeRow(int y){
		return y/80;
    }
    
    private int _getStrokeColumn(){
		return _getStrokeColumn(currentStroke.getFirstPoint().x);
    }
    
    private int _getStrokeColumn(int x){
		return x/80;
    }
    
    private boolean _changedField(int x, int y, SubPanel panel){
    	return currentSketch == null || currentSketch.getRow() != _getStrokeRow(y)+(panel.getYOffset()) || currentSketch.getColumn() != _getStrokeColumn(x)+(panel.getXOffset());
    }
    
    private void _startNewSketch() throws IOException{
    	if(currentSketch != null){
    		//_setFieldValue(_classifySketch(currentSketch));
    		List<Sketch> sketches = currentPanel.getSketches();
    	    int idx = sketches.indexOf(currentSketch);
    	    if(idx != -1) sketches.remove(idx);
    	    	
    	    List<Sketch> fieldSketch = fieldSketchMap.get(currentField);
    	    for(Sketch s: fieldSketch) {
            	int id = sketches.indexOf(s);
            	if(id != -1)
            		sketches.remove(id);
            	currentPanel.repaint();
            }
    	    int[][] inp = currentSketch.getImageMatrix();
    	    String[] letters = 
    			{"A", "B", "C", "D", "E", "F", "G", "H", "I",
    			"J", "K", "L", "M", "N", "O", "P", "Q", "R",
    			"S", "T", "U", "V", "W", "X", "Y", "Z"};
			
			
        	Matrix mat = new Matrix(inp);
        	if(count==5){
				index++;
				count = 0;
        	}
        	System.out.println("Draw " + letters[index]);
        	mat.matrixCompression(index);
			count++;
			
			System.out.println(count);
        	int comp[][] = mat.getComp();
        	
        	for(int i = 0;i<Constants.com_height;i++){
        		for(int j=0;j<Constants.com_width;j++)
        			System.out.print(comp[i][j]);
        		System.out.println();
        	}
        	testSketch(mat.getNeuralNetworkInput());
	    	currentSketch = null;
	    	//crosswordPanel.repaint();
		} 
    }
    
    private void testSketch(ArrayList<Double> input) {
            for (int k = 0; k < neuralNetwork.getLayers().size(); ++k) {
                for (Neuron neuron : neuralNetwork.getLayers().get(k)) {
                    double sum = 0.0;
                    if (k == 0) {
                        sum = Utilities.getSumFromInputs(neuron, input);
                    } else {
                        sum = Utilities.getSumFromPrev(neuron);
                    }
                    Utilities.activate(neuron, sum);
                }
            }
        int outputLayer = neuralNetwork.getLayers().size() - 1;
        double maxValue = -100.0;
        double maxNeuron = 0.0;
        for (int k = 0; k < neuralNetwork.getLayers().get(outputLayer).size(); ++k) {
            double currentValue = neuralNetwork.getLayers().get(outputLayer).get(k).activationValue;
            if (currentValue > maxValue) {
                maxValue = currentValue;
                maxNeuron = (double) k;
            }
        }
        String text = "";
        Set<Entry<String, Double>> entrySet = neuralNetwork.target.entrySet();
        for (Entry e : entrySet) {
            if(((double)e.getValue()) == maxNeuron) {
            	int val = Integer.parseInt(e.getKey().toString()) + 65;
            	text = Character.toString((char)val);
            	setTextInLabel(text, currentSketch.getRow(), currentSketch.getColumn());
            	System.out.println(text);
            	break;
            }
        }
    }
    
    private void setTextInLabel(String text, int row, int column) {
    		String finalText = "<html>";
    		String currentText = currentField.getText();
    	
    		if(currentText.indexOf("html") != -1) {
    			currentText= currentText.replaceAll("\\D+","");
    		}
    		
    		String htmlText = "<p align='left'>" + currentText + "</p><br/>";
    		
    		int unique = ((row + column) * (row + column + 1))/ 2 + column;
    		String currentBoxSolution = puzzle.getSolution().get(unique);
    		String color = "Green";
    		if(!currentBoxSolution.equals(text)) {
    			color = "Red";
    			wrongFields.add(this.currentField);
    		}
    		if(currentText.equals("")) {
    			this.currentField.shouldSetText = true;
    			htmlText = htmlText + "<h1 style ='padding-bottom: 5px; padding-left:20px; color:" +color+ "'>" + text + "</h1></html>";
    		} else {
    			this.currentField.shouldSetText = false;
    			htmlText += "<h1 style ='padding-left:20px; color:" + color+ "'>" + text + "</h1></html>";
    		}
    		finalText += htmlText;
    		System.out.println(finalText);
    		this.currentField.setText(finalText);
    }
        
    public void setPuzzle(Puzzle puzzleNew) {
    	this.puzzle = puzzleNew;
    }
    
    public CrosswordPanel getCrosswordPanel() {
    	return crosswordPanel;
    }
   
    public RulesPanel getRulesPanel() {
    	return rulesPanel;
    }
}
