import javax.swing.*;
import java.awt.*;
import java.util.*;

public class SimpleLineChart
{	
	ArrayList<ArrayList<Float>> data;
	int w;
	int h;		
	int margin;
	Color[] colors;
	Color borderColor = Color.lightGray;
				
	public SimpleLineChart(int w, int h, int margin, Color[] colors)
	{
		this.w = w;
		this.h = h;	
		this.colors = colors;
		this.margin = margin;
	}
	
	public void render(Graphics g)
	{
		if (data == null || data.size() == 0)
			return;
			
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
		RenderingHints.VALUE_ANTIALIAS_ON);
						
		int series = data.size();
		float ymin = Float.MAX_VALUE;
		float ymax = Float.MIN_VALUE;
						
		for (int i = 0; i < series; i++)
		{
			for (int j = 0; j < data.get(i).size(); j++)
			{
				if (data.get(i).get(j) < ymin)				
					ymin = data.get(i).get(j);
				if (data.get(i).get(j) > ymax)				
					ymax = data.get(i).get(j);
			}
		}
				
		float yratio = (h - 2.0f * margin)/(ymax - ymin);
				
		g.setColor(borderColor);
		g.drawRect(margin, margin, w - 2 * margin, h - 2 * margin);
				
		for (int k = 0; k < series; k++)
		{						
			if (data.get(k).size() < 2)
				continue;
				
			g.setColor(colors[k]);		
			
			float xratio = (w - 2.0f * margin)/(data.get(k).size() - 1.0f);
			Point[] points = new Point[data.get(k).size()];
			
			for (int i = 0; i < points.length; i++)
			{
				int x = (int)(margin + xratio * i);				
				int y = (int)(margin + yratio * (data.get(k).get(i) - ymin));
				points[i] = new Point(x, h - y);
			}
			
			for (int  i = 0; i < points.length - 1; i++)
			{
				Point p1 = points[i];
				Point p2 = points[i + 1];
				
				g.drawLine(p1.x, p1.y, p2.x, p2.y);
			}			
		}
	}
	
}	//fim da classe