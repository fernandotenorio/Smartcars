import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

public class Car implements Comparable<Car>
{
	int w = 12;
	int h = 20;
	int offset = 2;
	int wheelTick = 4;
	static Color defaultColor = new Color(80, 120, 255);
	static Color eliteColor = Color.orange;
	Color color = defaultColor;
	int x;
	int y;
	double maxSpeed  = 2.0;
	double maxTurnRate = 0.3;
	double leftForce;
	double rightForce;
	double xdir;
	double ydir;
	double rotation;
	double speed;
	NeuralNet brain;
	float fitness;	
	 
	public Car(int x, int y, double lf, double rf, NeuralNet brain)
	{
		this(x, y, lf, rf);		
		this.brain = brain;
	}
	public Car(int x, int y, double lf, double rf)
	{
		this.x = x;
		this.y = y;
		this.leftForce = lf;
		this.rightForce = rf;
	}
	
	public int compareTo(Car other)
	{						
		if (this.fitness > other.fitness)
				return 1;
		else if (this.fitness < other.fitness)
			return -1;
			
		return 0;
	}
	public Point getCenter()
	{
		return new Point(x + w/2, y + h/2);
	}
	
	public void update()
	{
		double force = leftForce - rightForce;
		
		if (force < -maxTurnRate)
			force = -maxTurnRate;
		else if (force > maxTurnRate)
			force = maxTurnRate;
		
		rotation += force;				
		speed = leftForce + rightForce; 
		xdir = -Math.sin(rotation);		
		ydir = Math.cos(rotation);		
		
		x = x +  (int)(xdir * speed); 
		y = y + (int)(ydir * speed);									
	}
	
	private void drawArrowHead(Graphics2D g2, Point tip, Point tail, Color color)  
	{  
		double phi = Math.toRadians(30);  
        double barb = 10;
        
        g2.setPaint(color);  
        double dy = tip.y - tail.y;  
        double dx = tip.x - tail.x;  
        double theta = Math.atan2(dy, dx);  
       
        double x, y, rho = theta + phi;  
        for(int j = 0; j < 2; j++)  
        {  
            x = tip.x - barb * Math.cos(rho);  
            y = tip.y - barb * Math.sin(rho);  
            g2.draw(new Line2D.Double(tip.x, tip.y, x, y));  
            rho = theta - phi;  
        }  
	}
	
	private void drawArrow(Graphics2D g)
	{
		Point p1 = getCenter();
		Point p2 = new Point((int)(p1.x  + 25 * xdir * speed), (int)(p1.y + 25 * ydir * speed));
		Point p3 = new Point((int)(p1.x  + 25 * xdir * speed), (int)(p1.y + 25 * ydir * speed));
		g.setColor(Color.red);
		g.drawLine(p1.x, p1.y, p2.x, p2.y);
		drawArrowHead(g, p2, p1, Color.red);
	}
	
   	public void draw(Graphics2D g)
	{		
		Graphics2D g2 = (Graphics2D) g.create();
                
        //drawArrow(g);
        
		g2.rotate(rotation - Math.PI, x + w/2.0, y + h/2.0);
		g2.setColor(Color.black);
		g2.fillRect(x - offset, y + offset, 2 * offset + w, wheelTick);
		g2.fillRect(x - offset, y + h - offset - wheelTick, 2 * offset + w, wheelTick);
		g2.setColor(color);
		g2.fillRect(x, y, w, h);
		g2.setColor(Color.black);
		g2.fillRect(x + offset, y + offset, w - 2*offset, w - 2*offset);
					        
		g2.dispose();
	}
	
}

class Item
{
	int x;
	int y;
	int size = 6;
	Color color = new Color(0, 160, 0);
	
	public Item(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	public void draw(Graphics2D g)
	{
		g.setColor(color);
		//g.fillRect(x, y, size, size);
		g.drawRect(x, y, size, size);
	}
	
	public Point getCenter()
	{
		return new Point(x + size/2, y + size/2);
	}
}