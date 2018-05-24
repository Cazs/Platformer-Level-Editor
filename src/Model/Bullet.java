package Model;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;

import States.Values;

public class Bullet extends WorldObject
{
	private int dir					= -1;
	private long id					= 0;
	private String sender			= "";
	
	public Bullet(double x,double y,int w,int h,double weight,int dir,long id,String sender)
	{
		super(x,y,w,h,weight,true,false);
		this.dir = dir;
		this.id = id;
		this.sender = sender;
	}
	
	public String getSender()
	{
		return sender;
	}
	
	public long getID()
	{
		return id;
	}
	
	public int getDirection()
	{
		return dir;
	}
	
	public WorldObject collided(ArrayList<? extends WorldObject> objects)
	{
		if(!objects.isEmpty())
		{
			for(WorldObject b:objects)
			{
				if(b != null)
				{
					if(b != this)//should not collide with itself
					{
						if(this.collidesWith(b, 0, 0))
						{
							return b;
						}
					}
				}
			}
		}
		return null;
	}
	/*public double getDirection()
	{
		double slope = (target_y - this.getY())/(target_x - this.getX()); 
		double direction = Math.atan(slope);
		return direction;
	}
	
	public Point getTarget()
	{
		return new Point((int)target_x,(int)target_y);
	}*/
}

