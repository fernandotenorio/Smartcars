import java.util.*;

 enum CrossoverType
{
	CX_ONE_POINT, CX_TWO_POINT, CX_UNIF, CX_BLEND
}

enum MutationType
{
	MUT_FLIP, MUT_GAUSS
}

public class GeneticAlgorithm<F, G>
{
	protected int popSize;
	protected float mutationRate;
	protected float crossoverRate;
	protected int elite;
	protected ArrayList<BaseChromosome<F, G>> currentPopulation;
	protected BaseChromosome<F, G> best;
	protected FitnessDelegate<F> fitnessDelegate;
	protected Random random = new Random();
	protected CrossoverType cxType;
	protected MutationType mutationType;
	protected float gaussDesvPad = 0.05f;
	
	public GeneticAlgorithm(ArrayList<BaseChromosome<F, G>> currentPopulation, FitnessDelegate<F> fitnessDelegate, 
	float mutationRate, float crossoverRate, int elite, CrossoverType cxType, MutationType mutationType)
	{
		this.popSize = currentPopulation.size();
		this.currentPopulation = currentPopulation;
		this.fitnessDelegate = fitnessDelegate;
		this.mutationRate = mutationRate;
		this.crossoverRate = crossoverRate;
		this.elite = elite;
		this.cxType = cxType;
		this.mutationType = mutationType;
		
		/*
		if (currentPopulation != null)
			for (BaseChromosome<F, G> cr : currentPopulation)
				cr.randomizeGenotype(random);
				*/
	}
	
	public BaseChromosome<F, G> getBest()
	{
		return this.best;
	}
	
	private BaseChromosome<F, G> rouleteWheelSelection(ArrayList<BaseChromosome<F, G>> population)
	{
		BaseChromosome<F, G> sorted = null;
		
		float totalFitness = 0.0f;
		for (BaseChromosome<F, G> agent : population)
			totalFitness += agent.fitness;
		
		float frac = random.nextFloat();
		float cut = frac *  totalFitness;
		
		for (BaseChromosome<F, G> agent:population)
		{
			cut -= agent.fitness;
			if (cut <= 0.0)
			{
				sorted = agent;
				break;
			}
		}
		
		return sorted;
	}
	
	public void evolve()
	{
		for (BaseChromosome<F, G> chromosome:currentPopulation)
		{
			chromosome.decode();
			if (fitnessDelegate != null)
				chromosome.fitness = fitnessDelegate.getFitness(chromosome.fenotype);	
			
			if (best == null || chromosome.fitness > best.fitness)
				best = chromosome;								
		}
				
		ArrayList<BaseChromosome<F, G>> newPopulation = new ArrayList<BaseChromosome<F, G>>(popSize);
		
		if (elite > 0)		
			Collections.sort (currentPopulation);
							
		//elite
		for (int i = 0; i < elite; i++)
			newPopulation.add(currentPopulation.get(popSize - i - 1));
		
		for (int i = 0; i < (popSize - elite)/2; i++)
		{
			BaseChromosome<F, G> parentA = rouleteWheelSelection(currentPopulation);
			BaseChromosome<F, G> parentB = rouleteWheelSelection(currentPopulation);
			
			ArrayList<BaseChromosome<F, G>> children = null;
			if (cxType == CrossoverType.CX_ONE_POINT)
				children = parentA.onePointCrossover(parentB, random, this.crossoverRate);
			else if (cxType == CrossoverType.CX_TWO_POINT)
				children = parentA.twoPointCrossover(parentB, random, this.crossoverRate);
			else if (cxType == CrossoverType.CX_UNIF)
				children = parentA.uniformCrossover(parentB, random, this.crossoverRate);
			else if (cxType == CrossoverType.CX_BLEND)
				children = parentA.blendCrossover(parentB, random, this.crossoverRate);
			
			BaseChromosome<F, G> childA = children.get(0);
			BaseChromosome<F, G> childB = children.get(1);
			
			if (mutationType == MutationType.MUT_FLIP)
			{
				childA.mutate(random, mutationRate);
				childB.mutate(random, mutationRate);
			}
			else if (mutationType == MutationType.MUT_GAUSS)
			{
				childA.mutateGauss(random, mutationRate, gaussDesvPad);
				childB.mutateGauss(random, mutationRate, gaussDesvPad);
			}
			
			newPopulation.add(childA);
			newPopulation.add(childB);			
		}
			
		currentPopulation = newPopulation;
		
	}
	
}