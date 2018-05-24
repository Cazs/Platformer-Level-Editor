package States;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import GUI.Canvas;
import GUI.Main;
import GameFrontEnd.FrontEndMain;
import Model.Actor;
import Model.Block;
import Model.Bullet;

public class Key extends KeyAdapter implements ActionListener
{
	private boolean a_down 			= false;
	private boolean d_down 			= false;
	private boolean w_down 			= false;
	private boolean s_down 			= false;
	private boolean jump			= false;
	private double speed   			= 0;
	private double acceleration	   	= 0.3;
	private double deceleration	   	= 0.7;
	private final Canvas canvas 	= new Canvas();
	private long bult_id					= 0;
	
	public Key()
	{
		canvas.setLayout(null);
	}
	
	private Block getClosestBlock(Block target)
	{
		double shortest_distance = -1;
		double distance = 0;
		Block shortest_distance_instance = null;
		int i=0;
		for(Block b:canvas.getBlocks())
		{
			//x = b.getX();
			distance = Math.sqrt(Math.pow((target.getX()-b.getX()),2)+Math.pow((target.getY()-b.getY()),2));
			
			if(shortest_distance<0)//the first block's distance
			{
				shortest_distance = distance;
			}
			
			if(distance<=shortest_distance)
			{
				shortest_distance = distance;
				shortest_distance_instance = b;
			}
			i++;
		}
		Main.log.println("Looped through:"+i+" blocks");
		return shortest_distance_instance;
	}
	
