package learner;

import java.util.ArrayList;
import java.util.HashMap;

public class NeuralNetwork {

    private ArrayList<ArrayList<Neuron>> _layers;

    public int maxEpoch = 30000;

    public HashMap<String, Double> target;

    public double minError = Double.MAX_VALUE;

    public int doubleIterationLimit = 2000;

    public int lowerLimit = 200;

    public int iterationsGlobal = 0;
    public int maxIterationLimit = 2000;
    ArrayList<ArrayList<Neuron>> bestNeuronsGlobal;

    public NeuralNetwork(ArrayList<Integer> hiddenLayers, int outputNeurons, int numInputNodes, Attribute targetAttr) {
        _layers = new ArrayList<ArrayList<Neuron>>();

        ArrayList<Neuron> prev = new ArrayList<Neuron>();
        ArrayList<Neuron> current = new ArrayList<Neuron>();
        // generate the first hidden layer
        if(hiddenLayers.size() != 0) {
            for(int i = 0; i < hiddenLayers.get(0); ++i) {
                Neuron newNeuron = new Neuron(NeuronType.HIDDEN);
                newNeuron.assignRandomWeights(numInputNodes+1);
                prev.add(newNeuron);
            }
            _layers.add(prev);
        }

        for(int i = 1; i < hiddenLayers.size(); ++i) {
            for(int j = 0; j < hiddenLayers.get(i); ++j) {
                Neuron newNeuron = new Neuron(NeuronType.HIDDEN);
                newNeuron.assignRandomWeights(prev.size()+1);
                Utilities.addConnections(newNeuron, prev);
                current.add(newNeuron);
            }
            prev = new ArrayList<Neuron>(current);
            _layers.add(prev);
            current.clear();
        }

        for(int i = 0; i < outputNeurons; ++i) {
            Neuron newNeuron = new Neuron(NeuronType.OUTPUT);
            if(prev.size() != 0) {
                newNeuron.assignRandomWeights(prev.size() +1);
                Utilities.addConnections(newNeuron, prev);

            } else {
                newNeuron.assignRandomWeights(numInputNodes+1);
            }
            current.add(newNeuron);
        }
        _layers.add(current);

        target = new HashMap<String, Double>();

        double counter = 0.0 ;

        for(String str : targetAttr.getValues()) {
            target.put(str, counter);
            counter++;
        }
    }

    public ArrayList<ArrayList<Neuron>> getLayers() {
        return _layers;
    }

    public void train(Data data, Attribute targetAttr, double eta, Data validationData) {
        int iterations = 0 ;
        minError  = Double.MAX_VALUE;
        for (int i = 0;  i < maxEpoch; ++i) {
            double totalError = 0.0;
            for(int j = 0; j < data.inputVectors.size(); ++j) {
                // forward
                for(int k = 0; k <_layers.size(); ++k) {
                    for(Neuron neuron : _layers.get(k)) {
                        double sum = 0.0;
                        if(k == 0) {
                         sum = Utilities.getSumFromInputs(neuron, data.inputVectors.get(j));
                        } else {
                         sum = Utilities.getSumFromPrev(neuron);
                        }
                        Utilities.activate(neuron, sum);
                    }
                }

                //start back propagation
                int outputLayer = _layers.size()-1;
                for(int k = 0; k < _layers.get(outputLayer).size(); ++k) {
                    double currentValue = target.get(data.rawData.get(j).get(targetAttr.getColumn()));
                    double desiredValue = 0.0;
                    if(((int)currentValue) == k) {
                        desiredValue = 1.0;
                    } else {
                        desiredValue = 0.0;
                    }

                    double error = desiredValue - _layers.get(outputLayer).get(k).activationValue;
                    totalError += Math.abs(error);
                    double gradient = (_layers.get(outputLayer).get(k).activationValue) * (1 - _layers.get(outputLayer).get(k).activationValue);
                    _layers.get(outputLayer).get(k).delta = gradient * error;
                }

                // calculate the delta for the other layers
                for(int k = outputLayer-1; k >=0; --k) {
                    for(int l = 0; l < _layers.get(k).size(); ++l) {
                        double gradient = (_layers.get(k).get(l).activationValue) * (1 - _layers.get(k).get(l).activationValue);
                        double sum = 0.0;
                        for(Neuron output : _layers.get(k).get(l).outputNeuron) {
                            sum += output.weights.get(l+1) * output.delta;
                        }
                        _layers.get(k).get(l).delta = gradient * sum;
                    }
                }

                // update the weights
                for(int k = 0; k <_layers.size(); ++k) {
                    for(Neuron neuron : _layers.get(k)) {
                        if(k == 0) {
                            Utilities.updateWeights(neuron, data.inputVectors.get(j), eta);
                        } else {
                            Utilities.updateWeightsInner(neuron, eta);
                        }
                    }
                }
            }
            double currentErrror = validate(validationData, targetAttr);


            NeuralNetwork bestNeural;
            if(currentErrror < minError) {
                minError = currentErrror;
               // bestNeural = new NeuralNetwork(this);
                if(i >= lowerLimit) {
                    doubleIterationLimit = 2 * i;
                }
                bestNeuralWeights();
            }

            if(i > doubleIterationLimit || i > maxIterationLimit) {
                break;
            }

            System.out.println("In iteration " + i);
            ++iterations;
        }
        iterationsGlobal = iterations;
        System.out.println("Minimum error is " + minError/validationData.inputVectors.size());
    }
    
