package org.bisag.menubar;

import javax.swing.JMenuBar;

import org.geotools.swing.JMapPane;

@SuppressWarnings("serial")
public class CustomMenuBar extends JMenuBar {

	public CustomMenuBar(JMapPane mapPane) {
		this.add(new LayerMenu(mapPane));
		this.add(new MapsMenu(mapPane));
		this.setAutoscrolls(true);
	}
}