	@Override
	public void keyPressed(KeyEvent e) 
	{
		super.keyPressed(e);
		switch(e.getKeyCode())
		{
			case KeyEvent.VK_1:
				Values.pan -= 1;
				break;
			case KeyEvent.VK_2:
				Values.pan += 1;
				break;
			case KeyEvent.VK_UP:				
				if(Values.physics || Values.FRONT_END)
				{
					Canvas.bullets.add(new Bullet(canvas.getActorInstance().getX(),canvas.getActorInstance().getY(),Values.BULLET_W,Values.BULLET_H,Values.BULLET_WEIGHT,3,bult_id,canvas.getActorInstance().getUsername()));
					sendBulletToClients(canvas.getActorInstance().getX(),canvas.getActorInstance().getY(),Values.BULLET_W,Values.BULLET_H,Values.BULLET_WEIGHT,3,bult_id,canvas.getActorInstance().getUsername());
					bult_id+=1;
				}
				else
				{
					for(Block b:canvas.getBlocks())
						b.setY(b.getY()+Values.pan);
				}
				break;
			case KeyEvent.VK_DOWN:
				
				if(Values.physics || Values.FRONT_END)
				{
					Canvas.bullets.add(new Bullet(canvas.getActorInstance().getX(),canvas.getActorInstance().getY(),Values.BULLET_W,Values.BULLET_H,Values.BULLET_WEIGHT,4,bult_id,canvas.getActorInstance().getUsername()));
					sendBulletToClients(canvas.getActorInstance().getX(),canvas.getActorInstance().getY(),Values.BULLET_W,Values.BULLET_H,Values.BULLET_WEIGHT,4,bult_id,canvas.getActorInstance().getUsername());
					bult_id+=1;
				}
				else
				{
					for(Block b:canvas.getBlocks())
						b.setY(b.getY()-Values.pan);
				}
				break;
			case KeyEvent.VK_RIGHT:
				if(Values.physics || Values.FRONT_END)
				{
					//System.out.print("Shooting right, Physics: " + Values.physics + " Frontend: " + Values.FRONT_END);
					Canvas.bullets.add(new Bullet(canvas.getActorInstance().getX(),canvas.getActorInstance().getY(),Values.BULLET_W,Values.BULLET_H,Values.BULLET_WEIGHT,2,bult_id,canvas.getActorInstance().getUsername()));
					sendBulletToClients(canvas.getActorInstance().getX(),canvas.getActorInstance().getY(),Values.BULLET_W,Values.BULLET_H,Values.BULLET_WEIGHT,2,bult_id,canvas.getActorInstance().getUsername());
					bult_id+=1;
				}
				else
				{
					for(Block b:canvas.getBlocks())
						b.setX(b.getX()-Values.pan);
				}
				break;
			case KeyEvent.VK_LEFT:
				if(Values.physics || Values.FRONT_END)
				{
					canvas.bullets.add(new Bullet(canvas.getActorInstance().getX(),canvas.getActorInstance().getY(),Values.BULLET_W,Values.BULLET_H,Values.BULLET_WEIGHT,1,bult_id,canvas.getActorInstance().getUsername()));
					sendBulletToClients(canvas.getActorInstance().getX(),canvas.getActorInstance().getY(),Values.BULLET_W,Values.BULLET_H,Values.BULLET_WEIGHT,1,bult_id,canvas.getActorInstance().getUsername());
					bult_id+=1;
				}
				else
				{
					for(Block b:canvas.getBlocks())
						b.setX(b.getX()+Values.pan);
				}
				break;
			case KeyEvent.VK_A:
				a_down = true;
				Values.direction="LEFT";
				if(!Values.physics)
				{
					//Move cursor...in edit mode
					if(Values.x-Values.STEP <= 200)
						for(Block b:canvas.getBlocks())
							b.setX(b.getX()+Values.STEP);
					else
						Values.x-=Values.STEP;
				}
				break;
			case KeyEvent.VK_D:
				d_down = true;
				Values.direction="RIGHT";
				if(!Values.physics)
				{
					//Move cursor...in edit mode
					if(Values.x+Values.STEP >= Values.canvas_width-200)
						for(Block b:canvas.getBlocks())
							b.setX(b.getX()-Values.STEP);
					else
						Values.x+=Values.STEP;
				}
				break;
			case KeyEvent.VK_W:
				w_down = true;
				if(!Values.physics)
				{
					if(Values.y-Values.STEP <= 200)
						for(Block b:canvas.getBlocks())
							b.setY(b.getY()+Values.STEP);
					else
						Values.y-=Values.STEP;
				}
				break;
			case KeyEvent.VK_S:
				s_down = true;
				if(!Values.physics)
				{
					if(Values.y+Values.STEP >= Values.canvas_height-200)
						for(Block b:canvas.getBlocks())
							b.setY(b.getY()-Values.STEP);
					else
						Values.y+=Values.STEP;
				}
				break;
			case KeyEvent.VK_SPACE:
				if(canvas.getActorInstance().getCollisionBottom())
				{
					Values.jump = true;
				}
				break;
			//Add to map
			case KeyEvent.VK_ENTER:
				boolean addable = true;
				java.util.Iterator<Block> it = canvas.getBlocks().iterator();
				if(Values.erase)
				{
					while(it.hasNext())
					{
						Block b = it.next();
						
						if(b.getX() == Values.x && b.getY() == Values.y)
						{
							it.remove();
						}
					}
				}
				else
				{
					for(Block b:canvas.getBlocks())
					{
						if(b.getX() == Values.x && b.getY() == Values.y)
						{
							addable = false;
							Main.info.append("A block already exists here \n");
						}
					}
					
					if(addable)
					{
						Block add = new Block(Values.x,Values.y,Values.CURSOR_SIZE,Values.CURSOR_SIZE,0,Values.block_colour,true,false);
						add.setClosestBlockLeft(getClosestBlock(add));
						canvas.addBlock(add);
						Main.id++;
					}
				}
				break;
			//Erase
			case KeyEvent.VK_E:
				if(Values.erase)
					Values.erase = false; 
				else 
					Values.erase = true;
				break;
			case KeyEvent.VK_R:
				if(Values.CURSOR_SIZE < 100)
					Values.CURSOR_SIZE += 10;
				break;
			case KeyEvent.VK_T:
				if(Values.CURSOR_SIZE > 10)
					Values.CURSOR_SIZE -= 10;
				break;
			case KeyEvent.VK_X:
				if(Values.colour) Values.colour = false; else Values.colour=true;
				break;
		}
	}
	
