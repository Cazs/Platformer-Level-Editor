package GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import Model.Block;
import States.Key;
import States.Values;

public class PropertiesPane extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5002898185961659121L;
	
	private final JCheckBox rig				= 	new JCheckBox("Rigid");
	private final JCheckBox spwn			= 	new JCheckBox("Spawn Point");
	private final JLabel w					= 	new JLabel("Weight: ");
	private final JLabel lbl_size			=	new JLabel("Size: ");
	private final JLabel lbl_colour			= 	new JLabel("Colour: ");
	private final JLabel lbl_id				= 	new JLabel("ID: ");
	private final JLabel lbl_x				= 	new JLabel("X: ");
	private final JLabel lbl_y				= 	new JLabel("Y: ");
	private Key key							= 	null;
	
	public PropertiesPane(Key key)
	{
		this.key = key;
		initUI();
		update();
	}
	
	private void initUI()
	{
		this.setPreferredSize(new Dimension(Values.PROP_W,400));
		this.setBackground(Color.DARK_GRAY);
		
		JPanel propHolder = new JPanel();
		propHolder.setLayout(new GridLayout(4,1));
		
		propHolder.add(rig);
		propHolder.add(spwn);
		propHolder.add(w);
		propHolder.add(lbl_size);
		propHolder.add(lbl_colour);
		propHolder.add(lbl_id);
		propHolder.add(lbl_x);
		propHolder.add(lbl_y);
		
		this.setLayout(new GridLayout(6,2));
		this.add(propHolder);
	}
	
	private void update()
	{
		/**Update properties pane**/
		javax.swing.Timer tprop_pane = new javax.swing.Timer(1, new ActionListener() 
		{
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				int numMarked = 0;
				for(Block b:key.getCanvas().getBlocks())
				{
					if(b.isMarked())
					{
						numMarked++;
					}
				}
				
				if(numMarked==1)
				{
					for(Block b:key.getCanvas().getBlocks())
					{
						if(b.isMarked())
						{
							rig.setSelected(b.isRigid());
							spwn.setSelected(b.isSpawnpoint());
							w.setText("Weight: " + b.getWeight());
							lbl_size.setText("Width: "+b.getWidth()+" Height:"+b.getHeight());
							lbl_colour.setText("Color: ["+b.getColour().getRed()+","+b.getColour().getGreen()+","+b.getColour().getBlue()+"]");
							lbl_id.setText("ID: N/A");
							lbl_x.setText("X: "+b.getX());
							lbl_y.setText("Y: "+b.getY());
						}
					}
				}
				else
				{
					rig.setSelected(false);
					spwn.setSelected(false);
					w.setText("Weight: ");
					lbl_size.setText("Size: ");
					lbl_colour.setText("Color: ");
					lbl_id.setText("ID: ");
					lbl_x.setText("X: ");
					lbl_y.setText("Y: ");
				}
			}
		});
		tprop_pane.start();
	}
}
