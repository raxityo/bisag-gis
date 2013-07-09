package org.bisag.menubar;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.*;

import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.geotools.swing.JMapPane;
import org.geotools.swing.data.JFileDataStoreChooser;

public class MapsMenu extends JMenu {
	private JMenuItem worldMap,worldPopMap, indiaMap, indiaGeology, indiaDrainage,browseMap,indiaHighways, indiaCapitals;
	private static MapContent content;
	private final String WORLD_MAP = "World_Map";
	private final String WORLD_POPULATED = "Populated_Places";
	private final String INDIA_MAP = "India_Map";
	private final String INDIA_GEOLOGY = "INDIA_geology_polygon";
	private final String INDIA_DRAINAGE = "INDIA_drainage_arc";
	private final String INDIA_HIGHWAYS = "INDIA_highways_arc";
	private final String INDIA_CAPITALS = "INDIA_capitals_point";

	public MapsMenu(JMapPane mapPane) {
		content = mapPane.getMapContent();
		
		browseMap = new JMenuItem("Browse");
		worldMap = new JMenuItem("World Map (Polygon)");
		worldPopMap = new JMenuItem("World Populated Places (Point)");
		indiaMap = new JMenuItem("India Map (Polygon)");
		indiaGeology = new JMenuItem("India Geology Map (Polygon)");
		indiaDrainage = new JMenuItem("India Drainage Map (Arc)");
		indiaHighways = new JMenuItem("India Highways Map (Arc)");
		indiaCapitals = new JMenuItem("India Capitals Map (Point)");

		browseMap.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Thread t = new Thread(new Runnable() {
					public void run() {
						loadMap(null);
					}
				});
				t.start();
			}
		});
		worldMap.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				Thread t = new Thread(new Runnable() {
					public void run() {
						loadMap(WORLD_MAP);
					}
				});
				t.start();

			}
		});

		worldPopMap.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				Thread t = new Thread(new Runnable() {
					public void run() {
						loadMap(WORLD_POPULATED);
					}
				});
				t.start();

			}
		});
		indiaMap.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Thread t = new Thread(new Runnable() {
					public void run() {
						loadMap(INDIA_MAP);
					}
				});
				t.start();

			}
		});
		indiaGeology.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Thread t = new Thread(new Runnable() {
					public void run() {
						loadMap(INDIA_GEOLOGY);
					}
				});
				t.start();

			}
		});
		indiaDrainage.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Thread t = new Thread(new Runnable() {
					public void run() {
						loadMap(INDIA_DRAINAGE);
					}
				});
				t.start();

			}
		});
		indiaHighways.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Thread t = new Thread(new Runnable() {
					public void run() {
						loadMap(INDIA_HIGHWAYS);
					}
				});
				t.start();

			}
		});
		indiaCapitals.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Thread t = new Thread(new Runnable() {
					public void run() {
						loadMap(INDIA_CAPITALS);
					}
				});
				t.start();

			}
		});

		JMenu worldMaps = new JMenu("World Maps");
		worldMaps.add(worldMap);
		worldMaps.add(worldPopMap);
		
		JMenu indiaMaps = new JMenu("India Maps");
		indiaMaps.add(indiaMap);
		indiaMaps.add(indiaGeology);
		indiaMaps.add(indiaDrainage);
		indiaMaps.add(indiaHighways);
		indiaMaps.add(indiaCapitals);
		
		this.add(browseMap);
		this.add(worldMaps);
		this.add(indiaMaps);
		this.setText("Load Map");
	}
	static void loadMap(final String mapLocation) {
		File file = new File("");
		FileDataStore store;
		try {
			if (mapLocation == null)
				file = JFileDataStoreChooser.showOpenFile("shp", null);
			else{
				file = new File("res\\maps\\" + mapLocation + ".shp");
			}
				
			
			store = FileDataStoreFinder.getDataStore(file);
			SimpleFeatureSource featureSource = store.getFeatureSource();
			Style style = SLD.createSimpleStyle(featureSource.getSchema(),new Color(150,150,255));
			Layer layer = new FeatureLayer(featureSource, style);
			content.addLayer(layer);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