    public Double validate(Data validationData,  Attribute targetAttr) {
        double totalError = 0.0;
        for(int j = 0; j < validationData.inputVectors.size(); ++j) {
            // forward
            double meanSquaredError = 0.0;
            for (int k = 0; k < _layers.size(); ++k) {
                for (Neuron neuron : _layers.get(k)) {
                    double sum = 0.0;
                    if (k == 0) {
                        sum = Utilities.getSumFromInputs(neuron, validationData.inputVectors.get(j));
                    } else {
                        sum = Utilities.getSumFromPrev(neuron);
                    }
                    Utilities.activate(neuron, sum);
                }
            }
            int outputLayer = _layers.size()-1;

            for(int k = 0; k < _layers.get(outputLayer).size(); ++k) {
                double currentValue = target.get(validationData.rawData.get(j).get(targetAttr.getColumn()));
                double desiredValue = 0.0;
                if(((int)currentValue) == k) {
                    desiredValue = 1.0;
                } else {
                    desiredValue = 0.0;
                }
                double error = desiredValue - _layers.get(outputLayer).get(k).activationValue;
                //totalError += Math.abs(error);
                meanSquaredError += (error*error);
            }
           totalError += (meanSquaredError/((double)_layers.get(outputLayer).size()));

        }
        System.out.println("Mean Square Error is " + totalError/validationData.inputVectors.size());
        return totalError;
    }

    public void bestNeuralWeights() {
        ArrayList<ArrayList<Neuron>> bestNeurons = new ArrayList<ArrayList<Neuron>>();

        for(int i = 0; i < _layers.size(); ++i) {
            ArrayList<Neuron> currentNeurons = new ArrayList<Neuron>();
            for(int j = 0; j < _layers.get(i).size(); ++j) {
                Neuron newNeuron = null;
                if(i == _layers.size()-1) {
                    newNeuron = new Neuron(NeuronType.OUTPUT);
                } else {
                    newNeuron = new Neuron(NeuronType.HIDDEN);
                }
                newNeuron.weights = new ArrayList<Double>(_layers.get(i).get(j).weights);
                currentNeurons.add(newNeuron);
            }
            bestNeurons.add(currentNeurons);
        }
        bestNeuronsGlobal = bestNeurons;
    }

    public void updateToBestWeights() {
        for(int i = 0 ; i < _layers.size(); ++i) {
            for(int j = 0; j < _layers.get(i).size(); ++j) {
                _layers.get(i).get(j).weights = new ArrayList<Double>(bestNeuronsGlobal.get(i).get(j).weights);
            }
        }
    }
}

