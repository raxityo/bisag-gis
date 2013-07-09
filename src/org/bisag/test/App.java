package org.bisag.test;

import java.io.File;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.vividsolutions.jts.geom.Geometry;

import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.geometry.jts.Geometries;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.styling.Fill;
import org.geotools.styling.Graphic;
import org.geotools.styling.Mark;
import org.geotools.styling.SLD;
import org.geotools.styling.Stroke;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.geotools.styling.Symbolizer;
import org.geotools.swing.JMapFrame;
import org.geotools.swing.data.JFileDataStoreChooser;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.Function;

/**
 * Displays a shapefile using a Style in which ColorLookupFunction is used to
 * set feature colours based on unique keys of a selected attribute.
 * 
 * @author michael
 */
public class App {

	public static void main(String[] args) throws Exception {
		File file = JFileDataStoreChooser.showOpenFile("shp", null);
		if (file != null) {
			FileDataStore store = FileDataStoreFinder.getDataStore(file);
			SimpleFeatureSource source = store.getFeatureSource();

			String fieldName = getFieldForColour(source);
			if (fieldName != null && fieldName.length() > 0) {
				Style style = createStyle(source, fieldName);
				Layer layer = new FeatureLayer(source, style);
				MapContent map = new MapContent();
				map.addLayer(layer);

				org.geotools.swing.JMapFrame mapframe = new JMapFrame(map);
				mapframe.enableToolBar(true);
				mapframe.setExtendedState(JFrame.MAXIMIZED_BOTH);
				mapframe.enableLayerTable(true);
				mapframe.setTitle("GIS Application");
				mapframe.setVisible(true);
			}
		}
	}

	public static String getFieldForColour(SimpleFeatureSource source)
			throws Exception {
		final String fieldName[] = new String[1];

		final String[] fieldNames = new String[source.getSchema().getAttributeCount()];
		int k = 0;
		for (AttributeDescriptor desc : source.getSchema().getAttributeDescriptors()) {
			fieldNames[k++] = desc.getLocalName();
		}

		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				Object obj = JOptionPane.showInputDialog(null,
						"Choose an attribute for colouring",
						"Feature attribute", JOptionPane.PLAIN_MESSAGE, null,
						fieldNames, fieldNames[0]);

				if (obj != null) {
					fieldName[0] = (String) obj;
				}
			}
		});

		return fieldName[0];
	}

	public static Style createStyle(SimpleFeatureSource source,
			String fieldName) throws Exception {
		StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory();
		FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();

		Function colourFn = ff.function("colorlookup", ff.literal(source), ff
				.property(fieldName));

		Stroke stroke = styleFactory.createStroke(colourFn, ff.literal(1.0f), // line
																				// width
				ff.literal(1.0f)); // opacity

		Fill fill = styleFactory.createFill(colourFn, ff.literal(1.0f)); // opacity

		Class<?> geomClass = source.getSchema().getGeometryDescriptor()
				.getType().getBinding();
		Symbolizer sym = null;
		Geometries geomType = Geometries
				.getForBinding((Class<? extends Geometry>) geomClass);
		switch (geomType) {
		case POLYGON:
		case MULTIPOLYGON:
			sym = styleFactory.createPolygonSymbolizer(stroke, fill, null);
			break;

		case LINESTRING:
		case MULTILINESTRING:
			sym = styleFactory.createLineSymbolizer(stroke, null);
			break;

		case POINT:
		case MULTIPOINT:
			Graphic gr = styleFactory.createDefaultGraphic();
			gr.graphicalSymbols().clear();
			Mark mark = styleFactory.getCircleMark();
			mark.setFill(fill);
			mark.setStroke(stroke);
			gr.graphicalSymbols().add(mark);
			gr.setSize(ff.literal(10.0f));
			sym = styleFactory.createPointSymbolizer(gr, null);
			break;

		default:
			throw new IllegalArgumentException("Unsupported geometry type");
		}

		Style style = SLD.wrapSymbolizers(sym);
		return style;
	}


}
