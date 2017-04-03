package view;

import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

public class RulesPanel extends JPanel {
	
	public RulesPanel(ArrayList<String> rules) {
		JLabel label = new JLabel();
		String text = "<html><ul>";
		for(String rule : rules) {
			text += ("<li style='font-size:11px;'>" + rule);
			text += ("</li>");
		}
		text += ("</ul></html>");
		label.setText(text);
		add(label);
	}
	
	public void setTextNew(ArrayList<String> rules) {
		JLabel label = new JLabel();
		String text = "<html><ul>";
		for(String rule : rules) {
			text += ("<li style='font-size:11px;'>" + rule);
			text += ("</li>");
		}
		text += ("</ul></html>");
		label.setText(text);
		this.removeAll();
		this.add(label);
	}
}
