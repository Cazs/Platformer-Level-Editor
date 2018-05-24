package GameFrontEnd;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import GUI.GameOver;
import GUI.Main;
import Model.Actor;
import Model.Bullet;
import States.Values;

public class Lobby extends JPanel 
{
	private Font font = null;
	private JLabel barrage 					= new JLabel("Barrage");
	private JLabel lobby 					= new JLabel("Game Lobby");
	private JLabel servers 					= new JLabel("Server Name: ");
	private ArrayList<String> users 		= null;
	private JButton host					= null;
	private JButton connect					= null;
	private JButton start					= null;
	private JTable table					= null;
	private JScrollPane scroll				= null;
	/*private JTextField svr_name			= null;
	private JTextField svr_ip				= null;*/
	public static JTextArea info 			= null;
	private GridBagConstraints gbc = new GridBagConstraints();
	private final int SVR_PORT				= 42;
	private DatagramSocket server			= null;
	private Object[][] data					= null;
	private String[] columns 				= {"Username","Status"};
	public static String name				= "";
	private DatagramPacket rcv				= null;
	private String username					= "";
	public int port						= 0;
	public ServerSocket server_tcp			= null;
	
	public Lobby()
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
		
		users = new ArrayList<String>();
		host = new JButton("Host Game");
		connect = new JButton("Connect to server");
		start = new JButton("Start Game");
		info = new JTextArea();
		
