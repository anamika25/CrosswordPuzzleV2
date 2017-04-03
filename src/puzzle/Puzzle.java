package puzzle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Set;
import java.util.TreeSet;
import Utilities.Constants;
import learner.*;
import view.Field;

public class Puzzle {
	private HashMap<Integer, String> solution;
	private HashMap<Integer, String> initialState;
	
	private ArrayList<String> rules;
	public ArrayList<FieldNode> Fields;
	
	Dictionary<String, String> d = new Hashtable<String, String>();
	String[] words = {"Tusk", "Deal", "Servant", "Medicine", "Desire"};
	String[] newrules = {"हाथी के दांत", "मोल की हुई वस्तु, क्रय-विक्रय", "नौकर, सेवक", "दवाई, औषधि", "इच्छा, कामना"};
	Set<String> across = new TreeSet<String>();
	Set<String> down = new TreeSet<String>();
	
	public Puzzle() {
		solution = new HashMap<Integer, String>();
		initialState = new HashMap<Integer, String>(); 
		rules = new ArrayList<String>();
		Fields= new ArrayList<FieldNode>();
	}
	
	public void buildGame() {
		/*Constants.solutionPath = "solution_" + puzzleNumber +".txt";
		Constants.puzzleRulesPath = "Puzzle" + puzzleNumber + "_rules.txt";
		FileReaderUtility fd1 = new FileReaderUtility(Constants.solutionPath);
		FileReaderUtility fd2 = new FileReaderUtility(Constants.puzzlePath);
		FileReaderUtility fd3 = new FileReaderUtility(Constants.puzzleRulesPath);
		ArrayList<String> sol = fd1.ReadFile();
		ArrayList<String> puzzle = fd2.ReadFile();
		rules = fd3.ReadFile();*/
		compute_crossword();
		//print_field();
		for(int i = 0 ; i < Constants.gridHeight; ++i) {
			for(int j = 0 ; j < Constants.gridWidth; ++j) {
				int unique = ((i+j) * (i+j+1))/2 + j;
				String s = find_field(j,i);
				if(s!=null)
				{
					solution.put(unique, s);
					initialState.put(unique, "");
				}
				else
				{
					solution.put(unique, "B");
					initialState.put(unique, "B");
				}

			}
		}
	}
	
	public HashMap<Integer, String> getSolution() {
		return solution;
	}
	
	
	public HashMap<Integer, String> getPuzzle() {
		return initialState;
	}
	
	
	public ArrayList<String> getRules() {
		List<String> list = Arrays.asList(words);
		rules.add("Across:");
		for(String s : across)
		{
			int i= list.indexOf(s);
			rules.add(newrules[i]);
		}
		//add position of word with hints
		rules.add("Down:");
		for(String s : down)
		{
			int i= list.indexOf(s);
			rules.add(newrules[i]);
		}
		return rules;
	}
	/*
	public String getSolutionCell(int row, int col) {
		if(solution==null)
			return null;
		for(int i=0;i<solution.size();i++)
		{
			FieldNode f = solution.get(i);
			if(f.row == row && f.col==col)
				return f.text;

		}
		return null;
	}
	
	
	public String getPuzzleCell(int row, int col) {
		if(initialState==null)
			return null;
		for(int i=0;i<initialState.size();i++)
		{
			FieldNode f = initialState.get(i);
			if(f.row == row && f.col==col)
				return f.text;

		}
		return null;
	}*/
	
	public static float getTime() {
        return System.currentTimeMillis() * 1000;
    }
    
    public void compute_crossword()
    {
    	float time_permitted = (float)1.00;
    	float start_full = getTime();
    	while ((getTime() - start_full) < time_permitted)
    	{
    		//copy.randomize_word_list()
    		//String[] available_words={"Tusk","Deal","Servant","Medicine","Desire"};
    		ArrayList<String> current_word_list= new ArrayList<String>();
    		for (int i=0;i<words.length;i++)
    		{
    			if (!Arrays.asList(current_word_list).contains(words[i]))
                    fit_and_add(words[i],current_word_list);
    		}
                
    	}
    	return;
    }
    
