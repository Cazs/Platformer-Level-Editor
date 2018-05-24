package GUI;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import GameFrontEnd.FrontEndMain;
import GameFrontEnd.Lobby;
import Model.Actor;
import Model.Block;
import Model.Bullet;
import Model.Physics;
import Model.WorldObject;
import States.Values;


public class Canvas extends JPanel
{
	private ArrayList<Block> blocks 		= new ArrayList<Block>();
	//private Block[][] blocks 				= new Block[1][1];
	private long ms							= 0;
	private boolean pause					= false;
	private JButton btn = new JButton("Click me");
	private Graphics2D g2d					= null;
	private Image actor_img					= null;
	private Image actor_right				= null;//7901004873405374
	private Image actor_left				= null;
	private ArrayList<Actor> actors			= null;
	private double curr_jump				= 0;
	public static ArrayList<Bullet> bullets = null;
	private static final int OVAL			= 2;
	private static final int RECT			= 1;
	private static final int FILL			= 3;
	private static final int DRAW			= 4;
	private int score_x						= 0;
	private int score_y						= 0;
	private BufferedImage img = null;
	
	public Canvas()
	{
		bullets = new ArrayList<Bullet>();
		actors = new ArrayList<Actor>();
		
		//actors.add(new Actor(350,10,Values.ACTOR_SIZE,Values.ACTOR_SIZE,10));
		//actor = new Actor(350,10,Values.ACTOR_SIZE,Values.ACTOR_SIZE,10);
		/*actor_img = getToolkit().getDefaultToolkit().getImage("./sphere.png");
		actor_img = actor_img.getScaledInstance(Values.ACTOR_SIZE, Values.ACTOR_SIZE, Image.SCALE_DEFAULT);
		actor_right = getToolkit().getDefaultToolkit().getImage("./actor_final.png");
		actor_left = getToolkit().getDefaultToolkit().getImage("./actor_final_l.png");
		
		img = new BufferedImage(Values.SCR_WIDTH, Values.SCR_HEIGHT, BufferedImage.TYPE_INT_RGB);*/
		javax.swing.Timer t = new javax.swing.Timer(1,new ActionListener()
		{
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				ms += 1;
			}
		}); 
		t.start();
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		g2d = (Graphics2D) g;
		
		//Zoom
		scaleCanvas();
		
		//Clear Canvas
		//drawShape(0, 0, Values.canvas_width, Values.canvas_height, Values.bg_colour, RECT, FILL);
		g2d.setColor(Values.bg_colour);
		g2d.fillRect(0, 0, Values.canvas_width, Values.canvas_height);
		
		//Draw Time
		if(Values.FRONT_END)
		{
			Font f = g.getFont();
			g2d.setFont(new Font("Julius Sans One",Font.PLAIN,36));
			g2d.setColor(Color.RED);
			g2d.drawString(""+(Values.MAX_TIME-(FrontEndMain.GAME_TICKS-FrontEndMain.START_GAME_TICKS))/1000, (Values.SCR_WIDTH/2)-40,30);
			//g2d.setColor(Color.GREEN);
			//g2d.drawRect((Values.SCR_WIDTH/2)-65,0, 100, 50);
		}
		
		//Draw healthbar
		if(Values.physics || Values.FRONT_END)
		{
			g.setColor(Color.LIGHT_GRAY);
			//g.setClip(new Rectangle(10,10));
			g.drawRect(10, 10, 300, 50);
			g.setColor(Color.GREEN);
			if(!actors.isEmpty())
				g.fillRect(10+1, 10+1, (int) (actors.get(0).getHP()-2), 48);
		}	
		//Draw scores
		drawScores();
		
