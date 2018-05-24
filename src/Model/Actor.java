package Model;
import java.awt.Rectangle;
import java.net.InetAddress;
import java.util.ArrayList;

import States.Values;


public class Actor extends WorldObject
{
	private boolean fall					= false;
	private double hp						= 300;
	private InetAddress ip					= null;
	private int port						= 0;
	private String username					= "";
	private boolean ready					= false;
	private double score					= 0;
	
	public Actor(double x,double y,int w,int h,double weight,InetAddress ip,int port,String username)
	{
		super(x,y,w,h,weight,true,false);
		this.ip = ip;
		this.port = port;
		this.username = username;
	}
	
	public void setScore(double score)
	{
		this.score = score;
	}
	
	public double getScore()
	{
		return score;
	}
	
	public void setReady(boolean ready)
	{
		this.ready = ready;
	}
	
	public boolean getReady()
	{
		return this.ready;
	}
	
	public double getHP()
	{
		return hp;
	}
	
	public void setHP(double hp)
	{
		this.hp = hp;
	}
	
	public InetAddress getIP()
	{
		return ip;
	}
	
	public int getPort()
	{
		return port;
	}
	
	public String getUsername()
	{
		return username;
	}
}
