package learner;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import Utilities.Constants;

public class Utilities {
	public static String splitter = " ";
	public static  String missingIdentifier = "?";
	
	public static void addConnections(Neuron neuron, ArrayList<Neuron> neurons) {
        for(Neuron prevLayerNeuron : neurons) {
            prevLayerNeuron.outputNeuron.add(neuron);
            neuron.inputNeuron.add(prevLayerNeuron);
        }
    }
	
	public static double getSumFromPrev(Neuron neuron) {
		double sum = 0.0;
	    for(int i = 0; i < neuron.weights.size(); ++i){
	    	if(i == 0) {
	    		sum += (1.0 * neuron.weights.get(i));
	        } else {
	            sum += (neuron.inputNeuron.get(i-1).activationValue * neuron.weights.get(i));
	        }
	     }
	     return sum;
	}
	   
	public static void activate(Neuron neuron, double sum) {
		double num = 1.0;
		double denom = 1.0 + Math.exp(-sum);
		neuron.activationValue = (num / denom);
	}
	
    public static void updateWeightsInner(Neuron neuron, double eta) {
        for(int i = 0; i < neuron.weights.size(); ++i) {
            double deltaVal = 0.0;
            if(i == 0) {
                deltaVal = eta * neuron.delta * 1.0;
            } else {
                deltaVal = eta * neuron.delta * neuron.inputNeuron.get(i-1).activationValue;
            }
            double newWeight = neuron.weights.get(i) + deltaVal;
            neuron.weights.set(i, newWeight);
        }
    }
    
    public static double getSumFromInputs(Neuron neuron, ArrayList<Double> inputs) {
        double sum = 0.0;
        for(int i = 0; i < neuron.weights.size(); ++i) {
            if(i == 0) {
                sum += (1.0 * neuron.weights.get(i));
            } else {
                sum += (inputs.get(i-1) * neuron.weights.get(i));
            }
        }
        return sum;
    }
   
    public static void updateWeights(Neuron neuron, ArrayList<Double> inputs, double eta) {
        for(int i = 0; i < neuron.weights.size(); ++i) {
            double deltaVal = 0.0;
            if(i == 0) {
                deltaVal = eta * neuron.delta * 1.0;
            } else {
                deltaVal = eta * neuron.delta * inputs.get(i-1);
            }
            double newWeight = neuron.weights.get(i) + deltaVal;
            neuron.weights.set(i, newWeight);
        }
    }   
    

    public static ArrayList<Attribute> constructAttributes(){
        ArrayList<Attribute> answer = new ArrayList<Attribute>();
        for(int i = 0 ; i < Constants.numAttributes; ++i) {
        	Attribute attr = new Attribute(i, "attribute", true);
        	answer.add(attr);
        }
        return answer;
    }
    
    public static void randomizeData(ArrayList<String> fileDataRow) {
        int size = fileDataRow.size();
        Random rand = new Random();
        for(int i = 0; i < size; ++i){
            int index1 = rand.nextInt(size);
            int index2 = rand.nextInt(size);

            String temp = fileDataRow.get(index1);
            fileDataRow.set(index1, fileDataRow.get(index2));
            fileDataRow.set(index2, temp);
        }
    }
    
    public static Data preProcessData(ArrayList<String> data, Attribute targetAttribute, ArrayList<Attribute> attributes){

        Data rawdata = new Data();
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        ArrayList<ArrayList<String>> answer = new ArrayList<ArrayList<String>>();

        for(String row : data){
            String [] attributeValues = row.split(splitter);
            ArrayList<String> currentRow = new ArrayList<String>();

            for(String currentAttribute : attributeValues){
                currentRow.add(currentAttribute);
            }

            answer.add(currentRow);

            if(!map.containsKey(currentRow.get(targetAttribute.getColumn()))) {
                map.put(currentRow.get(targetAttribute.getColumn()), 1);
            } else {
                map.put(currentRow.get(targetAttribute.getColumn()), map.get(currentRow.get(targetAttribute.getColumn())) + 1);
            }

            updateAttributeValues(attributes, currentRow);
        }

        for(Attribute attr : attributes) {
            if(attr.isDiscrete()) {
                attr.k = attr.getValues().size();
            }
        }
        rawdata.distribution = map;
        rawdata.rawData = answer;

        return rawdata;
    }
    
    public static void addInputNodes(Data data, ArrayList<Attribute> attributes, Replacement replacement) {
        
        ArrayList<ArrayList<Double>> input = new ArrayList<ArrayList<Double>>();
        for(ArrayList<String> row : data.rawData) {
            ArrayList<Double> currentRow = new ArrayList<Double>();
            for(int i = 0; i < (attributes.size() -1); ++i) {
                if(attributes.get(i).isDiscrete()) {
                    if(replacement == Replacement.LOG) {
                    	currentRow.add(Double.parseDouble(row.get(i)));
                    }
                } else  {
                    int column = attributes.get(i).getColumn();
                    double max = attributes.get(i).maxValue;
                    double min = attributes.get(i).minValue;
                    double val = Double.parseDouble(row.get(column));
                    val = (val - min) / (max-min);
                    currentRow.add(val);
                }
            }
            input.add(currentRow);
        }
        data.inputVectors = input;
    }
    
    private static ArrayList<Double> getContinuousLogarithmic(Attribute attr, String val) {
        ArrayList<Double> answer = new ArrayList<Double>();

        double numBits = Math.ceil((Math.log(attr.k) / Math.log(2.0)));

        for(double i = 0 ; i < numBits; ++i) {
            answer.add(0.0);
        }

        int counter = 0;

        for(String str : attr.getValues()) {
            if(val.equals(str)) {
                break;
            }
            ++counter;
        }


        int rightIndex  = (int)numBits - 1;

        while(counter  != 0) {
            if(counter % 2 != 0) {
                answer.set(rightIndex, 1.0);
            }
            --rightIndex;
            counter = counter/2;
        }

        return answer;
    }
    
    private static void updateMinMaxValue(Attribute attribute, Data data) {
        double max = Double.MIN_VALUE;
        double min = Double.MAX_VALUE;

        for (ArrayList<String> row : data.rawData) {
            double value = Double.parseDouble(row.get(attribute.getColumn()));
            if (value > max) {
                max = value;
            }
            if(value < min) {
                min = value;
            }
        }
        attribute.maxValue = max;
        attribute.minValue = min;
    }
    private static void updateAttributeValues(ArrayList<Attribute> attributes, ArrayList<String> row){
        for(Attribute attr : attributes) {
            if(!row.get(attr.getColumn()).equals(missingIdentifier)) {
                attr.addValue(row.get(attr.getColumn()));
            }
        }
    }
    
}