		try 
		{
			port = Integer.valueOf(JOptionPane.showInputDialog(null,"Enter the port number for the server to run on NOTE: Server/Host must run on 42 and clients can run on any other port number","Enter port number",JOptionPane.INFORMATION_MESSAGE));
			server = new DatagramSocket(port/*SVR_PORT*/);
			server_tcp = new ServerSocket(port);//25
			Thread t = new Thread(new Runnable()
			{
				
				@Override
				public void run() 
				{
					
					try 
					{
						BufferedReader in = null;
						Socket client = server_tcp.accept();
						while(true)
						{
							PrintWriter out = new PrintWriter(client.getOutputStream());
							in = new BufferedReader(new InputStreamReader(client.getInputStream()));
							String msg = in.readLine();
							if(msg!=null)
							{
								StringTokenizer t = new StringTokenizer(msg);
								String cmd = t.nextToken();
								System.out.println("CMD: " + cmd);
								switch(cmd)
								{
									case "HELO":
										out.println("250 HELLO");
										out.flush();
										break;
									case "MAIL":
										out.println("250 OK");
										out.flush();
										break;
									case "RCPT":
										out.println("250 OK");
										out.flush();
										break;
									case "DATA":
										String winner = in.readLine();
										GameOver.setWinner("Winner: " + winner);
										out.println("250 OK");
										out.flush();
									case "QUIT":
										out.println("221 Bye");
										out.flush();
										//client.close();
										return;
								}
							}
						}
					}
					catch (IOException e) 
					{
						e.printStackTrace();
					}
				}
			});
			t.start();
		}
		catch (SocketException e) 
		{
			JOptionPane.showMessageDialog(null,e.getMessage(), "SocketException",JOptionPane.ERROR_MESSAGE);
			System.exit(-1);
		}
		catch (IOException e) 
		{
			Main.log.println("Lobby IOException: " + e.getMessage());
		}
		initUI();
		initHandlers();
	}
	
	public DatagramSocket getServerInstance(){return server;}
	
	public void addData(String usr, String stat)
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
		temp[data.length][1] = stat;
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
	
	private void refreshList()
	{
		data = new Object[0][2];
		//echo("Refreshing list..",false);
		for(Actor a:FrontEndMain.key.getCanvas().getActors())
		{
			echo("Added player to list",false);
			addData(a.getUsername() + (a.getUsername().equals(FrontEndMain.host)?"[Host]":""), a.getReady()?"Ready":"Not Ready");
			//addData(a.getUsername() + (a.getIP()==FrontEndMain.host_ip?"[Host]":""), a.getIP().toString(), a.getReady()?"Ready":"Not Ready");
			//echo("Host_ip:" + FrontEndMain.host_ip + " A_IP: " + a.getIP(),false);
		}
		
		DefaultTableModel model = new DefaultTableModel(data,columns);
		table.setModel(model);
		
		//table.setPreferredSize(new Dimension(400,600));
	}
	
	public void initUI()
	{
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1;
		gbc.weighty = 1;
		
		barrage.setPreferredSize(new Dimension(400,50));
		addComponent(1, 0, 5, 2, gbc, barrage,80);
		lobby.setPreferredSize(new Dimension(100,30));
		addComponent(1, 2, 1, 2, gbc, lobby,56);
		//servers.setPreferredSize(new Dimension(400,5));
		servers.setForeground(Color.RED);
		addComponent(1, 4, 1, 1, gbc, servers,26);
		
		data = new Object[0][3];
		table = new JTable(data,columns);
		//table.setPreferredSize(new Dimension(400,10));
		scroll = new JScrollPane(table);
		scroll.setPreferredSize(new Dimension(400,200));
		addComponent(0, 5, 5, 1,gbc, scroll,12);
		
		refreshList();
		
		//info.setPreferredSize(new Dimension(400,100));
		info.setBackground(Color.BLACK);
		info.setForeground(Color.GREEN);
		info.setEditable(false);
		JScrollPane info_scroll = new JScrollPane(info);
		info_scroll.setPreferredSize(new Dimension(400,100));
		addComponent(0, 6, 5, 2,gbc, info_scroll,12);
		
		host.setPreferredSize(new Dimension(300,20));
		addComponent(0, 11, 1, 1, gbc, host,36);
		
		connect.setPreferredSize(new Dimension(300,20));
		addComponent(4, 11, 1, 1, gbc, connect,26);
		
		start.setPreferredSize(new Dimension(300,20));
		addComponent(1, 11, 1, 1, gbc, start,26);
	}
	
	public void hideThis(){this.setVisible(false);}
	
	public void initHandlers()
	{
		start.addActionListener(new ActionListener()
		{
			
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				int numActors = FrontEndMain.key.getCanvas().getActors().size();
				if(numActors == 0)
				{
					JOptionPane.showMessageDialog(null, "No players in session, please host a game first","No users in session",JOptionPane.ERROR_MESSAGE);
				}
				else
				{
					//Clear bullets, reset scores & HP
					FrontEndMain.key.getCanvas().getBullets().clear();
					FrontEndMain.key.getCanvas().resetActors();
					
					//Send start command to everyone else
					DatagramPacket snd;
					for(Actor a:FrontEndMain.key.getCanvas().getActors())
					{
						String msg = "START\t"+FrontEndMain.GAME_TICKS+100; //+100 to compensate for any delay
						snd = new DatagramPacket(msg.getBytes(), msg.getBytes().length,a.getIP(),a.getPort());
						try 
						{
							server.send(snd);
						}
						catch (IOException e) 
						{
							Main.log.println("IOException: " + e.getMessage());
							Main.log.flush();
							JOptionPane.showMessageDialog(null, "Error sending start command to clients, check logs","Error starting game",JOptionPane.ERROR_MESSAGE);
						}
					}
					//hideThis();
					Values.showgameover = false;
					Values.showlobby = false;
					Values.showcanvas = true;
					Values.updated = false;
				}
			}
		});
		
		host.addActionListener(new ActionListener()
		{
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				name = JOptionPane.showInputDialog(null,"Enter Server Name");
				final String username = JOptionPane.showInputDialog(null,"Enter username for the session");
				
				if(name != null && username != null && !name.isEmpty() && !username.isEmpty())
				{
					servers.setText("Servers: " + name);
					servers.setForeground(Color.GREEN);
					Thread clientListener = new Thread(new Runnable() 
					{
						
						@Override
						public void run() 
						{
							byte[] arr = new byte[256];
							DatagramPacket rcv = new DatagramPacket(arr, arr.length);
							try 
							{
								//Add host
								FrontEndMain.key.getCanvas().getActors().add(new Actor(350,10,Values.ACTOR_SIZE,Values.ACTOR_SIZE,10,InetAddress.getByName("localhost"),port,username));
								FrontEndMain.key.getCanvas().getActorInstance().setReady(true);//Hosting player is set to ready automatically
								FrontEndMain.host = username; 
								refreshList();
								FrontEndMain.host_ip = InetAddress.getByName("localhost");
								FrontEndMain.host_port = port;//SVR_PORT;
								
								echo("Server running, waiting for clients...",false);
								while(true)
								{
									server.receive(rcv);
									//echo("Packet recieved..",false);
									
									String msg = new String(rcv.getData(),0,rcv.getLength());
									if(msg.contains("JOIN REQ"))
									{
										//Client wants to join
										sendResponse("OK 100\t" + name,rcv);
										echo("JOIN REQ Recieved.",false);
									}
									
									if(msg.contains("UPDT_SCR"))
									{
										StringTokenizer toke = new StringTokenizer(msg,"\t");
										toke.nextToken();//CMD i.e. DETAILS etc
										String usr = toke.nextToken();
										double score = Double.valueOf(toke.nextToken());
										for(Actor a:FrontEndMain.key.getCanvas().getActors())
										{
											if(a.getUsername().equals(usr))
											{
												a.setScore(score);
											}
										}
									}
									
									if(msg.contains("DETAILS"))
									{
										echo("User DETAILS Recieved.",false);
										StringTokenizer toke = new StringTokenizer(msg,"\t");
										toke.nextToken();//CMD i.e. DETAILS etc
										String usr = toke.nextToken();
										boolean exists = false;
										for(Actor a:FrontEndMain.key.getCanvas().getActors())
										{
											if(a.getUsername().equals(usr))
											{
												sendResponse("ERR_USR_EXISTS 250",rcv);
												exists = true;
											}
										}
										if(!exists)
										{
											int x = Integer.valueOf(toke.nextToken());
											int y = Integer.valueOf(toke.nextToken());
											FrontEndMain.key.getCanvas().getActors().add(new Actor(x, y, Values.ACTOR_SIZE,Values.ACTOR_SIZE, 10, rcv.getAddress(), rcv.getPort(), usr));
											refreshList();
											echo(usr + " has joined the game",false);
											
											//Send requesting user details back
											sendResponse("DETAILS\t"+usr+"\t"+x+"\t"+y,rcv);
											
											//sendResponse("STAT\t"+usr+"\t"+"READY",rcv);
											//Tell requesting user the host's name
											sendResponse("HOST\t"+username,rcv);
											//Send details of other players, host included
											for(Actor a:FrontEndMain.key.getCanvas().getActors())
											{
												if(!a.getUsername().equals(usr))//dont send the current user's details & status
												{
													sendResponse("DETAILS\t"+ a.getUsername() +"\t"+ (int)a.getX() +"\t"+ (int)a.getY() ,rcv);
													sendResponse("STAT\t"+ a.getUsername() +"\t"+(a.getReady()?"READY":"!READY"),rcv);
												}
											}
										}
									}
									
									if(msg.contains("ADD_BULT"))
									{
										StringTokenizer toke = new StringTokenizer(msg,"\t");
										toke.nextToken();//CMD i.e. STAT etc
										double x = Double.valueOf(toke.nextToken());
										double y = Double.valueOf(toke.nextToken());
										int w = Integer.valueOf(toke.nextToken());
										int h = Integer.valueOf(toke.nextToken());
										double weight = Double.valueOf(toke.nextToken());
										int dir = Integer.valueOf(toke.nextToken());
										long id = Long.valueOf(toke.nextToken());
										String sender = toke.nextToken();
										FrontEndMain.key.getCanvas().addBullet(new Bullet(x, y, w, h, weight, dir, id, sender));
									}
									
									if(msg.contains("QUIT"))
									{
										StringTokenizer toke = new StringTokenizer(msg,"\t");
										toke.nextToken();//CMD i.e. STAT etc
										String usr = toke.nextToken();
										echo(usr + " has left.",false);
										Actor a = null;
										Iterator iterator = FrontEndMain.key.getCanvas().getActors().iterator();
										while(iterator.hasNext())
										{
											a = (Actor) iterator.next();
											if(a != null)
											{
												if(a.getUsername().equals(usr))
												{
													//print message to say who has left
													iterator.remove();
													break;
												}
											}
										}
									}
									
									if(msg.contains("REM_BULT"))
									{
										StringTokenizer toke = new StringTokenizer(msg,"\t");
										toke.nextToken();//CMD i.e. STAT etc
										long id = Long.valueOf(toke.nextToken());
										Bullet b = null;
										Iterator iterator = FrontEndMain.key.getCanvas().getBullets().iterator();
										while(iterator.hasNext())
										{
											b = (Bullet) iterator.next();
											if(b != null)
											{
												if(b.getID()==id)
												{
													iterator.remove();
													break;
												}
											}
										}
									}
									
									if(msg.contains("STAT"))
									{
										StringTokenizer toke = new StringTokenizer(msg,"\t");
										toke.nextToken();//CMD i.e. STAT etc
										String usr = toke.nextToken();
										String stat = toke.nextToken();
										echo("Username: " + usr + ", STAT: " + stat,false);
										if(stat.equals("READY"))
										{
											for(Actor a:FrontEndMain.key.getCanvas().getActors())
												if(a.getUsername().equals(usr))
													a.setReady(true);
										}
										else if(stat.equals("!READY"))
										{
											for(Actor a:FrontEndMain.key.getCanvas().getActors())
												if(a.getUsername().equals(usr))
													a.setReady(false);
										}
										refreshList();
										sendResponse("OK 300",rcv);
									}
									
									if(msg.contains("MOVE"))
									{
										StringTokenizer toke = new StringTokenizer(msg,"\t");
										toke.nextToken();//CMD i.e. STAT etc
										String usr = toke.nextToken();
										int x = Integer.valueOf(toke.nextToken());
										int y = Integer.valueOf(toke.nextToken());
										echo("MOVE " + usr + ", x: " + x + ", y: " + y,false);
										for(Actor a:FrontEndMain.key.getCanvas().getActors())
										{
											if(a.getUsername().equals(usr))
											{
												a.setX(x);
												a.setY(y);
											}
										}
										sendResponse("OK 400",rcv);
									}
								}
							} 
							catch (IOException e) 
							{
								//e.printStackTrace();
								JOptionPane.showMessageDialog(null,e.getMessage(), "IOException",JOptionPane.ERROR_MESSAGE);
							}
						}
					});
					clientListener.start();
				}
				else
				{
					JOptionPane.showMessageDialog(null,"Server name or username can't be empty", "Error",JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		connect.addActionListener(new ActionListener()
		{
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				String ip = JOptionPane.showInputDialog(null,"Enter Hosting Server IP Address");
				if(ip!=null && !ip.isEmpty())
				{
					try 
					{
						byte[] rec = new byte[512];
						rcv = new DatagramPacket(rec,rec.length);
						
						InetAddress host_ip = InetAddress.getByName(ip);
						int prt = 42;
						
						//final DatagramSocket server = new DatagramSocket(41);
						String req = "JOIN REQ";
						DatagramPacket snd = new DatagramPacket(req.getBytes(),req.getBytes().length,host_ip,prt);
						server.send(snd);
						
						server.receive(rcv);
						String msg = new String(rcv.getData(),0,rcv.getLength());
						if(msg.contains("OK 100"))
						{
							StringTokenizer toke = new StringTokenizer(msg,"\t");
							toke.nextToken();//CMD i.e. OK 100 etc
							name = toke.nextToken(); 
							servers.setText("Servers: " + name);
							servers.setForeground(Color.GREEN);
							start.setEnabled(false);
							
							FrontEndMain.host_ip = host_ip;
							FrontEndMain.host_port = prt;
							
							username = JOptionPane.showInputDialog(null,"Enter username for the session");
							if(username!=null && username.isEmpty())
							{
								JOptionPane.showMessageDialog(null,"Username can't be empty", "Error",JOptionPane.ERROR_MESSAGE);
								return;
							}
							
							//req = "DETAILS\t"+username+"\t"+(int)FrontEndMain.key.getCanvas().getActorInstance().getX()+"\t"+(int)FrontEndMain.key.getCanvas().getActorInstance().getY();
							req = "DETAILS\t"+username+"\t"+0+"\t"+0;
							snd = new DatagramPacket(req.getBytes(),req.getBytes().length,host_ip,prt);
							server.send(snd);
							
							//rcv = new DatagramPacket(rec,rec.length);
							/*server.receive(rcv);
							msg = new String(rcv.getData(),0,rcv.getLength());
							
							if(msg.contains("ERR_USR_EXISTS"))
							{
								JOptionPane.showMessageDialog(null,"Username already exists, try again with a different username", "Error",JOptionPane.ERROR_MESSAGE);
								return;
							}
							else
							{
								if(msg.contains("DETAILS"))
								{
									toke = new StringTokenizer(msg,"\t");
									toke.nextToken();//CMD i.e. DETAILS etc
									String usr = toke.nextToken();
									
									int x = Integer.valueOf(toke.nextToken());
									int y = Integer.valueOf(toke.nextToken());
									FrontEndMain.key.getCanvas().getActors().add(new Actor(x, y, Values.ACTOR_SIZE,Values.ACTOR_SIZE, 10, rcv.getAddress(), rcv.getPort(), usr));
									refreshList();
									echo(usr + " has joined the game",false);
									System.out.println("Received details");
									//sendResponse("OK 200",rcv);
								}
							}
							//Send status update to server
							req = "STAT\t" + username + "\tREADY";
							snd = new DatagramPacket(req.getBytes(),req.getBytes().length,host_ip,port);
							server.send(snd);
							//Set this user to ready after sending the ready command
							FrontEndMain.key.getCanvas().getActorInstance().setReady(true);
							refreshList();*/
						}
						
		
						Thread listener = new Thread(new Runnable()
						{
							
							@Override
							public void run() 
							{
								while(true)
								{
									try 
									{
										if(server.isClosed() || !server.isBound())
											return;
											server.receive(rcv);
										//echo("Client>_:Packet received",false);
										String msg = new String(rcv.getData(),0,rcv.getLength());
										
										if(msg.contains("ERR_USR_EXISTS"))
										{
											JOptionPane.showMessageDialog(null,"Username already exists, try again with a different username", "Error",JOptionPane.ERROR_MESSAGE);
											return;
										}
										
										if(msg.contains("HOST"))
										{
											echo(msg,true);
											StringTokenizer toke = new StringTokenizer(msg,"\t");
											toke.nextToken();//CMD i.e. DETAILS,HOST etc
											String usr = toke.nextToken();
											FrontEndMain.host = usr;
											refreshList();
										}
										
										if(msg.contains("UPDT_SCR"))
										{
											StringTokenizer toke = new StringTokenizer(msg,"\t");
											toke.nextToken();//CMD i.e. DETAILS etc
											String usr = toke.nextToken();
											double score = Double.valueOf(toke.nextToken());
											for(Actor a:FrontEndMain.key.getCanvas().getActors())
											{
												if(a.getUsername().equals(usr))
												{
													a.setScore(score);
												}
											}
										}
										
										if(msg.contains("DETAILS"))
										{
											StringTokenizer toke = new StringTokenizer(msg,"\t");
											toke.nextToken();//CMD i.e. DETAILS etc
											String usr = toke.nextToken();
											
											int x = Integer.valueOf(toke.nextToken());
											int y = Integer.valueOf(toke.nextToken());
											FrontEndMain.key.getCanvas().getActors().add(new Actor(x, y, Values.ACTOR_SIZE,Values.ACTOR_SIZE, 10, rcv.getAddress(), rcv.getPort(), usr));
											refreshList();
											echo(usr + " has joined the game",false);
											
											if(usr.equals(username))
											{
												String req = "STAT\t" + username + "\tREADY";
												DatagramPacket snd = new DatagramPacket(req.getBytes(),req.getBytes().length,rcv.getAddress(),rcv.getPort());
												server.send(snd);
												//Set this user to ready after sending the ready command
												FrontEndMain.key.getCanvas().getActorInstance().setReady(true);
												refreshList();
											}
											//sendResponse("OK 200",rcv);
										}
										
										if(msg.contains("START"))
										{
											StringTokenizer toke = new StringTokenizer(msg,"\t");
											toke.nextToken();
											//hideThis();
											FrontEndMain.GAME_TICKS = Long.valueOf(toke.nextToken());
											Values.showlobby = false;
											Values.showcanvas = true;
											Values.showgameover = false;
											Values.updated = false;
										}
										
										if(msg.contains("QUIT"))
										{
											StringTokenizer toke = new StringTokenizer(msg,"\t");
											toke.nextToken();//CMD i.e. STAT etc
											String usr = toke.nextToken();
											if(usr.equals(FrontEndMain.host))//if the host quit
											{
												FrontEndMain.key.getCanvas().getActors().clear();
												Values.showlobby = true;
												Values.showcanvas = false;
												Values.showgameover = false;
												Values.updated = false;
											}
											Actor a = null;
											Iterator iterator = FrontEndMain.key.getCanvas().getActors().iterator();
											while(iterator.hasNext())
											{
												a = (Actor) iterator.next();
												if(a != null)
												{
													if(a.getUsername().equals(usr))
													{
														//print message to say who has left
														iterator.remove();
														break;
													}
												}
											}
										}
										
										if(msg.contains("REM_BULT"))
										{
											StringTokenizer toke = new StringTokenizer(msg,"\t");
											toke.nextToken();//CMD i.e. STAT etc
											long id = Long.valueOf(toke.nextToken());
											Bullet b = null;
											Iterator iterator = FrontEndMain.key.getCanvas().getBullets().iterator();
											while(iterator.hasNext())
											{
												b = (Bullet) iterator.next();
												if(b != null)
												{
													if(b.getID()==id)
													{
														iterator.remove();
														break;
													}
												}
											}
										}
										
										if(msg.contains("UPDT_HP"))
										{
											StringTokenizer toke = new StringTokenizer(msg,"\t");
											toke.nextToken();//CMD i.e. STAT etc
											String username = toke.nextToken();
											double hp = Double.valueOf(toke.nextToken());
											
											for(Actor a:FrontEndMain.key.getCanvas().getActors())
											{
												if(a.getUsername().equals(username))
												{
													a.setHP(hp);
												}
											}
										}
										
										if(msg.contains("ADD_BULT"))
										{
											StringTokenizer toke = new StringTokenizer(msg,"\t");
											toke.nextToken();//CMD i.e. STAT etc
											double x = Double.valueOf(toke.nextToken());
											double y = Double.valueOf(toke.nextToken());
											int w = Integer.valueOf(toke.nextToken());
											int h = Integer.valueOf(toke.nextToken());
											double weight = Double.valueOf(toke.nextToken());
											int dir = Integer.valueOf(toke.nextToken());
											long id = Long.valueOf(toke.nextToken());
											String sender = toke.nextToken();
											FrontEndMain.key.getCanvas().addBullet(new Bullet(x, y, w, h, weight, dir, id, sender));
										}
										
										if(msg.contains("OK 300") || msg.contains("OK 400"))
										{
											
										}
										
										if(msg.contains("DETAILS"))
										{
											StringTokenizer toke = new StringTokenizer(msg,"\t");
											toke.nextToken();//CMD i.e. DETAILS etc
											String usr = toke.nextToken();
											boolean exists = false;
											for(Actor a:FrontEndMain.key.getCanvas().getActors())
											{
												if(a.getUsername().equals(usr))
												{
													sendResponse("ERR_USR_EXISTS 250",rcv);
													exists = true;
												}
											}
											if(!exists)
											{
												int x = Integer.valueOf(toke.nextToken());
												int y = Integer.valueOf(toke.nextToken());
												FrontEndMain.key.getCanvas().getActors().add(new Actor(x, y, Values.ACTOR_SIZE,Values.ACTOR_SIZE, 10, rcv.getAddress(), rcv.getPort(), usr));
												refreshList();
												echo("Username: " + usr + ", X:" + x + ", Y: " + y + " has joined",false);
												sendResponse("OK 200",rcv);
											}
										}
										if(msg.contains("STAT"))
										{
											StringTokenizer toke = new StringTokenizer(msg,"\t");
											toke.nextToken();//CMD i.e. STAT etc
											String usr = toke.nextToken();
											String stat = toke.nextToken();
											echo("Username: " + usr + ", STAT: " + stat,false);
											if(stat.equals("READY"))
											{
												for(Actor a:FrontEndMain.key.getCanvas().getActors())
													if(a.getUsername().equals(usr))
														a.setReady(true);
											}
											else if(stat.equals("!READY"))
											{
												for(Actor a:FrontEndMain.key.getCanvas().getActors())
													if(a.getUsername().equals(usr))
														a.setReady(false);
											}
											refreshList();
											sendResponse("OK 300",rcv);
										}
										
										if(msg.contains("MOVE"))
										{
											StringTokenizer toke = new StringTokenizer(msg,"\t");
											toke.nextToken();//CMD i.e. STAT etc
											String usr = toke.nextToken();
											int x = Integer.valueOf(toke.nextToken());
											int y = Integer.valueOf(toke.nextToken());
											echo("Move Username: " + usr + ", x: " + x + ", y: " + y,false);
											for(Actor a:FrontEndMain.key.getCanvas().getActors())
											{
												if(a.getUsername().equals(usr))
												{
													a.setX(x);
													a.setY(y);
												}
											}
											sendResponse("OK 400",rcv);
										}
									}
									catch (IOException e) 
									{
										echo(e.getMessage(),true);
										e.printStackTrace();
									}
								}
							}
						});
						listener.start();
					}
					catch (UnknownHostException e1) 
					{
						echo(e1.getMessage(),true);
					} 
					catch (SocketException e1) 
					{
						echo(e1.getMessage(),true);
					} 
					catch (IOException e1) 
					{
						echo(e1.getMessage(),true);
					}
				}
				else
				{
					JOptionPane.showMessageDialog(null,"Server IP Address can't be empty", "Error",JOptionPane.ERROR_MESSAGE);
				}
			}
		});
	}
	
	public void sendResponse(String response,DatagramPacket rcv) throws IOException
	{
		DatagramPacket snd = new DatagramPacket(response.getBytes(), response.getBytes().length,rcv.getAddress(),rcv.getPort());
		server.send(snd);
	}
	
	public void echo(String msg,boolean err)
	{
		if(err)
			info.append("[ERROR]"+msg + "\n");
		else
			info.append(msg + "\n");
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
}
