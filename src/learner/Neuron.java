package learner;

import java.util.ArrayList;
import java.util.Random;


public class Neuron {

    public ArrayList<Neuron> inputNeuron;

    public ArrayList<Neuron> outputNeuron;

    public ArrayList<Double> weights;

    public double activationValue = 0.0;

    public double delta = 0.0;

    public NeuronType neuronType;

    public double desiredOutput = 0.0;

    public Neuron(NeuronType neuronType) {
        inputNeuron = new ArrayList<Neuron>();
        outputNeuron = new ArrayList<Neuron>();
        weights = new ArrayList<Double>();
        activationValue = 0.0;
        delta = 0.0;
        desiredOutput = 0.0;
        this.neuronType = neuronType;
    }

    public void assignRandomWeights(int n) {
        Random rand = new Random();
        double rangeMinimum = -0.05;
        double rangeMaximum = 0.05;
        for(int i = 0; i < n; ++i) {
            double randomNumber = rangeMinimum  + (rangeMaximum-rangeMinimum) * rand.nextDouble();
            weights.add(randomNumber);
        }
    }
}
