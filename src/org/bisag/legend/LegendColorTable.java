package org.bisag.legend;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.*;

import javax.swing.*;

/**
* UI for legend color chooser.
* 
* @author: Rakshit Majithiya
*/

public class LegendColorTable extends JPanel {
	public static int countTable = 0;
	public int maxlength = 0;
	public Point initialPoint,mouseStartPoint,mouseEndPoint;
	public JLabel titleLabel;
	public LegendColorTable(Map<Object, Color> lookup, String fieldName,int width,int height,boolean addListeners) {
			
			
			Object[] key = lookup.keySet().toArray();

			this.setBorder(BorderFactory.createEmptyBorder());
			this.setName("NAME");
			this.setBackground(Color.WHITE);
			
			JPanel tableContainer = new JPanel(new SpringLayout());
			tableContainer.setBackground(Color.WHITE);
			for (int i = 0; i < key.length; i++) {
				// create panel for color
				JPanel colorp = new JPanel();
				colorp.setBackground(lookup.get(key[i]));
				colorp.setMaximumSize(new Dimension(20,20));
				colorp.setPreferredSize(new Dimension(20,20));
				colorp.setBorder(BorderFactory.createLineBorder(Color.BLACK));
				
				// create label for label

				JLabel field = new JLabel(key[i].toString());
				
				tableContainer.add(colorp);
				tableContainer.add(field);
				if(key[i].toString().length() > maxlength)
					maxlength = key[i].toString().length();
			}
			SpringUtilities.makeCompactGrid(tableContainer, key.length , 2, 6, 6, 6,6);
			
			
			titleLabel = new JLabel(fieldName,JLabel.LEADING);
			this.setLayout(new SpringLayout());
			this.add(titleLabel);
			this.add(tableContainer);
			
			SpringUtilities.makeCompactGrid(this, 2, 1, 10, 10, 10, 0);
			
			if(width !=0){
				this.setSize(width,height);
			}
			else{
				this.setSize((int)this.getPreferredSize().getWidth() + 50 , (int)this.getPreferredSize().getHeight() + 100);
			}
			
			if(addListeners){
				this.setOpaque(false);
				tableContainer.setOpaque(false);
				
				this.setToolTipText("Right-Click to modify the dimensions");
				
				this.addMouseListener(new MouseListener() {
				
				@Override
				public void mouseReleased(MouseEvent me) {
					if(me.isPopupTrigger()){
						setBorder(BorderFactory.createLineBorder(Color.GRAY));
						JPopupMenu popup = new JPopupMenu();
						JMenuItem configureTable = new JMenuItem("Resize");
						JMenuItem remove = new JMenuItem("Remove");
						
						configureTable.addActionListener(new ActionListener() {
							
							@Override
							public void actionPerformed(ActionEvent arg0) {
								System.out.println("" + getSize());
								int width = 0,height=0;
								try{
									width = Integer.parseInt(JOptionPane.showInputDialog("Enter New Width", getWidth()).trim());
									height = Integer.parseInt(JOptionPane.showInputDialog("Enter New Height", getHeight()).trim());
								}
								catch(Exception e){}
								if(width>0 && height > 0)
									setSize(width, height);
							}
						});
						
						remove.addActionListener(new ActionListener(){
							@Override
							public void actionPerformed(ActionEvent e) {
								if(JOptionPane.showConfirmDialog(null,  "Do you really want to remove this Legend table?","Remove Legend Table",JOptionPane.YES_NO_OPTION) == 0)
									setVisible(false);
							}
						});
						
						popup.add(configureTable);
						popup.add(remove);
						popup.show(me.getComponent(), me.getX(), me.getY());
						
						
					}
				}
				
				@Override
				public void mousePressed(MouseEvent me) {
					initialPoint = getLocation();
					mouseStartPoint = me.getLocationOnScreen();
				}
				
				@Override
				public void mouseExited(MouseEvent arg0) {
					setBorder(BorderFactory.createEmptyBorder());
					setCursor(Cursor.getDefaultCursor());
				}
				
				@Override
				public void mouseEntered(MouseEvent arg0) {
					setBorder(BorderFactory.createEtchedBorder());
					setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
				}
				
				@Override
				public void mouseClicked(MouseEvent arg0) {
				}
			});
				this.addMouseMotionListener(new MouseMotionListener() {
				
				@Override
				public void mouseMoved(MouseEvent arg0) {
				}
				
				@Override
				public void mouseDragged(MouseEvent me) {
					mouseEndPoint = me.getLocationOnScreen();
					int dx = mouseStartPoint.x - mouseEndPoint.x;
					int dy = mouseStartPoint.y - mouseEndPoint.y;
					setLocation(initialPoint.x-dx, initialPoint.y-dy);
				}
			});
			}
			
			
	}

}
