package org.bisag.popup;

import javax.swing.JPopupMenu;

import org.bisag.menubar.*;
import org.geotools.swing.JMapPane;


public class MapPanePopup extends JPopupMenu {
	public MapPanePopup(final JMapPane mapPane) {
		
		
		MapsMenu mapsMenu = new MapsMenu(mapPane);
		LayerMenu layerMenu = new LayerMenu(mapPane);
		
		this.add(mapsMenu);
		this.add(layerMenu);
		
	}
}
