import java.util.ArrayList;
import java.util.Random;

public class NeuralNet
{
	protected final int nInputs;
	protected final int nOutputs;
	protected final int nHiddenLayers;
	protected final int neuronsPerHiddenLayer;
	
	protected ArrayList<NeuronLayer> layers;
	private ArrayList<Double> inputs = new ArrayList<Double>();
	private ArrayList<Double> outputs = new ArrayList<Double>();
	
	static final int BIAS = -1;
	static final int ACTIVATION = 1;
	
	public NeuralNet(int nInputs, int nOutputs, int nHiddenLayers, int neuronsPerHiddenLayer, Random random)
	{
		this.nInputs = nInputs;
		this.nOutputs = nOutputs;
		this.nHiddenLayers = nHiddenLayers;
		this.neuronsPerHiddenLayer = neuronsPerHiddenLayer;
		
		buildNet(random);
	}
	
	public void buildNet(Random random)
	{
		layers = new ArrayList<NeuronLayer>();
		
		if (nHiddenLayers > 0)
		{
			layers.add(new NeuronLayer(neuronsPerHiddenLayer, nInputs, random));
		
			for (int i = 0; i < nHiddenLayers - 1; i++)
				layers.add(new NeuronLayer(neuronsPerHiddenLayer, neuronsPerHiddenLayer, random));
				
			layers.add(new NeuronLayer(nOutputs, neuronsPerHiddenLayer, random));
		}
		else
			layers.add(new NeuronLayer(nOutputs, nInputs, random));
		
	}
	
	public ArrayList<Double> getWeights()
	{
		ArrayList<Double> ws = new ArrayList<Double>();
		
		for (NeuronLayer layer : layers)
		{
			for (Neuron neuron : layer.neurons)
			{
				for (Double w : neuron.weights)
				{
					ws.add(w);
				}
			}
		}
		return ws;
	}
	
	public void putWeights(Double[] ws)
	{
		int c = 0;
		for (NeuronLayer layer : layers)
		{
			for (Neuron neuron : layer.neurons)
			{
				for (int i = 0; i < neuron.weights.length; i++)
				{
					neuron.weights[i] = ws[c++];
				}
			}
		}
	}
	
	public ArrayList<Double> update (double[] input)
	{
		addToInput(input);
		outputs.clear();
		
		if (input.length != nInputs)
			throw new IllegalStateException("Error: wrong number of inputs.");
			
		int c = 0;
		for (int i = 0; i < nHiddenLayers + 1; i++)
		{
			if (i > 0)
			{
				ArrayList<Double> old = inputs;
				inputs = outputs;
				outputs = old;
				outputs.clear();
			}
			
			c = 0;
			NeuronLayer layer = layers.get(i);
			
			for (int j = 0; j < layer.neurons.length; j++)
			{
				double netInput = 0.0;
				Neuron neuron = layer.neurons[j];
				
				for (int k = 0; k < neuron.weights.length - 1; k++)				
					netInput += neuron.weights[k] * inputs.get(c++);
				
				netInput += neuron.weights[neuron.weights.length - 1] * BIAS;
				outputs.add(sigmoid(netInput, ACTIVATION));
				
				c = 0;
			}
		}
		return outputs;
	}
	
	private double sigmoid(double netinput, double response) 
	{
		return ( 1.0 / ( 1.0 + Math.exp(-netinput / response)));
	}
	
	private void addToInput(double[] input)
	{
		inputs.clear();
		for (Double val : input)
			inputs.add(val);
	}
	
	private static double clamp(Random random)
	{
		return random.nextDouble() - random.nextDouble();		
	}
	
	
}	//fim da classe