package learner;

import java.util.ArrayList;
import java.util.HashMap;

public class Data {
    public HashMap<String, HashMap<String, Integer>> discreteAttributeDistribution;

    public HashMap<String, ArrayList<Integer>> continuousAttributesDistribution;

    public ArrayList<ArrayList<String>> rawData;

    public HashMap<String, Integer> distribution;

    public ArrayList<ArrayList<Double>> inputVectors;

    public Data() {
        rawData = new ArrayList<ArrayList<String>>();
        distribution = new HashMap<String, Integer>();
        inputVectors = new ArrayList<ArrayList<Double>>();
    }
}