		if(!Values.FRONT_END)//Draw the cursor only when making the level
		{
			//Draw                                  
			if(ms%80 >= 0 && ms%80<=5)
			{}else
			{
				g2d.setColor(Color.GREEN);
				g2d.fillRect(Values.x,Values.y, Values.CURSOR_SIZE,Values.CURSOR_SIZE);
			}
		}
		//
		if(!Values.FRONT_END)
		{
			g2d.setColor(Color.CYAN);
			g2d.drawRect(0, 0, Values.SCR_WIDTH, Values.SCR_HEIGHT);
		}
		//Draw All Blocks
		for(Block b:blocks)
		{
			//Draw collision bounds
			if(!Values.FRONT_END)
			{
				//drawShape(b.getBounds(0, 0).x-1, b.getBounds(0, 0).y-1, b.getBounds(0, 0).width+1, b.getBounds(0, 0).height+1,Color.WHITE, RECT, DRAW);
				g2d.setColor(Color.WHITE);
				g2d.drawRect(b.getBounds(0, 0).x-1, b.getBounds(0, 0).y-1, b.getBounds(0, 0).width+1, b.getBounds(0, 0).height+1);
			}
			//Draw actual block
			g2d.setColor(b.getColour());
			g2d.fillRect((int)(b.getX()),(int)(b.getY()), b.getWidth(),b.getHeight());
			
			if(!Values.FRONT_END)
			{
				//Draw coordinates of block
				g2d.drawString("[x:" + b.getX() + ",y:" + b.getY()+"]",(int)(b.getX()),(int)(b.getY()));
				
				//Draw distance line and value
				if(b.getClosestBlock()!=null)
				{
					g2d.drawLine((int)b.getClosestBlock().getX(), (int)b.getClosestBlock().getY(),(int) b.getX(),(int) b.getY());
					g2d.drawString("distance:"+b.getGap()+"px",(int)b.getX()-b.getGap()/2, (int)b.getY());
				}
				
				//Magenta square around block if it's marked
				if(b.isMarked())
				{
					g2d.setColor(Color.MAGENTA.darker());
					g2d.drawRect((int)(b.getX())-1,(int)(b.getY())-1, b.getWidth()+1,b.getHeight()+1);
				}
				//Draw white circle if spawn point
				if(b.isSpawnpoint())
				{
					g2d.setColor(Color.WHITE.brighter());
					g2d.drawOval((int)b.getX(), (int)b.getY(), b.getWidth(), b.getHeight());
					//g2d.drawLine(b.getX()+b.getSize()/2, b.getY(), b.getX()+b.getSize()/2, b.getY()+b.getSize());
				}
				
				//Draw diagonal cross if not rigid
				if(!b.isRigid())
				{
					g2d.setColor(Color.WHITE.brighter());
					g2d.drawLine((int)b.getX(), (int)b.getY(), (int) (b.getX()+b.getWidth()), (int)(b.getY()+b.getHeight()));
					g2d.drawLine((int)(b.getX()+b.getWidth()), (int)b.getY(), (int)b.getX(),(int)( b.getY()+b.getHeight()));
				}
			}
			//Simulate physics
			if(Values.physics)// && !b.isCollided())
			{
				Physics.applyGravity(b, blocks, b.getWeight()/Values.GRAVITY);
			}
		}
		
		//Make actor jump when player presses the spacebar
		if(Values.physics)
		{
			//Check if actor is gonna collide with the bottom of a block when jumping
			for(Block b: blocks)
			{
				if(!actors.isEmpty())
				{
					if(actors.get(0).collidesWith(b, 0, -Values.JUMP_INC))
					{
						Values.jump = false;
					}
				}
			}
			if(Values.jump)
			{
				if(curr_jump + Values.JUMP_INC <= Values.MAX_JUMP)
				{
					actors.get(0).setY(actors.get(0).getY()-Values.JUMP_INC);
					sendActorCoords();
					curr_jump += Values.JUMP_INC;
				}
				else
				{
					Values.jump = false;
					curr_jump = 0;
				}
			}
			else
			{
				if(!actors.isEmpty())
					Physics.applyGravity(actors.get(0), blocks, actors.get(0).getWeight()/Values.GRAVITY);
				//Send actor coordinates to other users
				sendActorCoords();
			}
		}
		if(!Values.FRONT_END)
		{
			if(Values.canvas_focused)
			{
				g2d.setStroke(new BasicStroke(10));
				g2d.setColor(new Color(Color.GREEN.getRGB()).brighter());
				g2d.drawRect(0, 0, Values.canvas_width-5,Values.canvas_height - 5);
			}
			else
			{
				g2d.setStroke(new BasicStroke(10));
				g2d.setColor(new Color(Color.RED.getRGB()).brighter());
				g2d.drawRect(0, 0, Values.canvas_width-5,Values.canvas_height - 5);
			}
		}
		g2d.setStroke(new BasicStroke(1));
		
		//Draw actors
		for(Actor a:actors)
		{
			g2d.setColor(Color.ORANGE);
			g2d.fillOval((int)a.getX(),(int)a.getY(), a.getWidth(),a.getHeight());
			//Draw actor bounds
			if(!Values.FRONT_END)
			{
				g2d.setColor(Color.WHITE);
				g2d.drawRect(a.getBounds(0, 0).x, a.getBounds(0, 0).y, a.getBounds(0, 0).width+1, a.getBounds(0, 0).height+1);
			}
		}
		//draw bullet
		try
		{
			handleBullets(g2d);
		}
		catch(ConcurrentModificationException e)
		{
			//System.err.println("ConcurrentModificationException: " + e.getMessage());
			Main.log.println("ConcurrentModificationException: " + e.getMessage());
			Main.log.flush();
		}
		
