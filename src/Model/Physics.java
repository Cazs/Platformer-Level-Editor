package Model;

import java.awt.Rectangle;
import java.util.ArrayList;

import States.Values;

public class Physics 
{
	private static double gravity = 9.8;
	
	public Physics()
	{
		
	}
	
	public static void applyGravity(WorldObject object,ArrayList<? extends WorldObject> objects,double yvel)
	{
		boolean can_move = true;
		for(WorldObject b:objects)
		{
			if(b != object)//make sure it doesn't collide with itself
			{
				if(object.collidesWith(b, 0, yvel))
				{
					can_move = false;
				}
			}
		}
		if(can_move)
		{
			object.setY(object.getY() + yvel);
			//object.setX(object.getX() + xvel);
			
			/*if(xvel > 0)
				object.setCollisionLeft(false);
			if(xvel < 0)
				object.setCollisionRight(false);
			if(yvel > 0)
				object.setCollisionTop(false);
			if(yvel < 0)*/
			object.setCollisionBottom(false);
		}
		else
		{
			/*if(xvel > 0)
				object.setCollisionLeft(true);
			if(xvel < 0)
				object.setCollisionRight(true);
			if(yvel > 0)
				object.setCollisionTop(true);
			if(yvel < 0)*/
			object.setCollisionBottom(true);
		}
	}
}
