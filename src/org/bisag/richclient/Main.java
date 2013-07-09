package org.bisag.richclient;

import java.io.File;

import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.map.*;
import org.geotools.swt.*;
/**
* Use of SWT as UI library.
* 
* @author: Rakshit Majithiya
*/

public class Main {
	   public static void main( String[] args ) throws Exception {
	    // create a default mapcontext
	    MapContext context = new DefaultMapContext();
	    // set the title
	    context.setTitle("BISAG GIS");
	    // add a shapefile if you like
	    File shapeFile = new File("res//maps//World_Map.shp");
	    ShapefileDataStore store = new ShapefileDataStore(shapeFile.toURI().toURL());
	    SimpleFeatureSource featureSource = store.getFeatureSource();
	    SimpleFeatureCollection shapefile = featureSource.getFeatures();
	    context.addLayer(shapefile, null);
	    // and show the mapContent viewer
	    SwtMapFrame.showMap(context);
	   }
	 }