    public void fit_and_add(String word, ArrayList<String> current_word_list)
    {
    	boolean fit = false;
    	int count = 0,col=0,row=0,vertical=0;
    	List<List<Integer>> coordlist = suggest_coord(word);
    	int maxloop= 2000;check if okay
		while( !fit && count < maxloop)
    	{
    		if (current_word_list.size() == 0) // this is the first word: the seed
    		{
    			// top left seed of longest word yields best results (maybe override)
    	        vertical= (int)Math.random() * 2;
    	        col=1;
    	        row=1;
    	        
    	        if(check_fit_score(col, row, vertical, word)==1)
                {
                	fit = true;
    	            set_word(col, row, vertical, word, true,current_word_list);
                }
    		}
    		
            else // a subsequent words have scores calculated
            {
            	try{
            		col = coordlist.get(count).get(0);
            		row = coordlist.get(count).get(1);
            		vertical = coordlist.get(count).get(2);
            	}
            	catch(Exception e){
            		e.printStackTrace();
            	} ; // no more coordinates, stop trying to fit  
            	
            	/*
                if(coordlist.get(count).get(3)!=0) // already filtered these out, but double check
                {
                	fit = true ;
    	            set_word(col, row, vertical, word, true,current_word_list);
                }*/
            }
    		count += 1;
    	}
    	            
		return;
    }
    
    public List<List<Integer>> suggest_coord(String word)
    {
    	//int[][] coordlist;
    	List<List<Integer>> coordlist = new ArrayList<List<Integer>>();
    	int count = 0,rowc=0,colc=0,row=0,cell=0;
    	int glc = -1;
    	for(int i=0;i<word.length();i++) // cycle through letters in word
    	{
    		glc += 1;
    	    rowc = 0;
    	    for (row=0;row< Constants.gridHeight;row++) // cycle through rows
    	    {
    	    	rowc += 1;
                colc = 0;
                for(cell=0;cell<Constants.gridWidth;cell++) // cycle through letters in rows
                {
                	colc += 1;
                	String s = Character.toString(word.charAt(i));
                	if(find_field(row,cell)==null)
                		return coordlist;
                    if(s.equals(find_field(row,cell))) // check match letter in word to letters in row
                    {
                    	try // suggest vertical placement 
                    	{
                    		if (rowc - glc > 0) // make sure we're not suggesting a starting point off the grid
                    		{
                    			if (((rowc - glc) + word.length()) <= Constants.gridHeight) // make sure word doesn't go off of grid
                    			{
                    				List<Integer> temp = new ArrayList<Integer>();
                    				temp.add(colc);
                    				temp.add(rowc - glc);
                    				temp.add(1);
                    				temp.add(colc + (rowc - glc));
                    				temp.add(0);
                    				coordlist.add(temp);
                    			}
                                    
                    		}
                                
                    	}
                    	catch(Exception e){
                            e.printStackTrace();
                    	}
                    	
                        try // suggest horizontal placement 
                        {
                        	if (colc - glc > 0) // make sure we're not suggesting a starting point off the grid
                        	{
                        		if (((colc - glc) + word.length()) <= Constants.gridWidth) // make sure word doesn't go off of grid
                        		{
                        			List<Integer> temp = new ArrayList<Integer>();
                    				temp.add(colc - glc);
                    				temp.add(rowc);
                    				temp.add(0);
                    				temp.add(rowc + (colc - glc));
                    				temp.add(0);
                    				coordlist.add(temp);
                        		}
                        	}
                            
                        }   
                        catch(Exception e){
                              e.printStackTrace();
                        } 
                    }
                                
                }
                    
    	    }
                
    	}
    	                          
    	List<List<Integer>> new_coordlist = sort_coordlist(coordlist, word);

    	return new_coordlist;
    }
    
    public List<List<Integer>> sort_coordlist(List<List<Integer>> coordlist, String word)
    {
    	//int[][] new_coordlist= new int[Constants.gridWidth][Constants.gridHeight];
    	List<List<Integer>> new_coordlist = new ArrayList<List<Integer>>(); 
    	for(List<Integer> coord: coordlist)
    	{
    		int col = coord.get(0);
    		int row = coord.get(1);
    		int vertical = coord.get(2);
    		coord.set(4, check_fit_score(col, row, vertical, word)); // checking scores
    	    if (coord.get(4)!=0) // 0 scores are filtered
    	       new_coordlist.add(coord);
    	}
        
        //random.shuffle(new_coordlist); // randomize coord list; why not?
        //new_coordlist.sort(i[4], reverse=True) // put the best scores first
    	return new_coordlist;
    }
    
