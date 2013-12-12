import java.util.Random;

public class Neuron
{
	protected double[] weights;
	
	public Neuron (int inputs, Random random)
	{
		weights = new double[inputs + 1];
		if (random != null)
			for (int i = 0; i < weights.length; i++)
				weights[i] = random.nextDouble() - random.nextDouble();				
	}
	
}