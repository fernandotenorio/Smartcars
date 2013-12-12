import java.util.Random;
import java.util.ArrayList;

public class RealChromosome extends BaseChromosome<Double, Double>
{
	protected Double[] minVals;
	protected Double[] maxVals;
	
	public RealChromosome(Double[] minVals, Double[] maxVals)
	{
		this.fenotype = new Double[minVals.length];
		this.minVals = minVals;
		this.maxVals = maxVals;
	}
	
	protected void randomizeGenotype(Random random)
	{
		for (int i = 0; i < fenotype.length; i++)								
			fenotype[i] = minVals[i] + random.nextDouble()*(maxVals[i] - minVals[i]);		
	}
	
	protected RealChromosome duplicate()
	{				
		Double[] max = new Double[this.fenotype.length];
		Double[] min = new Double[this.fenotype.length];
		
		for (int i = 0; i < this.fenotype.length; i++)
		{
			max[i] = this.maxVals[i];
			min[i] = this.minVals[i];
		}
		
		RealChromosome realClone = new RealChromosome(min, max);
		for (int i = 0; i < this.fenotype.length; i++)
			realClone.fenotype[i]  = this.fenotype[i];								
		
		return realClone;
	}
	
	protected void mutateGauss(Random random, float mr, float desvpad)
	{
		double noise = 0.0;
		for (int i = 0; i < fenotype.length; i++)
		{			
			if (random.nextFloat() < mr)
			{
				do 
				{
					noise = random.nextGaussian() * desvpad;
					
				} while( ! ((minVals[i] <= fenotype[i] + noise) && (fenotype[i] + noise <= maxVals[i])) );
				fenotype[i] += noise; 
			}
		}
	}
	
	protected void mutate(Random random, float mr)
	{		
		this.mutateGauss(random, mr, 0.1f);
	}
		
	protected ArrayList<BaseChromosome<Double, Double>> blendCrossover(BaseChromosome<Double, Double> other, Random random, float cr)
	{
		BaseChromosome<Double, Double> copyA = this.duplicate();
		BaseChromosome<Double, Double>copyB = other.duplicate();
		ArrayList<BaseChromosome<Double, Double>> childs = new ArrayList<BaseChromosome<Double, Double>>(2);
		childs.add(copyA); 
		childs.add(copyB); 
		
		if (random.nextFloat() < cr)
		{
			int index = random.nextInt(fenotype.length);
			for (int i = index; i < fenotype.length; i++)
			{				
				double beta = 0.5;
				Double var1 = fenotype[i];
				Double var2 = other.fenotype[i];
				
				Double newVar1 = beta * var1 + (1 - beta) * var2;
				Double newVar2 = (1 - beta) * var1 + beta * var2;
				
				copyA.fenotype[i] = newVar1;
				copyB.fenotype[i] = newVar2;
			}
		}
		
		return childs;
	}
	
	protected ArrayList<BaseChromosome<Double, Double>> onePointCrossover(BaseChromosome<Double, Double> other, Random random, float cr)
	{
		return null;
	}
	
	protected ArrayList<BaseChromosome<Double, Double>> twoPointCrossover(BaseChromosome<Double, Double> other, Random random, float cr)
	{
		return null;
	}
	
	protected ArrayList<BaseChromosome<Double, Double>> uniformCrossover(BaseChromosome<Double, Double> other, Random random, float cr)
	{
		return null;
	}
		
	protected void decode(){}
	
}