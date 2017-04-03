package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import Utilities.*;
import view.CrosswordPanel.SubPanel;
public class Field extends JLabel {
	
	public static class LetterSet {
		private static final String[] alphabets = 
				{"A", "B", "C", "D", "E", "F", "G", "H", "I",
				"J", "K", "L", "M", "N", "O", "P", "Q", "R",
				"S", "T", "U", "V", "W", "X", "Y", "Z"};
	}
	
	private int x;      // X position in game.
    private int y;      // Y position in game.
    private SubPanel parentPanel;
    public boolean shouldSetText = false;
    public int row;
    public int col;
    public String text;
    
    public Field(int x, int y, SubPanel parent, boolean shouldSetText, String text) {
        super("", LEFT);
        this.x = x;
        this.y = y;
        this.parentPanel = parent;
        setPreferredSize(new Dimension(Constants.width,Constants.height));
        setBorder(BorderFactory.createLineBorder(Color.GRAY));
        setFont(new Font(Font.DIALOG, Font.PLAIN, 10));
        if(shouldSetText) {
        	setVerticalAlignment(TOP);
        	setText(text);
        }
        setOpaque(false);
    }
    /*
    public Field(int row, int col, String text) {
        super("", LEFT);
        this.row = x;
        this.col = y;
        this.text = text;
    }*/

    public int getFieldX() {
        return x;
    }

    public int getFieldY() {
        return y;
    }
    
    public SubPanel getParentPanel() {
    	return parentPanel;
    }
    
}