	@Override
	public void keyReleased(KeyEvent e) 
	{
		super.keyReleased(e);
		switch(e.getKeyCode())
		{
			case KeyEvent.VK_A:
				a_down = false;
				break;
			case KeyEvent.VK_D:
				d_down = false;
				break;
			case KeyEvent.VK_W:
				w_down = false;
				break;
			case KeyEvent.VK_S:
				s_down = false;
				break;
		}
	}
	
	public void sendResponse(String response,DatagramSocket server, InetAddress dest_ip,int dest_port) throws IOException
	{
		DatagramPacket snd = new DatagramPacket(response.getBytes(), response.getBytes().length,dest_ip,dest_port);
		server.send(snd);
		Main.log.println("Sent Packet To Client: " + dest_ip);
	}
	
	private void sendBulletToClients(double x,double y, int w, int h, double weight, int dir,long id,String sender)
	{
		if(FrontEndMain.lobby!=null && canvas!=null)
		{
			String msg = "ADD_BULT\t" + x + "\t" + y + "\t" + w + "\t" + h + "\t" + weight + "\t" + dir + "\t" + id + "\t" + sender;
			for(Actor a:canvas.getActors())
			{
				if(a != canvas.getActorInstance())//Don't waste time sending data to itself
				{
					try 
					{
						sendResponse(msg,FrontEndMain.lobby.getServerInstance(),a.getIP(),a.getPort());
					} 
					catch (IOException e) 
					{
						e.printStackTrace();
					}
				}
			}
		}
		else
		{
			System.err.println("Lobby or Canvas are null");
		}
	}
	
	public Canvas getCanvas()
	{
		return canvas;
	}
	
	private void listen()
	{
		Actor a = canvas.getActorInstance();
		
		if(!a_down && !d_down)
		{
			//decrease speed
			if(speed > 0)
				speed -= deceleration;
			//Move world while decelerating
			if(Values.direction.equals("RIGHT") && speed > 0)
			{
				boolean rcol = false;
				if(a!=null)
				{
					if(a.getX() + speed > Values.canvas_width - 50)
					{
						//Move world
						for(Block b:canvas.getBlocks())
							rcol = b.collidesWith(a,-speed , 0)?true:false;
						if(!rcol)
						{
							for(Block b:canvas.getBlocks())
								b.setX(b.getX() - speed);
						}
						else
						{
							speed = 0;
							for(Block b:canvas.getBlocks())
								b.setX(a.getX());
						}
					}
					else
					{
						//Move actor if no collision
						for(Block b:canvas.getBlocks())
							if(a.collidesWith(b,speed , 0))
								rcol = true;
						if(!rcol)
							a.setX(a.getX()+ speed);
					}
				}
			}
			if(Values.direction.equals("LEFT") && speed > 0)
			{
				boolean lcol = false;
				if(a!=null)
				{
					if(a.getX() - speed < 50)
					{
						//Move world
						for(Block b:canvas.getBlocks())
							lcol = b.collidesWith(a,speed , 0)?true:false;
						if(!lcol)
						{
							//for(Block b:canvas.getBlocks())
								//b.setX(b.getX() + speed);
						}
						else
						{
							speed = 0;
							//for(Block b:canvas.getBlocks())
								//b.setX(a.getX()+a.getWidth());
						}
					}
					else
					{
						//Move actor if no collision
						for(Block b:canvas.getBlocks())
							if(a.collidesWith(b,-speed , 0))
								lcol = true;
						if(!lcol)
							a.setX(a.getX()- speed);
					}
				}
			}
		}
		
		if(a_down && Values.physics)
		{
			//Increase speed
			if(speed < Values.MAX_SPEED)
			{
				speed += acceleration;
			}
			
			Values.run = true;
			boolean lcol = false;
			if(a!=null)
			{
				moveActor(a,1);
				//Move world
				/*if(a.getX()- speed < 200)
				{
					for(Block b:canvas.getBlocks())
						lcol = b.collidesWith(a,speed , 0)?true:false;
					if(!lcol)
						for(Block b:canvas.getBlocks())
							b.setX(b.getX() + speed);
				}
				else
				{
					/*double xcol 	= 0;
					double xcolwidth = 0;
					//Move actor if no collision
					for(Block b:canvas.getBlocks())
					{
						if(a.collidesWith(b,-speed , 0))
						{
							lcol = true;
							xcol = b.getX();
							xcolwidth = b.getWidth();
							break;
						}
					}
					if(!lcol)
					{
						a.setX(a.getX()- speed);
					}
					else
					{
						a.setX(xcol + xcolwidth);
						speed = 0;
					}*/
					/*moveActor(a,1);
				}*/
			}
		}
		
		if(d_down && Values.physics)
		{
			//Increase speed
			if(speed < Values.MAX_SPEED)
			{
				speed += acceleration;
			}
			
			Values.run = true;
			boolean rcol = false;
			if(a!=null)
			{
				moveActor(a,0);
				//Move world
				/*if(a.getX() + speed > Values.canvas_width - 200)
				{
					for(Block b:canvas.getBlocks())
						rcol = b.collidesWith(a,-speed , 0)?true:false;
					if(!rcol)
						for(Block b:canvas.getBlocks())
							b.setX(b.getX() - speed);
				}
				else
				{
					//Move actor if no collision
					/*for(Block b:canvas.getBlocks())
						if(a.collidesWith(b,speed , 0))
							rcol = true;
					if(!rcol)
					{
						a.setX(a.getX()+ speed);
					}
					else
					{
						a.setX(xcol + xcolwidth);
						speed = 0;
					}*/
					/*moveActor(a,0);
				}*/
			}
		}
		
		if(w_down && Values.physics)
		{
			//Move actor
		}
		
		if(s_down && Values.physics)
		{
			//Move actor 
		}
	}
	
