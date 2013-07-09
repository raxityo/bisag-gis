package org.bisag.tools;

import org.bisag.legend.SpringUtilities;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;

import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.styling.Style;
import org.geotools.swing.table.FeatureCollectionTableModel;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.filter.Filter;

/**
* The query window to perform different queries in map.
* 
* @author: Rakshit Majithiya
*/

public class QueryWindow extends JFrame {
	 

	

	private JTable resultsTable;

	public JList featuresList;
	public JScrollPane featuresPane,resultsTablePane;
	public JTextField valueField;
	public static JTextArea queryTextArea;
	public MapContent mapContent;
	public Layer layer;
	public JButton okButton;
	public SimpleFeatureCollection featureCollection;
	public static String listvalue;

	public QueryWindow(MapContent mapContent,Layer layer){
		this.mapContent = mapContent;
		this.layer = layer;
		initComponents();
		queryFeatures();
		this.pack();
		this.setSize(getPreferredSize());
		this.setVisible(true);
		this.setLocation(200,100);
		this.setTitle("Query Window");
	}

	private void initComponents() {
		
		//Initialize The List
		
		SimpleFeatureSource source = (SimpleFeatureSource) layer.getFeatureSource();
		String[] fieldNames = new String[source.getSchema().getAttributeCount()];
		int k = 0;
		for (AttributeDescriptor desc : source.getSchema().getAttributeDescriptors())
			fieldNames[k++] = desc.getLocalName();

		featuresList = new JList(fieldNames);
		
		featuresList.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent event) {
				listvalue = featuresList.getSelectedValue().toString();
			}
		});
		featuresList.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(FocusEvent fe){
				if(queryTextArea.getText().equals("INCLUDE")){
					queryTextArea.setText("");
				}
				queryTextArea.append(listvalue);
			}
		});
		
		featuresPane = new JScrollPane(featuresList);
		featuresPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Features", TitledBorder.TOP, TitledBorder.LEFT));
		
		
		
		//initialize button panel and activate the buttons
		
		JPanel buttonPanel = new JPanel(new SpringLayout());
		buttonPanel.add(new JButton("="));
		buttonPanel.add(new JButton("<"));
		buttonPanel.add(new JButton(">"));
		buttonPanel.add(new JButton("LIKE"));
		buttonPanel.add(new JButton("IN"));
		buttonPanel.add(new JButton("NOT IN"));
		buttonPanel.add(new JButton("%"));
		buttonPanel.add(new JButton("<="));
		buttonPanel.add(new JButton(">="));
		buttonPanel.add(new JButton("AND"));
		buttonPanel.add(new JButton("OR"));
		buttonPanel.add(new JButton("NOT"));
		
		buttonPanel.setSize(buttonPanel.getPreferredSize());
		SpringUtilities.makeCompactGrid(buttonPanel, 4, 3, 5, 5, 5, 5);
		activateButtonPanel(buttonPanel);
		
		
		//initialize and activate value field
		valueField = new JTextField("Enter Your Value Here");
		valueField.setMaximumSize(valueField.getPreferredSize());
		valueField.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent arg0) {
				queryTextArea.append(" '"+valueField.getText()+"' ");
			}

			@Override
			public void focusGained(FocusEvent arg0) {
				if(valueField.getText().equals("Enter Your Value Here"));
					valueField.setText("");
				
			}
		});
		
		
		// Right panel for Buttons and keys.
		JPanel rightPanel = new JPanel(new SpringLayout());
		rightPanel.add(buttonPanel);
		rightPanel.add(valueField);
		SpringUtilities.makeCompactGrid(rightPanel, 2, 1, 10, 10, 5, 5);
		
		// Top panel (Left + Right)
		JPanel topPanel = new JPanel(new SpringLayout());
		topPanel.add(featuresPane);
		topPanel.add(rightPanel);
		topPanel.setSize(topPanel.getPreferredSize());
		
		SpringUtilities.makeCompactGrid(topPanel, 1, 2, 10, 10, 20, 0);
		
		//Panel for Text Area and OK button
		queryTextArea = new JTextArea("INCLUDE",2,50);
		queryTextArea.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		queryTextArea.setMaximumSize(queryTextArea.getPreferredSize());
		
		JPanel queryBtns = new JPanel();
		queryBtns.setLayout(new SpringLayout());
		
		okButton = new JButton("Query");
		okButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent ae) {
					queryFeatures();
					
					if(getFeatureCollection().size()!=0)
						JOptionPane.showMessageDialog(null,	"Query Successful\n" + getFeatureCollection().size() + "Rows returned");
					else
						JOptionPane.showMessageDialog(null, "No rows returned");
					
			}
		});
		JButton clearButton = new JButton("Clear");
		clearButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent ae) {
				queryTextArea.setText("INCLUDE");
				queryFeatures();
			}
		});
		
		queryBtns.add(okButton);
		queryBtns.add(clearButton);
		SpringUtilities.makeCompactGrid(queryBtns, 2, 1, 2, 2, 5, 5);
		
		JPanel textAreaPanel = new JPanel(new SpringLayout());
		textAreaPanel.add(queryTextArea);
		textAreaPanel.add(queryBtns);

		textAreaPanel.setSize(textAreaPanel.getPreferredSize());
		SpringUtilities.makeCompactGrid(textAreaPanel, 1, 2, 10, 10, 5, 0);
		
		//Panel for Results Table
		resultsTable = new JTable();
		resultsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		resultsTable.setModel(new DefaultTableModel(5, 5));
		resultsTable.setPreferredScrollableViewportSize(new Dimension(500, 200));
		resultsTablePane = new JScrollPane(resultsTable);
		resultsTablePane.setMaximumSize(resultsTablePane.getPreferredSize());
		
		
		//Panel for Control buttons
		JButton displayButton = new JButton("Display in Map");
		displayButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try{
					SimpleFeatureCollection featureCollection = getFeatureCollection();
					Style style = org.geotools.styling.SLD.createSimpleStyle(featureCollection.getSchema(),Color.CYAN);
					Layer layer = new FeatureLayer(featureCollection, style);
					layer.setTitle("Querried Features");
					mapContent.addLayer(layer);
					for(int i = 0; i<mapContent.layers().size() -1; i++)
						mapContent.layers().get(i).setVisible(false);
					dispose();
				}
				catch(Exception ex){}
						
			}
		});
		JButton cancleButton = new JButton("Cancle");
		cancleButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		controlsPanel.add(displayButton);
		controlsPanel.add(cancleButton);
		
		Container c = this.getContentPane();
		c.setLayout(new SpringLayout());
		
		c.add(topPanel);
		c.add(textAreaPanel);
		c.add(resultsTablePane);
		c.add(controlsPanel);
		
		SpringUtilities.makeCompactGrid(c, 4, 1, 0, 0, 0, 10);
	}

	private void activateButtonPanel(JPanel buttonPanel){
		Component[] buttons = buttonPanel.getComponents();
		
		for(int i = 0; i < buttons.length; i++){
			((JButton)buttons[i]).addActionListener(new ButtonAction());
		}
	}
		
	private void queryFeatures(){
		FeatureCollectionTableModel model = new FeatureCollectionTableModel(getFeatureCollection());
		resultsTable.setModel(model);
		
	}
	
	public SimpleFeatureCollection getFeatureCollection() {
		try{
			SimpleFeatureSource source = (SimpleFeatureSource) mapContent.layers().get(0).getFeatureSource();
			Filter filter = CQL.toFilter(queryTextArea.getText());
			return (SimpleFeatureCollection) source.getFeatures(filter);
		}catch(Exception ex){
			JOptionPane.showMessageDialog(null, "Invalid Query Entered");
		}
		return null;
	}

}

class ButtonAction implements ActionListener{

	@Override
	public void actionPerformed(ActionEvent e) {
		QueryWindow.queryTextArea.append(" "+e.getActionCommand()+" ");
		
	}
	
}