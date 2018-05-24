package GUI;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;

import GameFrontEnd.FrontEndMain;
import GameFrontEnd.Lobby;
import Model.Actor;
import States.Values;

public class GameOver extends JPanel 
{//out.write("GET / HTTP/1.1.\r\n");
	//out.flush();
	private Font font = null;
	private JLabel barrage 					= new JLabel("Barrage");
	private JLabel results 					= new JLabel("*Game Over - Results*");
	private JLabel servers 					= new JLabel("Server Name: ");
	private static JLabel winner 					= new JLabel("Winner : ");
	private JButton exit					= null;
	private JButton lobby					= null;
	private JTable table					= null;
	private JScrollPane scroll				= null;
	public static JTextArea info 			= null;
	private GridBagConstraints gbc = new GridBagConstraints();
	private Object[][] data					= null;
	private String[] columns 				= {"Username","Score"};
	
	public GameOver()
	{
		try 
		{
			font = Font.createFont(Font.TRUETYPE_FONT, new File("./fonts/Blackout-Midnight.ttf"));
			
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		    ge.registerFont(font);
		    
		    Main.log.println("Blackout font loaded.");
		} 
		catch (FileNotFoundException e)
		{
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
		GridBagLayout gbl = new GridBagLayout(); 
		this.setLayout(gbl);
		
		servers.setText("Server: " + Lobby.name);
		exit = new JButton("Exit Game");
		lobby = new JButton("Go to lobby");
		info = new JTextArea();
		initUI();
		initHandlers();
	}
	
	public static void setWinner(String txt)
	{
		winner.setText(txt);
		winner.repaint();
	}
	
	public void addComponent(int x,int y,int w,int h,GridBagConstraints gbc,Component c,int fontsize)
	{
		gbc.gridwidth = w;
		gbc.gridheight = h;
		/*gbc.weightx = wx;
		gbc.weighty = wy;*/
		gbc.gridx = x;
		gbc.gridy = y;
		c.setFont(new Font("Blackout",Font.PLAIN,fontsize));
		this.add(c,gbc);
	}
	
	public void addData(String usr, String score)
	{
		Object[][] temp = new Object[data.length+1][2];
		//Make backup
		for(int row=0;row<data.length;row++)
		{
			for(int col=0;col<2;col++)
			{
				temp[row][col] = data[row][col];
			}
		}
		temp[data.length][0] = usr;
		//temp[data.length][1] = ip;
		temp[data.length][1] = score;
		//Copy back to actual array
		data = new Object[temp.length][2];
		for(int row=0;row<temp.length;row++)
		{
			for(int col=0;col<2;col++)
			{
				data[row][col] = temp[row][col];
			}
		}
	}
	
	public void refreshList()
	{
		data = new Object[0][2];
		for(Actor a:FrontEndMain.key.getCanvas().getActors())
		{
			addData(a.getUsername() + (a.getUsername().equals(FrontEndMain.host)?"[Host]":""), String.valueOf(a.getScore()));
		}
		servers.setText("Server: " + Lobby.name);//FrontEndMain.host);
		DefaultTableModel model = new DefaultTableModel(data,columns);
		table.setModel(model);
		
		/*if(Values.showgameover)
		{
			
			//FrontEndMain.lobby.server_tcp. 
		}*/
	}
	
	public void echo(String msg,boolean err)
	{
		if(err)
			info.append("[ERROR]"+msg + "\n");
		else
			info.append(msg + "\n");
	}
	
	public void initHandlers()
	{
		exit.addActionListener(new ActionListener()
		{
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
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
						echo("IOException: " + e1.getMessage(),true);
						Main.log.println("IOException: " + e1.getMessage());
						Main.log.flush();
					}
				}
				//FrontEndMain.lobby.getServerInstance().close();
				System.exit(0);
			}
		});
	
		lobby.addActionListener(new ActionListener()
		{
			
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				Values.showlobby = true;
				Values.showcanvas = false;
				Values.showgameover = false;
				Values.updated = false;
			}
		});
	}
	
	public void initUI()
	{
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1;
		gbc.weighty = 1;
		
		barrage.setPreferredSize(new Dimension(400,50));
		addComponent(1, 0, 5, 2, gbc, barrage,80);
		results.setForeground(Color.CYAN);
		results.setPreferredSize(new Dimension(400,20));
		addComponent(1, 2, 5, 2, gbc, results,30);
		//servers.setPreferredSize(new Dimension(400,5));
		servers.setForeground(Color.RED);
		addComponent(1, 4, 1, 1, gbc, servers,26);
		
		winner.setForeground(Color.GREEN);
		addComponent(1, 5, 1, 1, gbc, winner,26);
		
		data = new Object[0][3];
		table = new JTable(data,columns);
		//table.setPreferredSize(new Dimension(400,10));
		scroll = new JScrollPane(table);
		scroll.setPreferredSize(new Dimension(400,200));
		addComponent(0, 6, 5, 1,gbc, scroll,12);
		
		refreshList();
		
		//info.setPreferredSize(new Dimension(400,100));
		info.setBackground(Color.BLACK);
		info.setForeground(Color.GREEN);
		info.setEditable(false);
		JScrollPane info_scroll = new JScrollPane(info);
		info_scroll.setPreferredSize(new Dimension(400,100));
		addComponent(0, 7, 5, 2,gbc, info_scroll,12);
		
		lobby.setPreferredSize(new Dimension(300,20));
		addComponent(0, 12, 1, 1, gbc, lobby,36);
		
		exit.setPreferredSize(new Dimension(300,20));
		addComponent(4, 12, 1, 1, gbc, exit,26);
		
	}
}
