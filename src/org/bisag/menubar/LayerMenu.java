package org.bisag.menubar;


import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import org.bisag.main.Main;
import org.geotools.map.MapContent;
import org.geotools.swing.JMapPane;


@SuppressWarnings("serial")
public class LayerMenu extends JMenu {
	private JMenuItem legend,remove,removeAll,exit;

	public LayerMenu(final JMapPane mapPane) {
		final MapContent mapContent = mapPane.getMapContent();
		legend = new JMenuItem("Make Legend");
		legend.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
					Main.legendAction();
			}
		});
		
		remove = new JMenuItem("Remove Layer");
		remove.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(mapContent.layers().size() != 0)
				{
					mapContent.removeLayer(mapContent.layers().get(mapContent.layers().size() -1));
					org.bisag.geotools.JMapFrame.leftSplitPane.setBottomComponent(null);
					mapPane.removeAll();
				}
				else
					JOptionPane.showMessageDialog(null,"No layers are added !");
			}
		});
		
		removeAll = new JMenuItem("Remove All Layers");
		removeAll.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				while(mapContent.layers().size()>0)
					mapContent.layers().remove(0);
				mapPane.removeAll();
				mapPane.repaint();
				org.bisag.geotools.JMapFrame.leftSplitPane.setBottomComponent(null);
			}
		});

		exit = new JMenuItem("Exit Application");
		exit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
	
		
		this.add(legend);
		this.add(remove);
		this.add(removeAll);
		this.add(exit);
		this.setText("Layer");
		this.setVisible(true);
	}

	
}
