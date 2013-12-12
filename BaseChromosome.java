
import java.util.Random;
import java.util.ArrayList;

public abstract class BaseChromosome<F, G> implements Comparable<BaseChromosome>
{
	protected F[] fenotype;
	protected G[] genotype;
	protected float fitness;
	
	public int compareTo(BaseChromosome other)
	{						
		if (this.fitness > other.fitness)
				return 1;
		else if (this.fitness < other.fitness)
			return -1;
			
		return 0;
	}
	
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("fenotype:\n");
		sb.append("[");
		for (int i = 0; i < fenotype.length; i++)
		{
			if (i != fenotype.length - 1)
				sb.append(fenotype[i] + ", ");
			else
				sb.append(fenotype[i]);
			
		}
		sb.append("]");
		
		return sb.toString();
	}
	
	protected abstract ArrayList<BaseChromosome<F, G>> uniformCrossover(BaseChromosome<F, G> other, Random random, float cr);
	protected abstract ArrayList<BaseChromosome<F, G>> twoPointCrossover(BaseChromosome<F, G> other, Random random, float cr);
	protected abstract ArrayList<BaseChromosome<F, G>> onePointCrossover(BaseChromosome<F, G> other, Random random, float cr);
	protected abstract ArrayList<BaseChromosome<F, G>> blendCrossover(BaseChromosome<F, G> other, Random random, float cr);
	 
	protected abstract void mutateGauss(Random random, float mr, float desvpad); 
	protected abstract void mutate(Random random, float mr);
	protected abstract BaseChromosome<F, G> duplicate();
	protected abstract void decode();
	protected abstract void randomizeGenotype(Random random);
	
}