		//Check game state
		checkGameState();
	}
	
	public void resetActors()
	{
		for(Actor a: actors)
		{
			a.setHP(300);
			a.setScore(0);
		}
	}
	
	public void addBullet(Bullet b)
	{
		if(this.bullets!=null)
			this.bullets.add(b);
		else
			System.err.println("Bullets are null");
	}
	
	public void sendActorCoords()
	{
		int x = (int)this.getActorInstance().getX();
		int y = (int)this.getActorInstance().getY();
		String usr = this.getActorInstance().getUsername();
		
		if(FrontEndMain.lobby!=null)
		{
			String msg = "MOVE\t" + usr + "\t" + x + "\t" + y;
			for(Actor a:this.getActors())
			{
				if(a != this.getActorInstance())//Don't waste time sending data to itself
				{
					sendResponse(msg,FrontEndMain.lobby.getServerInstance(),a.getIP(),a.getPort());
				}
			}
		}
	}
	
	public void sendResponse(String response,DatagramSocket server, InetAddress dest_ip,int dest_port)
	{
		DatagramPacket snd = new DatagramPacket(response.getBytes(), response.getBytes().length,dest_ip,dest_port);
		try 
		{
			server.send(snd);
		}
		catch (IOException e) 
		{
			System.err.println("IOException: " + e.getMessage());
		}
	}
	
	public ArrayList<Bullet> getBullets()
	{
		return bullets;
	}
	
	public void scaleCanvas()
	{
		double width = this.getWidth();
		double height = this.getHeight();
		Values.zoomWidth = (int) (width*Values.SCALE);
		Values.zoomHeight = (int) (height*Values.SCALE);
		
		double anchorx = (width-Values.zoomWidth)/2;
		double anchory = (height-Values.zoomHeight)/2;
		
		AffineTransform at = new AffineTransform();
		at.translate(anchorx, anchory);
		at.scale(Values.SCALE, Values.SCALE);
		//at.translate(-100, -100);
		g2d.setTransform(at);
	}
	
	public void drawScores()
	{
		g2d.setFont(new Font("Julius Sans One",Font.PLAIN,16));
		score_x = Values.SCR_WIDTH-220;
		score_y = 10;
		for(Actor actor:actors)
		{
			g2d.setColor(Color.WHITE);
			g2d.drawRect(score_x, score_y, 200, 20);
			g2d.drawString(actor.getUsername()+" : " + actor.getScore(), score_x, score_y+15);
			score_y+=20;
		}
	}
	private long hit_anim = 0;
	private javax.swing.Timer anim_timer = null;
	
	public void hitAnimation()
	{
		hit_anim = 0;
		anim_timer = new javax.swing.Timer(1, new ActionListener()
		{
			
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				if(hit_anim < 5000)//make bar go red for 2 sec
				{
					hit_anim+=1;
					g2d.setStroke(new BasicStroke(10));
					g2d.setColor(new Color(Color.RED.getRGB()).brighter());
					g2d.drawRect(0, 0, Values.canvas_width-5,Values.canvas_height - 5);
				}
				else
				{
					if(anim_timer!=null)
					{
						anim_timer.stop();
					}
				}
			}
		});
		anim_timer.start();
	}
	
	public void checkGameState()
	{
		if((Values.MAX_TIME-(FrontEndMain.GAME_TICKS-FrontEndMain.START_GAME_TICKS))/1000 <= 0)
		{
			//Show Game Over screen
			Values.showgameover = true;
			Values.showcanvas = false;
			Values.showlobby = false;
			Values.updated = false;
			
			if(FrontEndMain.gameover!=null && !Values.updated)//refresh the list once
			{
				try
				{
					//Get winner
					double max_score = 0;
					String winner = "";
					for(Actor a:FrontEndMain.key.getCanvas().getActors())
					{
						if(a.getScore()>max_score)
						{
							winner = a.getUsername();
						}
						if(a.getScore()==max_score)
						{
							winner += " " + a.getUsername();
						}
					}
					
					Socket client = null;
					for(Actor a:actors)
					{
						client = new Socket(a.getIP(),a.getPort());//25
						PrintWriter out = new PrintWriter(client.getOutputStream());
						out.println("HELO " + a.getUsername());
						out.flush();
						System.out.println("Sent greeting");
						if(readResponse(client).contains("250 HELLO"))
						{
							out.println("MAIL FROM: <" + Lobby.name + ">");
							out.flush();
							System.out.println("Sent MAIL FROM");
							if(readResponse(client).contains("250 OK"))
							{
								out.println("RCPT TO: <" + Lobby.name + ">");
								out.flush();
								System.out.println("Sent RCPT");
								if(readResponse(client).contains("250 OK"))
								{
									out.println("DATA");
									out.flush();
									System.out.println("Sent DATA");
									//if(readResponse(client).contains("250 HELLO"))
									{
										out.println(winner+"\r\n");
										out.flush();
									}
									if(readResponse(client).contains("250 OK"))
									{
										out.println("QUIT 0");
										out.flush();
										System.out.println("Sent QUIT");
										
										//if(readResponse(client).contains("221 Bye"))
										{
											System.out.println("Email Sent");
											Main.log.println("Email Sent\n");
										}
									}
								}
							}
						}
					}
					client.close();
				}
				catch(IOException e)
				{
					
				}
				FrontEndMain.gameover.refreshList();
			}
			
		}
	}
	
	public String readResponse(Socket s) throws IOException
	{
		BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
		return in.readLine();
	}
	
	public void handleBullets(Graphics g2d) throws ConcurrentModificationException
	{
		Iterator iterator = bullets.iterator();
		Bullet b = null;
		while(iterator.hasNext())
		{
			b = (Bullet) iterator.next();
			if(b != null)
			{
				g2d.setColor(Color.CYAN);
				g2d.fillOval((int)b.getX(), (int)b.getY(), 10, 10);
				switch(b.getDirection())
				{
					case 1://left
						b.setX(b.getX()-1);
						//b.setY();
						break;
					case 2://right
						b.setX(b.getX()+1);
						break;
					case 3://up
						b.setY(b.getY()-1);
						break;
					case 4://down
						b.setY(b.getY()+1);
						break;
				}
				if(Values.physics)
				{
					//Apply collision detection against blocks
					Physics.applyGravity(b, blocks, 0);//0 because they were just moved
					//Apply collision detection against other bullets
					//Physics.applyGravity(b, bullets, 0);//0 because they were just moved
				}
				if(b.collided(blocks)!=null || b.collided(bullets)!=null /*|| b.collided(actors)!=null*/)
				{
					Main.log.println("Bullet-Block/Bullet collision");
					if(iterator != null && b != null)
						iterator.remove();
					
				}
				
				Actor a = (Actor)b.collided(actors);
				if(a != null && !a.getUsername().equals(b.getSender()))//actors.get(0).getUsername()//didn't hit himself 
				{
					Main.log.println("Bullet-Actor collision");
					a.setHP(a.getHP()-20);//subtract players health by 20 if they have been hit
					if(a.getUsername().equals(getActorInstance().getUsername()))//animation
					{
						hitAnimation();
					}
					
					String resp = "UPDT_HP\t"+a.getUsername()+"\t"+a.getHP();
					sendResponse(resp,FrontEndMain.lobby.getServerInstance(), a.getIP(), a.getPort());
					
					if(iterator != null && b != null)
					{
						for(Actor actor:actors)//Send a cmd to remove the bullet to others
						{
							if(actor.getUsername().equals(b.getSender()))
							{
								actor.setScore(actor.getScore()+20);
								for(Actor act:actors)//Send a cmd to remove the bullet to others
								{
									resp = "UPDT_SCR\t"+actor.getUsername()+"\t"+actor.getScore();
									sendResponse(resp,FrontEndMain.lobby.getServerInstance(), act.getIP(), act.getPort());
								}
							}
							
							if(actor != null && !actor.getUsername().equals(b.getSender()))//Again - don't hit yourself
							{
								resp = "REM_BULT\t"+b.getID();
								sendResponse(resp,FrontEndMain.lobby.getServerInstance(), actor.getIP(), actor.getPort());
							}
						}
						iterator.remove();
					}
				}
			}
		}
	}
	
	public ArrayList<Actor> getActors()
	{
		return actors;
	}
	
	public Actor getActorInstance()
	{
		if(!actors.isEmpty())
			return this.actors.get(0);
		else
			return null;
	}
	
	public void addBlock(Block b)
	{
		blocks.add(b);
	}
	
	public ArrayList<Block> getBlocks()
	{
		return blocks;
	}
	
	@SuppressWarnings("unchecked")
	public void setBlocks(ArrayList<Block> bk)
	{
		int i = 0;
		if(bk!=null && !bk.isEmpty())
		{
			this.blocks = (ArrayList<Block>)bk.clone();
		}
		else
		{
			System.err.println("Backup empty");
		}
	}
}
