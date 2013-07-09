package org.bisag.test;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.visitor.UniqueVisitor;
import org.geotools.filter.FunctionExpressionImpl;
import org.geotools.filter.capability.FunctionNameImpl;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.capability.FunctionName;
import org.opengis.filter.expression.Expression;

/**
*
* @author michael
*/
public class ColorLookupFunction extends FunctionExpressionImpl {

    private static final float INITIAL_HUE = 0.1f;

    private static final int SOURCE_PARAM_INDEX = 0;
    private static final String SOURCE_PARAM_NAME = "featureSource";

    private static final int COLOUR_PARAM_INDEX = 1;
    private static final String COLOUR_PARAM_NAME = "colourExpr";

    // Set name, return type, and names and types of parameters
    public FunctionName NAME = new FunctionNameImpl("colorlookup",
            Color.class,  // return type
            FunctionNameImpl.parameter(SOURCE_PARAM_NAME,
SimpleFeatureSource.class),
            FunctionNameImpl.parameter(COLOUR_PARAM_NAME, Expression.class));

    Map<Object, Color> lookup;

    private int numColours;
    private float hue;
    private float hueIncr;
    private float saturation = 0.7f;
    private float brightness = 0.7f;

    public ColorLookupFunction() {
        super("colorlookup");
    }

    @Override
    public Object evaluate(Object obj) {
        if (obj instanceof SimpleFeature) {
            SimpleFeature feature = (SimpleFeature) obj;
            Object key =
getParameters().get(COLOUR_PARAM_INDEX).evaluate(feature);

            if (lookup == null) {
                createLookup();
            }

            Color color = lookup.get(key);
            if (color == null) {
                color = addColor(key);
            }

            return color;
        }

        return null;
    }

    private void createLookup() {
        lookup = new HashMap<Object, Color>();
        try {
            SimpleFeatureCollection fc;

            Object o = getParameters().get(SOURCE_PARAM_INDEX).evaluate(null);
            if (o instanceof SimpleFeatureSource) {
                fc = ((SimpleFeatureSource) o).getFeatures();

            } else if (o instanceof SimpleFeatureCollection) {
                fc = (SimpleFeatureCollection) o;

            } else {
                throw new IllegalArgumentException(
                        "First parameter must be either a SimpleFeatureSource or SimpleFeatureCollection");
            }

            Expression colourExpr = getParameters().get(COLOUR_PARAM_INDEX);

            UniqueVisitor visitor = new UniqueVisitor(colourExpr);
            fc.accepts(visitor, null);

            numColours = visitor.getUnique().size();
            hue = INITIAL_HUE;
            hueIncr = (1.0f - hue) / numColours;

        } catch (Exception ex) {
            throw new IllegalStateException("Problem creating colour lookup", ex);
        }
    }

    private Color addColor(Object key) {
        Color c = new Color(Color.HSBtoRGB(hue, saturation, brightness));
        hue += hueIncr;
        lookup.put(key, c);
        return c;
    }
}
