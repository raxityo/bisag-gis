package org.bisag.main;



import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;

import javax.imageio.ImageIO;
import javax.swing.*;

import org.bisag.geotools.JMapFrame;
import org.bisag.legend.LegendColorTable;
import org.bisag.menubar.*;
import org.bisag.tools.QueryWindow;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.geometry.jts.Geometries;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.*;
import org.geotools.renderer.GTRenderer;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.*;
import org.geotools.styling.Stroke;
import org.geotools.swing.JMapPane;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.Function;

import com.vividsolutions.jts.geom.Geometry;

/** 
 * The main launcher class
 * @author Rakshit Majithiya :p
 */
@SuppressWarnings("serial")
public class Main extends org.bisag.geotools.JMapFrame{


	public JButton refreshBtn,printBtn,queryBtn;
	public JMapPane mapPane;
	public JPanel mainPanel;
	protected boolean distanceflag;
	protected boolean distance_and_scalefalg;
	protected double dis;
	protected int numberofpoint;
	public static MapContent content;
	
	public Main() {
		try {
			
			this.configureDisplay();
			mapPane.setForeground(Color.BLUE);
			refreshBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					
					JMapFrame.splitPane.resetToPreferredSizes();
					JMapFrame.leftSplitPane.resetToPreferredSizes();
					JMapFrame.leftSplitPane.setBottomComponent(null);
					getMapPane().removeAll();
				}
			});
			queryBtn.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					Thread t = new Thread(new Runnable(){
						public void run(){
							new QueryWindow(getMapContent(),getMapContent().layers().get(0)).setVisible(true);
						}
					});
					if(getMapContent().layers().size()!=0)
						t.start();
					else
						JOptionPane.showMessageDialog(null, "No Layers are added !");
				}
			});
			printBtn.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					saveNewImage(getMapContent(), "TEST");
					saveImage(getMapContent(), "TEST", 1000);
					
				}
			});
			

		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, ex.getMessage());
		}

	}
