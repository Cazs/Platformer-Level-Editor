package GameFrontEnd;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import GUI.GameOver;
import GUI.Main;
import Model.Actor;
import Model.Block;
import States.Key;
import States.Values;

public class FrontEndMain extends JFrame 
{
	public static Key key 							= null;
	private JMenu file 								= null;
	public static long id							= 0;
	public static JTextArea info 					= null;
	public static Lobby lobby						= null;
	public static GameOver gameover					= null;
	private boolean updated							= false;
	public static Font title_font					= null;
	public static long GAME_TICKS					= 0;
	public static long START_GAME_TICKS				= 0;
	public static String host						= "";
	public static InetAddress host_ip				= null;
	public static int host_port						= 0;
	private static Thread tKey						= null;
	
	public FrontEndMain()
	{
		super("Barrage Front End");
		Values.FRONT_END = true;
		Values.physics = true;
		
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setBounds(0,0,Values.SCR_WIDTH,Values.SCR_HEIGHT);
		this.setLocationRelativeTo(null);
		this.setLayout(new BorderLayout());
		this.setVisible(true);
		this.setFocusable(true);
		
		try 
		{
			Main.log = new PrintWriter(new FileWriter("log.log",true));
			//Load font
			title_font = Font.createFont(Font.TRUETYPE_FONT, new File("./fonts/julius.ttf"));
				
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		    ge.registerFont(title_font);
		 
		    Main.log.println("Canvas font file loaded.");
		    Main.log.flush();
		} 
		catch (FileNotFoundException e) 
		{
			//System.exit(0);
			Main.log.println(e.getMessage());
		}
		catch (FontFormatException e) 
		{
			Main.log.println(e.getMessage());
		}
		catch (IOException e) 
		{
			Main.log.println(e.getMessage());
		}
		
		this.addWindowListener(new WindowAdapter() 
		{
			@Override
			public void windowClosing(WindowEvent arg0) 
			{
				super.windowClosing(arg0);
				
				if(!FrontEndMain.key.getCanvas().getActors().isEmpty())//if there are actors send the quit command
				{
					String msg = "QUIT\t" + FrontEndMain.key.getCanvas().getActorInstance().getUsername();
					try 
					{
						DatagramPacket snd;
						for(Actor a:FrontEndMain.key.getCanvas().getActors())
						{
							snd = new DatagramPacket(msg.getBytes(),msg.getBytes().length,a.getIP(),a.getPort());//FrontEndMain.host_ip,FrontEndMain.host_port);
							FrontEndMain.lobby.getServerInstance().send(snd);
						}
					}
					catch (IOException e1) 
					{
						if(Lobby.info!=null)
							Lobby.info.append("IOException: " + e1.getMessage());
						Main.log.println("IOException: " + e1.getMessage());
						Main.log.flush();
					}
				}
				if(!FrontEndMain.lobby.getServerInstance().isClosed())
					FrontEndMain.lobby.getServerInstance().close();
			}
		});
		//
		/*tKey = new Thread(new Runnable()
		{
			
			@Override
			public void run() 
			{
				javax.swing.Timer t = new javax.swing.Timer(20,key);
				t.start();
			}
		});*/
		
		//Add local player first
		key = new Key();
		lobby = new Lobby();
		gameover = new GameOver();
		this.add(gameover);
		
		//Canvas refresh thread
		Thread t_canv = new Thread(new Runnable() 
		{
			
			@Override
			public void run() 
			{
				while(true)
				{
					//if(!lobby.isVisible() && !updated)
					if(Values.showlobby && !Values.updated)
					{
						//START_GAME_TICKS = GAME_TICKS;
						removeComponent(key.getCanvas());
						removeComponent(gameover);
						addComponent(lobby);
						
						repaintFrame();
						tKey = null;
					}
					
					if(Values.showgameover && !Values.updated)
					{
						//START_GAME_TICKS = GAME_TICKS;
						removeComponent(key.getCanvas());
						removeComponent(lobby);
						addComponent(gameover);
						
						repaintFrame();
						tKey = null;
					}
					
					if(Values.showcanvas && !Values.updated)
					{
						tKey = new Thread(new Runnable()
						{
							
							@Override
							public void run() 
							{
								javax.swing.Timer t = new javax.swing.Timer(20,key);
								t.start();
							}
						});
						
						START_GAME_TICKS = GAME_TICKS;
						removeComponent(lobby);
						removeComponent(gameover);
						addComponent(key.getCanvas());
						
						try 
						{
							//Deserialize game level from disk
							ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File("./levels/level01.blvl")));
							@SuppressWarnings("unchecked")
							ArrayList<Block> blocks = (ArrayList)ois.readObject();
							key.getCanvas().setBlocks(blocks);
							ois.close();
						} 
						catch (IOException e) 
						{
							JOptionPane.showMessageDialog(null, "Could not open level: " + e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
						} 
						catch (ClassNotFoundException e) 
						{
							JOptionPane.showMessageDialog(null, "Could not open level: " + e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
						}
						
						
						repaintFrame();
						Values.updated = true;
						setFocusHere();
						tKey.start();
					}
					try
					{
						Thread.sleep(5);
					}
					catch(InterruptedException e)
					{
						
					}
					key.getCanvas().repaint();
				}
			}
		});
		t_canv.start();
		
		//Screen size update timer
		javax.swing.Timer scr = new javax.swing.Timer(1,new ActionListener()
		{
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				//GAME_TICKS = GAME_TICKS + 4;
				//Refresh screen size
				Values.SCR_WIDTH = getContentPane().getWidth();//Toolkit.getDefaultToolkit().getScreenSize().width;
				Values.SCR_HEIGHT =getContentPane().getHeight();// Toolkit.getDefaultToolkit().getScreenSize().height;
				Values.canvas_width = Values.SCR_WIDTH;
				Values.canvas_height = Values.SCR_HEIGHT;
			}
		});
		scr.start();
		
		Timer ticks = new Timer();
		ticks.scheduleAtFixedRate(new TimerTask()
		{
			
			@Override
			public void run() 
			{
				GAME_TICKS = GAME_TICKS + 1;
			}
		}, 0, 1);
	}
		
	private void setFocusHere()
	{
		this.requestFocusInWindow();
	}
	
	public void removeComponent(Component c)
	{
		this.remove(c);
	}
	
	public void addComponent(Component c)
	{
		this.add(c);
	}
	
	public void repaintFrame()
	{
		this.revalidate();
		this.repaint();
	}
	
	public static void main(String[] args) 
	{
		FrontEndMain m = new FrontEndMain();
		m.setVisible(true);
		m.addKeyListener(key);
	}

}