    public String find_field(int row, int col)
    {
    	if(Fields.size()==0 || Fields==null)
    		return null;
    	FieldNode f = new FieldNode();
    	for(int i=0;i<Fields.size();i++)
		 {
			f = Fields.get(i);
			if(f.row == row && f.col == col)
       		return f.text;
		 }
    	return null;
    }
    
    public void print_field()
    {
    	FieldNode f = new FieldNode();
    	for(int i=0;i<Fields.size();i++)
		 {
			f = Fields.get(i);
			System.out.println("row: "+f.row+" col: "+f.col+" text: "+f.text);
		 }
    	return;
    }
    
    public void set_word(int col, int row, int vertical, String word, boolean flag, ArrayList<String> current_word_list)
    {
    	String color = "Green";
    	if(flag)
    	{
    	            //word.vertical = vertical
    				current_word_list.add(word);
    	    		for(int i=0;i<word.length();i++)
    	    		{
    	    			String finalText = "<html>";
        	    		char text = word.charAt(i);
        	    		//String currentText = currentField.getText();
        	    	
        	    		/*
        	    		if(currentText.indexOf("html") != -1) {
        	    			currentText= currentText.replaceAll("\\D+","");
        	    		}*/

    	    			//String htmlText = "<p align='left'>" + currentText + "</p><br/>";
        	    		String htmlText ="";
    	    			htmlText = htmlText + "<h1 style ='padding-bottom: 5px; padding-left:20px; color:" +color+ "'>" + text + "</h1></html>";
        	    		finalText += htmlText;
        	    		System.out.println(finalText);
        	    		
        	    		String s= Character.toString(text);
        	    		FieldNode f = new FieldNode();
        	    		f.setFieldNode(row,col,s);
        	    		Fields.add(f);
        	    		print_field();
        	    		
        	    		//this.currentField.setText(finalText);
        	    
        	    		if(vertical==1)
        	    		{
        	    			row += 1;
        	    			down.add(word);
        	    		}  
    	                else
    	                {
    	                	col += 1;
    	                	across.add(word);
    	                }
    	                    
    	    		}       
    	}
            
        return;
    }
   
    public int check_fit_score(int col, int row, int vertical, String word)
    {
    	int score=1,count=1; // give score a standard value of 1, will override with 0 if collisions detected
    	String active_cell= new String();
    	
    	if (col < 1 || row < 1)
            return 0;

        for (int letter=0;letter<word.length();letter++)  
        {
        	 try{
             	active_cell = find_field(row-1,col-1);
             } 
             catch(Exception e){
                 e.printStackTrace();
             }
        	 String s= Character.toString(word.charAt(letter));
             if (active_cell==null || active_cell.equals(s))
                 System.out.println("pass");
             else
                 return 0;
  
             if(s.equals(active_cell))//if(active_cell.equals(s))
                 score += 1;
  
             if (vertical!=0)
             {
             	// check surroundings
                 if (!active_cell.equals(s)) // don't check surroundings if cross point
                 {
                 	if(!check_if_cell_clear(col+1, row)) // check right cell
                     return 0;

                 	if(!check_if_cell_clear(col-1, row)) // check left cell
                     return 0;
                 }
                     
  
                 if(count == 1) // check top cell only on first letter
                 {
                 	if(!check_if_cell_clear(col, row-1))
                         return 0;
                 }
                     
                 if(count == word.length()) // check bottom cell only on last letter
                 {
                 	if(!check_if_cell_clear(col, row+1)) 
                         return 0;
                 }
             }
                       
             else // else horizontal
             {
             	// check surroundings
                 if(!s.equals(active_cell)) // don't check surroundings if cross point
                 {
                 	if(!check_if_cell_clear(col, row-1)) // check top cell
                     return 0;

                 if(!check_if_cell_clear(col, row+1)) // check bottom cell
                     return 0;
                 }
                     
                 if (count == 1) // check left cell only on first letter
                 {
                 	if(!check_if_cell_clear(col-1, row))
                         return 0;
                 }
                     
                 if(count == word.length()) // check right cell only on last letter
                 {
                 	if(!check_if_cell_clear(col+1, row))
                         return 0;
                 }
             }
             
             if (vertical!=0) // progress to next letter and position
                 row += 1;
             else  //else horizontal
                 col += 1;
  
             count += 1;
        }
           
    	return score;
    }
   
    public boolean check_if_cell_clear(int col, int row)
    {
    	try{
    		String cell= new String();
    		cell = find_field(row,col);
    	    if (cell==null) 
    	       return true;
    	}
            
    	catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
}
