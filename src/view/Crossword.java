package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.Random;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;

import controller.ButtonController;
import controller.CrosswordController;
import models.Grid;
import puzzle.Puzzle;

public class Crossword extends JFrame{
	public Crossword() {
		super("Crossword");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());
        Grid grid = new Grid(); // empty 
        Random r = new Random();
        int Low = 1;
        int High = 4;
        int Result = r.nextInt(High-Low) + Low;
        
        Puzzle puzzle = new Puzzle();
        puzzle.buildGame();
        
        
        JPanel subButtonPanel = new JPanel();
        subButtonPanel.setLayout(new GridLayout(1,0,0,0));
        RulesPanel rulesPanel = new RulesPanel(puzzle.getRules());
        rulesPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
        subButtonPanel.add(rulesPanel);
        
        ButtonController buttonController = new ButtonController(grid);
        ButtonPanel buttonPanel = new ButtonPanel();
        buttonPanel.setController(buttonController);
        subButtonPanel.add(buttonPanel);
        add(subButtonPanel, BorderLayout.EAST);
        
       
        CrosswordPanel panel = new CrosswordPanel(puzzle);
        CrosswordController controller = new CrosswordController(panel, grid, puzzle, rulesPanel, Result);
        //CrosswordPanel panel = new CrosswordPanel();
        //CrosswordController controller = new CrosswordController(panel, grid);
        panel.setController(controller);
        add(panel, BorderLayout.CENTER);
        buttonController.setController(controller);
        grid.addObserver(panel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
      
        //controller.trainNeuralNetwork();
	}
		
	public static void main(String[] args) {
        try { 
        	
        	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 
        }
        catch (Exception ex) { 
        	ex.printStackTrace(); 
        }
        new Crossword();
    }
}
