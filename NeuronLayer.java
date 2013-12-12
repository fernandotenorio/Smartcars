import java.util.Random;

public class NeuronLayer
{
	protected Neuron[] neurons;
	
	public NeuronLayer(int nNeurons, int inpusPerNeuron, Random random)
	{
		neurons = new Neuron[nNeurons];
		for (int i = 0; i < neurons.length; i++)
			neurons[i] = new Neuron(inpusPerNeuron, random);
	}
}