	private void moveActor(Actor a,int dir)//1=left, 0=right
	{
		double xcoll = 0;
		double ycoll = 0;
		int wcoll = 0;
		int hcoll = 0;
		
		boolean coll = false;
		//Move actor if no collision
		for(Block b:canvas.getBlocks())
		{
			if(dir==1)//left
			{
				//if(a.collidesWith(b,dir==1?-speed:speed , 0))
				if(a.collidesWith(b, -speed, 0))
				{
					xcoll = b.getX();
					ycoll = b.getY();
					wcoll = b.getWidth();
					hcoll = b.getHeight();
					
					coll = true;
					break;
				}
			}
			else//right
			{
				//if(a.collidesWith(b,dir==1?-speed:speed , 0))
				if(a.collidesWith(b, speed, 0))
				{
					xcoll = b.getX();
					ycoll = b.getY();
					wcoll = b.getWidth();
					hcoll = b.getHeight();
					
					coll = true;
					break;
				}
			}
		}
		if(!coll)
		{
			a.setX(dir==1?a.getX()-speed:a.getX()+speed);
		}
		else
		{
			
			//a.setX(dir==1?xcoll+wcoll:xcoll-a.getWidth());
			speed = 0;
		}
	}

	private void shiftScreen()
	{
		Actor a = canvas.getActorInstance();
		if(a!=null)
		{
			if(a.getX() > Values.SCR_WIDTH-100)
			{
				for(Block b:canvas.getBlocks())
					if(b!=null)
						b.setX(b.getX()-1);
			}
			
			if(a.getX() < 100)
			{
				for(Block b:canvas.getBlocks())
					b.setX(b.getX()+1);
			}
			
			if(a.getY() > Values.SCR_HEIGHT-100)
			{
				for(Block b:canvas.getBlocks())
					b.setY(b.getY()-1);
			}
			
			if(a.getY() < 100)
			{
				for(Block b:canvas.getBlocks())
					b.setY(b.getY()+1);
			}
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		listen();
	}
}