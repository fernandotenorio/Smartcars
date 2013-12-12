import java.util.ArrayList;
import java.util.Random;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.*;
import java.util.Collections;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class World
{
	//Entties
	ArrayList<Car> cars;
	ArrayList<Item> items;
	Point[] carPositions;
	Point[] itemPositions;
	final int carSize;
	final int itemSize;
	
	//Screen
	Rectangle screen;
	final boolean wrapScreen = true;
	final int w;
	final int h;
	
	//Statistics
	ArrayList<Float> bestList = new ArrayList<Float>();
	ArrayList<Float> averageList = new ArrayList<Float>();
	final int cycles = 1200;
	
	//NN settings
	final int inputs = 4;
	final int outputs = 2;
	final int hidden = 1;
	final int neuronsPerHidden = 6;

	//GA
	GeneticAlgorithm<Double, Double> ga = null;
	final float mutationRate = 0.3f;
	final float cxRate = 0.9f;
	final int elite = 4;
	Random random = new Random();	
	
	public World(int ncars, int nitems, int w, int h)
	{
		this.w = w;
		this.h = h;
		
		cars = new ArrayList<Car>(ncars);
		items = new ArrayList<Item>(nitems);
		carPositions = new Point[ncars];
		itemPositions = new Point[nitems];
		
		Item dummyItem = new Item(0, 0 );
		this.itemSize = dummyItem.size;
		Car dummyCar = new Car(0, 0, 0.0f, 0.0f);
		this.carSize = dummyCar.h;
		
		for (int i = 0; i < nitems; i++)
			itemPositions[i] = new Point(random.nextInt(w - itemSize), random.nextInt(h - itemSize));
			
		for (int i = 0; i < ncars; i++)
			carPositions[i] = new Point(random.nextInt(w - carSize), random.nextInt(h - carSize));
		
		NeuralNet dummyNN = new NeuralNet(inputs, outputs, hidden, neuronsPerHidden, random);	
		int nWeights = dummyNN.getWeights().size();
		
		for (int i = 0; i < ncars; i++)
		{
			NeuralNet nn = new NeuralNet(inputs, outputs, hidden, neuronsPerHidden, random);											
			cars.add(new Car(carPositions[i].x, carPositions[i].y, 0.0, 0.0, nn));			
		}
			
		for (int i = 0; i < nitems; i++)
			items.add(new Item(itemPositions[i].x, itemPositions[i].y));
			
		//GA				
		ArrayList<BaseChromosome<Double, Double>> population = new ArrayList<BaseChromosome<Double, Double>>();
		Double[] min = new Double[nWeights];
		Double[] max = new Double[nWeights];
		
		for (int  i = 0; i < nWeights; i++)
		{
			min[i] = -1.0;
			max[i] = 1.0;
		}
		
		for (int i = 0; i < cars.size(); i++)
		{
			RealChromosome r = new RealChromosome(min, max);
			r.randomizeGenotype(random);
			Car car = cars.get(i);
			car.brain.putWeights(r.fenotype);																	
			population.add(r);
		}
		
		ga = new GeneticAlgorithm<Double, Double>(population, null, mutationRate, cxRate, elite, CrossoverType.CX_BLEND, 
		MutationType.MUT_GAUSS);
		
		screen = new Rectangle(0, 0, w, h);
											
	}
	
	private void pickItems()
	{
		for (Car car : cars)
		{
			for (Item item : items)
			{
				Point cc = car.getCenter();
				Point ic = item.getCenter();
				
				int dx = cc.x - ic.x;
				int dy = cc.y - ic.y;
				if (Math.sqrt(dx * dx + dy * dy) < carSize/2)
				{
					car.fitness += 1;
					item.x = random.nextInt(w - itemSize);
					item.y = random.nextInt(h - itemSize);
				}
			}
		}
	}
	
	public void resetWorld()
	{
		for (int i = 0; i < cars.size(); i++)
		{
			Car c = cars.get(i);
			c.x = carPositions[i].x;
			c.y = carPositions[i].y;
		}
		
		for (int i = 0; i < items.size(); i++)
		{
			Item item = items.get(i);
			item.x = itemPositions[i].x;
			item.y = itemPositions[i].y;
		}
	}
	
	private Rectangle carRect = new Rectangle(0, 0, 0, 0);
	private int m = 0;	
	public void update()
	{				
		for (int i = 0; i < cars.size(); i++)
		{
			Car car = cars.get(i);
			pickItems();			
			
			double[] inputs = new double[4];
			double[] closest = getClosestItem(car);
			inputs[0] = closest[0];
			inputs[1] = closest[1];
			inputs[2] = car.xdir;
			inputs[3] = car.ydir;															
			
			ArrayList<Double> outputs = car.brain.update(inputs);
			car.leftForce = outputs.get(0);
			car.rightForce = outputs.get(1);
			car.update();
			
			if (wrapScreen)
			{
				carRect.setBounds(car.x, car.y, car.w, car.h);			
				Rectangle intersection = screen.intersection(carRect);
				Point center = car.getCenter();
				
				if (intersection.isEmpty())
				{
					 if (center.x > w)
						 car.x = 0;
					 if (center.x < 0)
						 car.x = w;
					 if (center.y > h)
						 car.y = 0;
					 if (center.y < 0)
						 car.y = h;
				}
			}			
		}				
		
		if (m == cycles)
		{												
			float averageFitness = 0.0f;
			float bestFitness = 0.0f;
				
			for (int i = 0; i < cars.size(); i++)
			{					
				Car car = cars.get(i);				
				BaseChromosome r = ga.currentPopulation.get(i);
				r.fitness = car.fitness;	
				
				if (r.fitness > bestFitness)
					bestFitness = r.fitness;
					
				averageFitness += car.fitness;
			}		
			
			bestList.add(bestFitness);
			averageList.add(averageFitness/cars.size());
			
			System.out.println("Average fitness: " + averageFitness/cars.size());
			System.out.println("Best fitness: " + bestFitness);
			System.out.println();
			
			ga.evolve();						
						
			for (int i = 0; i < cars.size(); i++)
			{				
				Car car = cars.get(i);
				car.color = Car.defaultColor;
				BaseChromosome<Double, Double> r = ga.currentPopulation.get(i);					
				car.brain.putWeights(r.fenotype);
				car.fitness = 0;
			}
			
			for (int  i = 0; i < elite; i++)
				cars.get(i).color = Car.eliteColor;
			
			resetWorld();
			m = 0;
		}
		else
		{			
			m++;
		}
	}
	
	public double[] getClosestItem(Car car)
	{
		double minDistance = Double.MAX_VALUE;		
		double dxMin = Double.MAX_VALUE;
		double dyMin = Double.MAX_VALUE;
		Point cc = car.getCenter();
		
		for (Item item : items)
		{
			Point pc = item.getCenter();
			double dx = pc.x - cc.x;
			double dy = pc.y - cc.y;			
			double dist = dx * dx + dy * dy;
			
			if (dist < minDistance)
			{				
				minDistance = dist;
				dxMin = dx;
				dyMin = dy;
			}
		}
		
		double mod = Math.sqrt(dxMin * dxMin + dyMin * dyMin);
		if (mod < 0.0001)
			return new double[]{dxMin, dyMin};
		else
			return new double[]{dxMin/mod, dyMin/mod};
	}
	
	public void render(Graphics g)
	{
		Graphics2D g2d = (Graphics2D)g;
				
		for (Item item :  items)
			item.draw(g2d);
			
		for (Car c : cars)
			c.draw(g2d);
		
	}
	public static void main (String[] args)
	{
		JFrame f = new JFrame("Smart Cars");
		f.setResizable(false);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		WorldPanel world = new WorldPanel();
		f.getContentPane().add(world, BorderLayout.WEST);		
		f.pack();
		f.setVisible(true);		
	}
	
}	//fim da classe

 class WorldPanel extends JPanel implements Runnable
{
	int w = 700;
	int h = 600;
	int cars = 20;
	int items = 120;
	World world = new World(cars, items, w, h);
	ScheduledThreadPoolExecutor animator = null;
	static long FPS = 120L;
	boolean chartMode = false;
	SimpleLineChart chart = new SimpleLineChart(w, h, 10, new Color[]{new Color(255, 40, 40), new Color(40, 40, 255)});
		
