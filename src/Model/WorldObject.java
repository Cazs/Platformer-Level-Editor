package Model;

import java.awt.Rectangle;
import java.io.Serializable;

public class WorldObject implements Serializable
{
	private double x				= 0;
	private double y				= 0;
	private boolean rigid			= false;
	private boolean spawn			= false;
	private int width				= 0;
	private int height				= 0;
	private double weight			= 0;
	private Block closestBlock		= null;
	private int gap					= -1;
	private boolean collision_top   = false;
	private boolean collision_bottom   = false;
	private boolean collision_left   = false;
	private boolean collision_right   = false;
	
	public WorldObject(double x,double y,int w,int h,double weight,boolean rigid,boolean spawn)
	{
		this.x = x;
		this.y = y;
		this.width = w;
		this.height =  h;
		this.weight = weight;
		this.rigid = rigid;
		this.spawn = spawn;
	}
	
	public void setClosestBlockLeft(Block b)
	{
		this.closestBlock = b;
		if(closestBlock!=null)
		{
			gap =  (int) Math.sqrt(Math.pow((this.getX()-closestBlock.getX()),2)+Math.pow((this.getY()-closestBlock.getY()),2));
			//(int)closestBlock.getClosestBlockLeft().getX(), (int)closestBlock.getClosestBlockLeft().getY(),(int) closestBlock.getX(),(int) closestBlock.getY())
		}
		else
		{
			System.out.println("First block with no left block");
		}
	}
	
	public int getGap(){return gap;}
	
	public Block getClosestBlock(){return closestBlock;} 
	
	public void setX(double x){this.x = x;}
	
	public void setY(double y){this.y = y;}
	
	public void setCollisionTop(boolean col)	{this.collision_top = col;}
	
	public void setCollisionBottom(boolean col)	{this.collision_bottom = col;}
	
	public void setCollisionLeft(boolean col)	{this.collision_left = col;}
	
	public void setCollisionRight(boolean col)	{this.collision_right = col;}
	
	public boolean getCollisionTop(){return collision_top;}
	
	public boolean getCollisionBottom(){return collision_bottom;}
	
	public boolean getCollisionLeft(){return collision_left;}
	
	public boolean getCollisionRight(){return collision_right;}
	
	public void setRigid(boolean r){this.rigid = r;}
	
	public void setSpawnpoint(boolean s){this.spawn = s;}
	
	public void setWeight(double weight){this.weight = weight;}
	
	public void setWidth(double w){this.width = (int) w;}
	
	public void setHeight(double h){this.height = (int) h;}
	
	public double getX(){return x;}
	
	public double getY(){return y;}
	
	public boolean isRigid(){return rigid;}
	
	public boolean isSpawnpoint(){return spawn;}
	
	public double getWeight(){ return weight;}
	
	public int getWidth(){return width;}
	
	public int getHeight(){return height;}
	
	public Rectangle getBounds(double xOffset,double yOffset)
	{
		Rectangle bounds = new Rectangle();
		bounds.x = (int) (x + xOffset);
		bounds.y = (int) (y + yOffset);
		bounds.width = width;
		bounds.height = height;
		return bounds;
	}
	
	public boolean collidesWith(WorldObject o,double xOffset,double yOffset)
	{
		if(getBounds(xOffset, yOffset).intersects(o.getBounds(0, 0)))
			return true;
		else
			return false;
	}
}
