package GUI;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import Model.Actor;
import Model.Block;
import States.Key;
import States.Values;


public class Main extends JFrame 
{	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1063976636815474012L;
	
	private static Key key 							= new Key();
	private JMenuBar bar 							= null;
	private JMenu file 								= null;
	public static long id							= 0;
	private int x									= 0;
	private int y									= 0;
	public static JTextArea info 					= null;
	private static boolean dragging					= false;
	private static boolean moving					= false;
	private static int startx							= 0;
	private static int starty							= 0;
	private static double dragx						= 0;
	private static double dragy						= 0;
	public static PrintWriter log 					= null;
	
	private void setFocusHere()
	{
		this.requestFocusInWindow();
	}
	
	public Main() throws MalformedURLException
	{
		super("Platformer Level Editor");
		try 
		{
			log = new PrintWriter("log.log");
		} 
		catch (FileNotFoundException e1) 
		{
			System.exit(0);
			e1.printStackTrace();
		}
		try 
		{
			UIManager.setLookAndFeel(javax.swing.plaf.nimbus.NimbusLookAndFeel.class.getName());
		} 
		catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e1) 
		{
			e1.printStackTrace();
		}
		//Add local player first
		try 
		{
			key.getCanvas().getActors().add(new Actor(350,10,Values.ACTOR_SIZE,Values.ACTOR_SIZE,10,InetAddress.getByName("localhost"),42,"Caspr"));
		}
		catch (UnknownHostException e1) 
		{
			//e1.printStackTrace();
			JOptionPane.showMessageDialog(null,"Could not open port on specified IP: " + e1.getMessage(), "UnknownHostException",JOptionPane.ERROR_MESSAGE);
		}
		
		info = new JTextArea();
		LeftPane left = new LeftPane(key);
		PropertiesPane properties = new PropertiesPane(key);
		TopPane top = new TopPane(info);
		
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setBounds(0,0,Values.SCR_WIDTH,Values.SCR_HEIGHT);
		this.setLocationRelativeTo(null);
		this.setLayout(new BorderLayout());
		this.setVisible(true);
		this.setFocusable(true);
		
		
		Thread t_canv = new Thread(new Runnable() 
		{
			
			@Override
			public void run() 
			{
				while(true)
				{
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
		
		javax.swing.Timer scr = new javax.swing.Timer(1,new ActionListener()
		{
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				
				//Refresh screen size
				Values.SCR_WIDTH = getContentPane().getWidth();//Toolkit.getDefaultToolkit().getScreenSize().width;
				Values.SCR_HEIGHT =getContentPane().getHeight();// Toolkit.getDefaultToolkit().getScreenSize().height;
				Values.canvas_width = Values.SCR_WIDTH - Values.PROP_W - Values.LEFT_W;
				Values.canvas_height = Values.SCR_HEIGHT - Values.TOP_H; 
			}
		});
		scr.start();
		
		this.addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusLost(FocusEvent e) 
			{
				super.focusLost(e);
				Values.canvas_focused = false;
			}
			
			@Override
			public void focusGained(FocusEvent e) 
			{
				super.focusGained(e);
				Values.canvas_focused = true;
			}
		});
		
		key.getCanvas().addMouseWheelListener(new MouseWheelListener()
		{
			
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) 
			{
				if(e.getWheelRotation()<0)
				{
					Values.SCALE += 0.1;
				}
				else
				{
					Values.SCALE -= 0.1;
				}
			}
		});
		
