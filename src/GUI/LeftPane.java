package GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import Model.Block;
import States.Key;
import States.Values;

public class LeftPane extends JPanel 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2347348294847996016L;
	private JColorChooser col 						= new JColorChooser();;
	private boolean blk 							= false;
	private boolean bg								= false;
	private final JButton block 					= new JButton("Choose block colour");
	private final JButton bkg 						= new JButton("Choose background colour");
	private final JButton spawn 					= new JButton("Set Spawn Point To Selected");
	private final JButton rigid 					= new JButton("Set Rigid Body To Selected");
	private final JButton physics 					= new JButton();//"Simulate Physics"
	private final JButton wght 						= new JButton("Apply Weight To Selected");
	private JTextField weight						= new JTextField();
	private final JFrame colour_frame 				= new JFrame();
	private static ArrayList<Block> backup			=  new ArrayList<Block>();
	private Key key 								= null;
	private ImageIcon play							= null;
	private ImageIcon stop							= null;
	
	public LeftPane(Key key) throws MalformedURLException
    {
		this.key = key;
		initUI();
		initEventHandlers();
	}
	
	private void initUI() throws MalformedURLException
    {
		this.setPreferredSize(new Dimension(Values.LEFT_W,400));
		this.setBackground(Color.DARK_GRAY);

		//Play button image
		File imgPlay = new File("data/play.png");
        File imgStop = new File("data/stop.png");

        if(!imgPlay.exists() || !imgStop.exists())
        {
            JOptionPane.showMessageDialog(null, "data/ directory is incomplete, please reinstall.", "Installation Corrupted", JOptionPane.ERROR_MESSAGE);
            return;
        }

		play = new ImageIcon(new URL("file://"+imgPlay.getAbsolutePath()));
		Image image = play.getImage();
		image = image.getScaledInstance(image.getWidth(null)/2, image.getHeight(null)/2, Image.SCALE_SMOOTH);
		play.setImage(image);

		//Stop button image
		stop = new ImageIcon(new URL("file://"+imgStop.getAbsolutePath()));
		image = stop.getImage();
		image = image.getScaledInstance(image.getWidth(null)/2, image.getHeight(null)/2, Image.SCALE_SMOOTH);
		stop.setImage(image);
		physics.setIcon(play);
		
		weight.setToolTipText("Weight e.g. 2.3");
		//Colour frame properties
		colour_frame.setBounds(0,0,480,320);
		colour_frame.setLocationRelativeTo(null);
		colour_frame.add(col);
		
		block.setPreferredSize(new Dimension(Values.LEFT_W,50));
		bkg.setPreferredSize(new Dimension(Values.LEFT_W,50));
		spawn.setPreferredSize(new Dimension(Values.LEFT_W,50));
		rigid.setPreferredSize(new Dimension(Values.LEFT_W,50));
		physics.setPreferredSize(new Dimension(Values.LEFT_W,50));
		weight.setPreferredSize(new Dimension(Values.LEFT_W,40));
		wght.setPreferredSize(new Dimension(Values.LEFT_W,50));
		
		this.add(block);
		this.add(bkg);
		this.add(spawn);
		this.add(rigid);
		this.add(physics);
		this.add(weight);
		this.add(wght);
	}
	
	private void initEventHandlers()
	{
		block.addActionListener(new ActionListener()
		{
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				colour_frame.setVisible(true);//show colour frame
				blk = true;
			}
		});
		
		colour_frame.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e) 
			{
				super.windowClosing(e);
				if(blk)
				{
					Values.block_colour = col.getColor().brighter();
					blk = false;
					Main.info.append("Changed block colour to " + col.getColor() + "\n");
				}
				
				if(bg)
				{
					Values.bg_colour = col.getColor().brighter();
					bg = false;
					Main.info.append("Changed background colour to " + col.getColor() + "\n");
				}
			}
		});
		
		wght.addActionListener(new ActionListener()
		{
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				if(weight.getText().length()>0)
				{
					for(Block b: key.getCanvas().getBlocks())
						if(b.isMarked())
						{
							b.setWeight(Double.valueOf(weight.getText()));
							Main.info.append(String.format("Weight of %s units applied to block: " + b + "\n",weight.getText()));
						}
				}
				else
				{
					JOptionPane.showMessageDialog(null, "Cannot have empty weight","Error",JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		rigid.addActionListener(new ActionListener()
		{
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				for(Block b: key.getCanvas().getBlocks())
				{
					if(b.isMarked())
					{
						if(b.isRigid())
							b.setRigid(false);
						else
							b.setRigid(true);
					}
			}
				Main.info.append("Applied rigid body to selected blocks \n");
				//JOptionPane.showMessageDialog(null, "Applied rigid body to selected blocks","Rigid application Success",JOptionPane.INFORMATION_MESSAGE);
			}
		});
		
		physics.addActionListener(new ActionListener() 
		{
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				if(Values.physics)
				{
					key.getCanvas().setBlocks(backup);
					Values.physics	= false;
					Main.info.append("Physics simulation ended \n");
					key.getCanvas().getActorInstance().setX(350);
					key.getCanvas().getActorInstance().setY(10);
					physics.setIcon(play);
				}
				else
				{
					backup = null;
					backup = new ArrayList<Block>();
					for(Block b:key.getCanvas().getBlocks())
						backup.add(new Block(b.getX(),b.getY(),b.getWidth(),b.getHeight(),b.getWeight(),b.getColour(),b.isRigid(),b.isSpawnpoint()));
					
					Values.physics = true;
					Main.info.append("Physics simulation started \n");
					physics.setIcon(stop);
				}
			}
		});
		
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
				Main.info.append("Applied spawnpoint to selected blocks \n");
			}
		});
		
		col.getSelectionModel().addChangeListener(new ChangeListener() 
		{
			@Override
			public void stateChanged(ChangeEvent e) 
			{
				if(blk)//if changing the block colour
				{
					Values.block_colour = col.getColor().brighter();
					for(Block b:key.getCanvas().getBlocks())
						if(b.isMarked())
							b.setColour(Values.block_colour);
				}
				
				if(bg)//if changing the background colour
				{
					Values.bg_colour = col.getColor().brighter();
				}
			}
		});
	
		bkg.addActionListener(new ActionListener()
		{
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				bg = true;
				colour_frame.setVisible(true);//display colour frame
			}
		});
	}
}
