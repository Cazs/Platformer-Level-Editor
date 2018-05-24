package Model;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.io.Serializable;


public class Block extends WorldObject implements Serializable
{
	private Color col 					= null;
	private boolean marked 				= false;
	
	public Block(double x,double y,int w,int h,double weight,Color col,boolean rigid,boolean spawn)
	{
		super(x,y,w,h,weight,rigid,spawn);
		this.col = col;
	}
	
	public void setColour(Color col){this.col = col;}
	
	public void setMarked(boolean m){this.marked = m;}
	
	public boolean isMarked(){return marked;}
		
	public Color getColour(){return col;}
}