//	public void savePDF(){
//		 Rectangle suggestedPageSize = getITextPageSize(page1.getPageSize());
//		  Rectangle pageSize = rotatePageIfNecessary(suggestedPageSize);
//		  //rotate if we need landscape
//		  Document document = new Document(pageSize);
//
//		  PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(outputFile));
//		  document.open();
//		  Graphics2D graphics = cb.createGraphics(pageSize.getWidth(), pageSize.getHeight());
//
//		  // call your GTRenderer here
//		  GTRenderer draw = new StreamingRenderer();
//		  draw.setMapContent(mapContent);
//
//		  draw.paint(graphics, outputArea, mapContent.getLayerBounds() );
//
//		  // cleanup
//		  graphics.dispose();
//
//		  //cleanup
//		  document.close();
//		  writer.close();
//	}
	public void saveImage(final MapContent map, final String file, final int imageWidth) {

	    GTRenderer renderer = new StreamingRenderer();
	    renderer.setMapContent(map);

	    Rectangle imageBounds = null;
	    ReferencedEnvelope mapBounds = null;
	    try {
	        mapBounds = map.getMaxBounds();
	        double heightToWidth = mapBounds.getSpan(1) / mapBounds.getSpan(0);
	        imageBounds = new Rectangle(
	                0, 0, imageWidth, (int) Math.round(imageWidth * heightToWidth));

	    } catch (Exception e) {
	        // failed to access mapContent layers
	        throw new RuntimeException(e);
	    }

	    BufferedImage image = new BufferedImage(imageBounds.width, imageBounds.height, BufferedImage.TYPE_INT_RGB);

	    Graphics2D gr = image.createGraphics();
	    gr.setPaint(Color.WHITE);
	    gr.fill(imageBounds);

	    try {
	        renderer.paint(gr, imageBounds, mapBounds);
	        File fileToSave = new File(file);
	        ImageIO.write(image, "jpeg", fileToSave);

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	  public void saveNewImage(MapContent map, String file){ 
	  
	    GTRenderer renderer = new StreamingRenderer();
	    renderer.setMapContent( map );
	    System.out.println("line 139");
	    Rectangle imageBounds=null;
	    try{
	        ReferencedEnvelope mapBounds=map.getMaxBounds();

	        double heightToWidth = mapBounds.getSpan(1) / mapBounds.getSpan(0);
	        int imageWidth = 600;

	        imageBounds = new Rectangle(0, 0, imageWidth, (int) Math.round(imageWidth * heightToWidth));
	    }catch(Exception e){
	        
	    } 
	    System.out.println("line 151");
//	    Rectangle imageSize = new Rectangle(600,600);
	    
	    BufferedImage image = new BufferedImage(imageBounds.width, imageBounds.height, BufferedImage.TYPE_INT_RGB); //darker red fill

	    Graphics2D gr = image.createGraphics();
	    gr.setPaint(Color.WHITE);
	    gr.fill(imageBounds);
	    
	    try {
	    	System.out.println("line 161");
	        renderer.paint(gr, imageBounds,map.getMaxBounds());
	        System.out.println("line 163");
	        
	        File fileToSave = new File(file);
	        System.out.println("line 166");
	        ImageIO.write(image, "jpeg", fileToSave);
	        System.out.println("line 168");
	    }catch(IOException e){
	        
	    }    
	  }
	public static void legendAction() {
		try {
			LegendColorTable.countTable = 0;
			Layer layer = content.layers().get(content.layers().size() - 1);
			SimpleFeatureSource featureSource = (SimpleFeatureSource) layer.getFeatureSource();
			String fieldName = getFieldForColour(featureSource);

			if (fieldName != null && fieldName.length() > 0) {
				
				Style style = createStyle(featureSource, fieldName);
				
				content.removeLayer(layer);
				Layer newLayer = new FeatureLayer(featureSource, style);
				content.addLayer(newLayer);
				
			} else
				throw new NullPointerException("Error occured during making legend");
			
		} catch (ArrayIndexOutOfBoundsException ex) {
			JOptionPane.showMessageDialog(null,
					"No layers are added !");
		} catch (Exception ex) {
			ex.printStackTrace();
			//JOptionPane.showMessageDialog(null, ex.getMessage());
		}
	}
	
	
	private void configureDisplay() {
		content = new MapContent();
		mapPane = new JMapPane(content);
		mapPane.setEnabled(true);
		content.setTitle("GIS APPLICATION");
		mapPane.setToolTipText("Map Area");
		this.setMapContent(content);
		

		// Create Buttons
		
		printBtn = new JButton("Print");
		
		refreshBtn = new JButton("Reset");
		refreshBtn.setToolTipText("Reset layout of window");
		
		queryBtn = new JButton("Query");
		
		// Main Panel
		mainPanel = new JPanel();
		Container contentPane = this.getContentPane();
		
		contentPane.setLayout(new BorderLayout());
		contentPane.add(mainPanel, BorderLayout.SOUTH);
		
		this.setTitle("GIS Application");
		this.enableToolBar(true);
		this.enableStatusBar(true);
		this.enableLayerTable(true);
		

		JToolBar toolbar = this.getToolBar();
		toolbar.addSeparator();
		toolbar.add(refreshBtn);
		toolbar.addSeparator();
		//toolbar.add(printBtn);
		toolbar.add(queryBtn);

//		JButton distance = new JButton("Distance");
//        toolbar.add(distance);
//        distance.addActionListener(new ActionListener() {
//
//            public void actionPerformed(ActionEvent e) {
//                distanceflag = true;
//                distance_and_scalefalg = true;
//                dis = 0.0;
//                numberofpoint = 0;
//                final JFrame ftemp = new JFrame("Distance");
//                final JLabel flabel = new JLabel();
//                String step = "To mesuare Distance between two points: \n (1)-Press Mouse Left button on start point. \n (2)-Press Mouse Left button on destination point. \n (3)-For new session press mouse right button.";
//                JOptionPane.showMessageDialog(null, step);
//                getMapPane().setCursorTool(
//                        new CursorTool() {
//
//                            private int x1,px1,py1,y1,lx1,ly1,x2,y2,px2,py2;
//							private double tempdistance;
//							private int tnumberofpixel;
//							private int lineincentimeter;
//							private double scale;
//
//							@Override
//                            public void onMouseClicked(MapMouseEvent ev) {
//                                if (SwingUtilities.isLeftMouseButton(ev)) {
//                                    if (distanceflag == true) {
//                                        distanceflag = false;
//                                        x1 = ev.getMapPosition().getX();
//                                        px1 = ev.getX();
//                                        py1 = ev.getY();
//                                        y1 = ev.getMapPosition().getY();
//                                        lx1 = ev.getXOnScreen();
//                                        ly1 = ev.getYOnScreen();
//                                    } else {
//                                        String str;
//                                        //JFrame ftemp=new JFrame("Distance");
//                                        ftemp.setVisible(false);
//                                        Graphics g = getGraphics();
//                                        //Graphics scaleg=scalepanel.getGraphics();
//
//                                        g.drawLine(lx1, ly1, ev.getXOnScreen(), ev.getYOnScreen());
//                                        lx1 = ev.getXOnScreen();
//                                        ly1 = ev.getYOnScreen();
//                                        x2 = ev.getMapPosition().getX();
//                                        y2 = ev.getMapPosition().getY();
//                                        px2 = ev.getX();
//                                        py2 = ev.getY();
//                                        dis = dis + Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
////                                        tempdistance=dis;
//                                        numberofpoint += Math.sqrt(Math.pow(px1 - px2, 2) + Math.pow(py1 - py2, 2));
//                                        System.out.println("numberofpoint = " + numberofpoint);
//                                        if (distance_and_scalefalg == true) {
//                                            distance_and_scalefalg = false;
//                                            tempdistance = dis;
//                                            tnumberofpixel = numberofpoint;
//                                            lineincentimeter = tnumberofpixel / 28;
//                                            scale = tempdistance / lineincentimeter;
//                                        }
////
//                                        System.out.println("lineincentimeter = " + lineincentimeter);
//                                        x1 = ev.getMapPosition().getX();
//                                        y1 = ev.getMapPosition().getY();
//                                        px1 = ev.getX();
//                                        py1 = ev.getY();
//                                        System.out.println("x1=" + x1 + "  " + "y1=" + y1);
//                                        System.out.println("x2=" + x2 + "  " + "y2=" + y2);
//                                        System.out.println("distance=" + dis + " Meters");
//                                        scalevalue.setVisible(true);
//                                        scalevalue.setText("Scale :  1 cm = " + scale + " Meters");
//                                        toolbar.add(scalevalue, BorderLayout.EAST);
//                                        flabel.setText("Distance = " + dis + " Meters");
//                                        JPanel fpanel = new JPanel(new FlowLayout());
//                                        JButton meterbtn = new JButton("Meters");
//                                        JButton kmbtn = new JButton("KiloMeters");
//                                        JButton milebtn = new JButton("Miles");
//                                        JButton yardbtn = new JButton("Yards");
//                                        meterbtn.addActionListener(new ActionListener() {
//
//                                            public void actionPerformed(ActionEvent e) {
//                                                flabel.setText("Distance = " + dis + " Meters");
//                                                scalevalue.setText("Scale :  1 cm = " + scale + " Meters");
//                                            }
//                                        });
//
//                                        kmbtn.addActionListener(new ActionListener() {
//
//                                            public void actionPerformed(ActionEvent e) {
//                                                flabel.setText("Distance = " + dis / 1000 + " KiloMeters");
//                                                scalevalue.setText("Scale :  1 cm = " + scale / 1000 + " Km");
//                                            }
//                                        });
//
//                                        milebtn.addActionListener(new ActionListener() {
//
//                                            public void actionPerformed(ActionEvent e) {
//                                                flabel.setText("Distance = " + dis * 0.00062 + " Miles");
//                                                scalevalue.setText("Scale :  1 cm = " + scale * 0.00062 + " Miles");
//                                            }
//                                        });
//
//
//                                        yardbtn.addActionListener(new ActionListener() {
//
//                                            public void actionPerformed(ActionEvent e) {
//                                                flabel.setText("Distance = " + dis * 1.094 + " Yards");
//                                                scalevalue.setText("Scale :  1 cm = " + scale * 1.094 + " Yards");
//                                            }
//                                        });
//
//                                        fpanel.add(meterbtn);
//                                        fpanel.add(kmbtn);
//                                        fpanel.add(milebtn);
//                                        fpanel.add(yardbtn);
//                                        ftemp.add(flabel, BorderLayout.CENTER);
//                                        ftemp.add(fpanel, BorderLayout.SOUTH);
//                                        ftemp.setSize(400, 200);
//                                        ftemp.setVisible(true);
//
//                                    }
//                                } else {
//                                    distanceflag = true;
//                                    distance_and_scalefalg = true;
//                                    scalevalue.setVisible(false);
//                                    dis = 0.0;
//                                    numberofpoint = 0;
//                                    setMapContext(context);
//                                    repaint();
//                                }
//                            }
//                        });
//            }
//        });
		this.setJMenuBar(new CustomMenuBar(mapPane));
		this.setExtendedState(MAXIMIZED_BOTH);
		this.setMinimumSize(new Dimension(800,800));
		
		this.setIconImage(java.awt.Toolkit.getDefaultToolkit().createImage("res\\img\\icon.png"));
		this.setVisible(true);

	}

	private static String getFieldForColour(SimpleFeatureSource source)
			throws Exception {

		String selectedField = new String();
		String[] fieldNames = new String[source.getSchema().getAttributeCount()];
		int k = 0;
		for (AttributeDescriptor desc : source.getSchema().getAttributeDescriptors()) {
			fieldNames[k++] = desc.getLocalName();
		}

		selectedField = JOptionPane.showInputDialog(null,
				"Choose an attribute for colouring", "Feature attribute",
				JOptionPane.PLAIN_MESSAGE, null, fieldNames, fieldNames[0])
				.toString();
		return selectedField;
	}

	@SuppressWarnings("unchecked")
	private static Style createStyle(SimpleFeatureSource source,
			String fieldName) throws Exception {
		
		StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory();
		FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();

		Function colourFn = ff.function("colorlookup", ff.literal(source), ff.property(fieldName));
		
		Stroke stroke = styleFactory.createStroke(colourFn, ff.literal(1.0f), // line
				// width
				ff.literal(1.0f)); // opacity
		
		Fill fill = styleFactory.createFill(colourFn, ff.literal(1.0f)); // opacity
		
		Class<?> geomClass = source.getSchema().getGeometryDescriptor().getType().getBinding();
		Symbolizer sym = null;
		Geometries geomType = Geometries.getForBinding((Class<? extends Geometry>) geomClass);
		
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

	public static void main(String[] args) throws Exception {
		new Main();

	}
}
