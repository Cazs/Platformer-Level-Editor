package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import States.Values;

public class TopPane extends JPanel 
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8003700376855433880L;
	private JTextArea info = null; 

	public TopPane(JTextArea info)
	{
		this.info = info;
		initUI();
	}
	
	private void initUI()
	{
		this.setLayout(new BorderLayout());
		this.setBackground(Color.DARK_GRAY);
		this.setPreferredSize(new Dimension(400,Values.TOP_H));
		
		info.setLineWrap(true);
		info.setEditable(false);
		info.setForeground(Color.GREEN);
		info.setBackground(Color.DARK_GRAY);
		
		JScrollPane scroll = new JScrollPane(info);
		scroll.setPreferredSize(new Dimension(250,0));
		//scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		this.add(scroll,BorderLayout.WEST);
	}
}
