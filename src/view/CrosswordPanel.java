package view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.sun.xml.internal.ws.util.StringUtils;

import Utilities.Constants;
import controller.CrosswordController;
import models.Sketch;
import models.Stroke;
import puzzle.Puzzle;

public class CrosswordPanel extends JPanel implements Observer{
	
	public class SubPanel extends JPanel{
		private List<Sketch> sketches;
		private Stroke ongoingStroke;
		private int xOffset;
		private int yOffset;
		private boolean isBlack;
		
		public SubPanel(int xOff, int yOff, boolean isBlack){
			super();
			sketches = new ArrayList<Sketch>();
			xOffset = xOff;
			yOffset = yOff;
			this.isBlack = isBlack;
		}
		
		public SubPanel(GridLayout grid, int xOff, int yOff, boolean isBlack){
			super(grid);
			sketches = new ArrayList<Sketch>();
			xOffset = xOff;
			yOffset = yOff;
			this.isBlack = isBlack;
		}
		
		public int getXOffset(){
			return xOffset;
		}
		
		public int getYOffset(){
			return yOffset;
		}
		
		public List<Sketch> getSketches() {
	        return sketches;
	    }

	    public void addSketch(Sketch skt) {
	        sketches.add(skt);
	    }

	    public void resetPoints() {
	    	ongoingStroke = null;
	    }
	    
	    public void setOngoingStroke(Stroke s){
	    	this.ongoingStroke = s;
	    }
	    
	    @Override
	    public void paintComponent(Graphics g) {
	    	if(!isBlack) {
	    		g.setColor(Color.WHITE);
	    	    
	    	} else {
	    		g.setColor(Color.BLACK);
	    	}
	        
	        g.fillRect(0, 0, this.getWidth(), this.getHeight());
	        
	        g.setColor(Color.BLACK);
	        
	        for (int i = 0; i < sketches.size(); i++) {
				for (int j = 0; j < sketches.get(i).getStrokes().size(); j++) {
					Stroke stroke = sketches.get(i).getStrokes().get(j);
					for (int k = 1; k < stroke.getPoints().size(); k++) {
			            Point p1 = stroke.getPoints().get(k - 1);
			            Point p2 = stroke.getPoints().get(k);
			            g.drawLine(p1.x, p1.y, p2.x, p2.y);
			        }
				}
			}
	        
	        //draw ongoing stroke
	        if(ongoingStroke != null){
		        for (int k = 1; k < ongoingStroke.getPoints().size(); k++) {
		            Point p1 = ongoingStroke.getPoints().get(k - 1);
		            Point p2 = ongoingStroke.getPoints().get(k);
		            g.drawLine(p1.x, p1.y, p2.x, p2.y);
		        }
	        }
	    }
	}
	
	private Field[][] fields;
	private SubPanel[][] panels;
	
	public CrosswordPanel(Puzzle puzzle) {
		super(new GridLayout(Constants.gridHeight, Constants.gridWidth));
		panels = new SubPanel[Constants.gridHeight][Constants.gridWidth];
		for (int y = 0; y < Constants.gridHeight; y++) {
            for (int x = 0; x < Constants.gridWidth; x++) {
            	int unique = ((y+x) *(y + x + 1))/2 + x;
            	String currentState = puzzle.getPuzzle().get(unique);
            	//String currentState = puzzle.find_field(y, x);
                panels[y][x] = new SubPanel(new GridLayout(Constants.subpanelHeight, Constants.subpanelWidth), x, y, (currentState.equals("B")));
                panels[y][x].setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
                add(panels[y][x]);
            }
        }
		
		fields = new Field[Constants.gridHeight][Constants.gridWidth];
        for (int y = 0; y < Constants.gridHeight; y++) {
            for (int x = 0; x < Constants.gridWidth; x++) {
            	int unique = ((y+x) *(y + x + 1))/2 + x;
            	String currentState = puzzle.getPuzzle().get(unique);
            	//String currentState = puzzle.find_field(y, x);
            	
            	if(isInteger(currentState)) {
            		fields[y][x] = new Field(x, y, panels[y][x], true,currentState);
            	} else {
            		fields[y][x] = new Field(x, y, panels[y][x], false, "");
            	}
            	/*
            	if(currentState!=null) {
            		fields[y][x] = new Field(x, y, panels[y][x], true,currentState);
            	} else {
            		fields[y][x] = new Field(x, y, panels[y][x], false, "");
            	}*/
                panels[y][x].add(fields[y][x]);
            }
        }
		
	}
	
	public void startNewGame(Puzzle newPuzzle) {
		for (int y = 0; y < Constants.gridHeight; y++) {
            for (int x = 0; x < Constants.gridWidth; x++) {
            	int unique = ((y+x) *(y + x + 1))/2 + x;
            	String currentState = newPuzzle.getPuzzle().get(unique);
            	//String currentState = newPuzzle.getPuzzleCell(x,y);
            	//String currentState = newPuzzle.find_field(y, x);
            	panels[y][x].setBackground(Color.WHITE);
            	if(currentState.equals("B")) {
            		panels[y][x].setBackground(Color.BLACK);
            	}
            	/*if(currentState==null) {
            		panels[y][x].setBackground(Color.BLACK);
            	}*/
            }
		}
		
		for (int y = 0; y < Constants.gridHeight; y++) {
            for (int x = 0; x < Constants.gridWidth; x++) {
            	int unique = ((y+x) *(y + x + 1))/2 + x;
            	//String currentState = newPuzzle.getPuzzle().get(unique);
            	//String currentState = newPuzzle.getSolutionCell(x,y);
            	String currentState = newPuzzle.find_field(y, x);
            	/*
            	if(isInteger(currentState)) {
            		fields[y][x].setVerticalAlignment(SwingConstants.TOP);
            		fields[y][x].setText(currentState);
            	} else {
            		fields[y][x].setText("");
            	}*/
            	if(currentState!=null) {
            		fields[y][x].setVerticalAlignment(SwingConstants.TOP);
            		fields[y][x].setText(currentState);
            	} else {
            		fields[y][x].setText("");
            	}
            }
		}
		
		
	}
	public static boolean isInteger(String s) {
	    return isInteger(s,10);
	}

	public static boolean isInteger(String s, int radix) {
	    if(s.isEmpty()) return false;
	    for(int i = 0; i < s.length(); i++) {
	        if(i == 0 && s.charAt(i) == '-') {
	            if(s.length() == 1) return false;
	            else continue;
	        }
	        if(Character.digit(s.charAt(i),radix) < 0) return false;
	    }
	    return true;
	}
	// TO DO
	public void update(Observable o, Object arg) {
	
	}
	
	public void setController(CrosswordController controller) {
        for (int y = 0; y < Constants.gridHeight; y++) {
            for (int x = 0; x < Constants.gridWidth; x++){
               panels[y][x].addMouseListener(controller);
               panels[y][x].addMouseMotionListener(controller);
            }
        }
    }
}