		key.getCanvas().addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e) 
			{
				super.mouseClicked(e);
				setFocusHere();
				for(Block b:key.getCanvas().getBlocks())
				{
					Rectangle r = new Rectangle();
					r.setBounds((int)e.getPoint().getX(), (int)e.getPoint().getY(), 1, 1);
					r.x = r.x + (Values.canvas_width - Values.zoomWidth);
					r.y = r.y + (Values.canvas_height - Values.zoomHeight);
					if(r.intersects(b.getBounds(0, 0)))
					{
						if(b.isMarked())
							b.setMarked(false);
						else
							b.setMarked(true);
					}
				}
				
				/*Values.dir_x = e.getX();
				Values.dir_y = e.getY();
				Values.shoot = true;*/
			}
			
			@Override
			public void mousePressed(MouseEvent e) 
			{
				super.mouseClicked(e);
				startx = e.getX();
				starty = e.getY();
			}
			
			@Override
			public void mouseReleased(MouseEvent arg0) 
			{
				super.mouseReleased(arg0);
				startx = 0;
				starty = 0;
			}
			
			@Override
			public void mouseDragged(MouseEvent e) 
			{
				super.mouseDragged(e);
				double dragdx = e.getPoint().getX()-startx;
				double dragdy = e.getPoint().getY()-starty;
				
				for(Block b:key.getCanvas().getBlocks())
				{
					if(dragx-e.getX()<0)//change was negative
					{
						b.setX(b.getX()+2);
					}
					else
					{
						b.setX(b.getX()-2);
					}
					
					if(dragy-e.getY()<0)
					{
						b.setY(b.getY()+2);
					}
					else
					{
						b.setY(b.getY()-2);
					}
				}
				dragx = e.getPoint().getX();
				dragy = e.getPoint().getY();
			}
		});
		
		bar = new JMenuBar();
		file = new JMenu("File");
		JMenu edit = new JMenu("Edit");
		
		JMenuItem exit = new JMenuItem("Exit");
		exit.addActionListener(new ActionListener()
		{
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				System.exit(0);
			}
		});
		
		JMenuItem rigid = new JMenuItem("Apply rigid body to selected blocks");
		rigid.addActionListener(new ActionListener()
		{
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				for(Block b: key.getCanvas().getBlocks())
					if(b.isMarked())
					{
						if(b.isRigid())
							b.setRigid(false);
						else
							b.setRigid(true);
					}
				
				//notif.setText("Applied rigid body to selected blocks");
				JOptionPane.showMessageDialog(null, "Applied rigid body to selected blocks","Rigid application Success",JOptionPane.INFORMATION_MESSAGE);
			}
		});
		
		JMenuItem spawn = new JMenuItem("Set spawn point to selected blocks");
		spawn.addActionListener(new ActionListener()
		{
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				for(Block b: key.getCanvas().getBlocks())
				{
					if(b.isMarked())
					{
						if(b.isSpawnpoint())
							b.setSpawnpoint(false);
						else
							b.setSpawnpoint(true);
					}
				}
			}
		});
		
		JMenuItem save = new JMenuItem("Save Level");
		save.addActionListener(new ActionListener()
		{
			
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				try 
				{
					ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File("./levels/level01.blvl")));
					oos.writeObject(key.getCanvas().getBlocks());
					oos.flush();
					oos.close();
					
					JOptionPane.showMessageDialog(null, "Game level saved to disk - ./levels/level01.blvl","Success",JOptionPane.INFORMATION_MESSAGE);
				}
				catch (IOException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		JMenuItem open = new JMenuItem("Open Level");
		open.addActionListener(new ActionListener()
		{
			
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				try 
				{
					ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File("./levels/level01.blvl")));
					@SuppressWarnings("unchecked")
					ArrayList<Block> blocks = (ArrayList)ois.readObject();
					key.getCanvas().setBlocks(blocks);
					ois.close();
					JOptionPane.showMessageDialog(null, "Game level opened","Success",JOptionPane.INFORMATION_MESSAGE);
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				} catch (ClassNotFoundException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		file.add(save);
		file.add(open);
		file.add(rigid);
		file.add(spawn);
		file.add(exit);
		
		bar.add(file);
		bar.add(edit);
		this.setJMenuBar(bar);
		
		add(properties,BorderLayout.EAST);
		add(key.getCanvas(),BorderLayout.CENTER);
		add(top,BorderLayout.NORTH);
		add(left,BorderLayout.WEST);
		
	}
	
	public static void main(String[] args)
	{
		Main m = null;
		try
		{
			m = new Main();
			m.setVisible(true);
			m.addKeyListener(key);
			Thread tKey	= new Thread(() ->
                                        {
                                            javax.swing.Timer t = new javax.swing.Timer(20,key);
                                            t.start();
                                        });
			tKey.start();
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
	}

}