	public WorldPanel()
	{
		setPreferredSize(new Dimension(w, h));
		setDoubleBuffered(true);
		setFocusable(true);
 		requestFocus();
 	 	addKeyWatcher();
 	 	setBackground(Color.white);
	}
	
	private void addKeyWatcher()
 	{
  		addKeyListener(new KeyAdapter() 
  		{
   			public void keyPressed(KeyEvent e)
   			{ 
   				 int key = e.getKeyCode();
    			if (key == KeyEvent.VK_SPACE) 
    			{     				
    				chartMode = !chartMode;
     				if (chart.data == null)
     				{
     					ArrayList<ArrayList<Float>> data = new ArrayList<ArrayList<Float>>();
     					data.add(world.bestList);
     					data.add(world.averageList);
     					chart.data = data;     					
     				}
    			}
			} 
		});
	}
    	       
	public void addNotify()
	{
		super.addNotify();
		long delayToStart = 100L;
		animator = new ScheduledThreadPoolExecutor(1);
		animator.scheduleAtFixedRate(this, delayToStart, 1000L/FPS, TimeUnit.MILLISECONDS);
	}
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
		RenderingHints.VALUE_ANTIALIAS_ON);
		
		if (chartMode)
			chart.render(g);
		else
			world.render(g);		
	}
	
	public void run()
	{				
		world.update();							
		repaint();			
	}
	
}