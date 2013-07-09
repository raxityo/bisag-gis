package org.bisag.legend;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import org.bisag.geotools.JMapFrame;

/**
* GUI wizard to show a wizard to implement the LegendCreation.
* 
* @author: Rakshit Majithiya
*/

public class LegendCreationWizard extends JFrame {
	public static boolean valueChanged;
	public JScrollPane previewPanel,editPane;
	public JPanel editArea,titlePanel,buttons;
	public Dimension d;
	public LegendColorTable legendColorTable;
	public Object[] keys;
	public JTextField titleField,widthField,heightField ,editValueFields[];
	public JComboBox positionOfTable ;
	JButton insertTable;
	public Map<Object,Color> ascendingLookup,descendingLookup,unorderedLookup;
	public JPanel[] editingRow;
	public JRadioButton descendingOrderRadioButton,ascendingOrderRadioButton,originalOrderRadioButton;
	public Map<Object, Color> newLookup;
	
	public LegendCreationWizard(Map<Object, Color> lookup, String fieldName) {
		
		this.unorderedLookup = lookup;
		this.ascendingLookup = new TreeMap<Object,Color>(lookup);
		this.descendingLookup = new TreeMap<Object, Color>(this.ascendingLookup).descendingMap();
		
		JMapFrame.splitPane.setDividerLocation(0);
		JMapFrame.getMapPane().removeAll();
		Container c = this.getContentPane();
		c.setLayout(new SpringLayout());
		c.add(getEditPanel(ascendingLookup, fieldName));
		JPanel tempDim  = new JPanel(new FlowLayout(FlowLayout.LEFT));
		tempDim.add(getDimensionsPanel());
		c.add(tempDim);
		c.add(getButtonsPanel());
		
		SpringUtilities.makeCompactGrid(c, 3, 1, 10, 10, 5, 5);
		this.pack();
		this.setResizable(false);
		this.setVisible(true);
		
	}
	

	public JPanel getEditPanel(Map<Object, Color> lookup, String fieldName) {
		 
		legendColorTable = new LegendColorTable(lookup, fieldName,0,0,false);
		legendColorTable.setOpaque(true);
		previewPanel = new JScrollPane(legendColorTable);
		previewPanel.setAutoscrolls(true);
		previewPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Color.GRAY),
				"Preview Of Legend Table", TitledBorder.CENTER,
				TitledBorder.TOP));
		previewPanel.setBackground(Color.WHITE);
		
		d = new Dimension(legendColorTable.getWidth() < 500 ? 500 : legendColorTable.getWidth(),
				legendColorTable.getHeight() > 500 ? 500 : legendColorTable.getHeight());
		d = new Dimension(legendColorTable.getWidth() > 500 ? 500 : legendColorTable.getWidth(),
				(int) d.getHeight());
		previewPanel.setMaximumSize(d);
		previewPanel.setPreferredSize(d);
		
		// setup edit panel
		editArea = new JPanel();
		editArea.setLayout(new SpringLayout());
		editArea.setBackground(Color.WHITE);
		
		titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		titlePanel.add(new JLabel("Title : "));
		titleField = new JTextField(fieldName,10);
		titleField.addFocusListener(new FocusAdapter());
		titlePanel.add(titleField);
		titlePanel.setSize(titlePanel.getPreferredSize());
		editArea.add(titlePanel);
		
		
		
		
		keys = lookup.keySet().toArray();

		editingRow = new JPanel[keys.length];
		editValueFields = new JTextField[keys.length];
		
		
		
		for(int i = 0 ; i<keys.length ; i++){
			editValueFields[i] = new JTextField(keys[i].toString(),10);
			editValueFields[i].addFocusListener(new FocusValueAdapter(editValueFields[i]));

			
			
			editingRow[i] = new JPanel(new FlowLayout(FlowLayout.LEFT));
			JPanel colorp = new JPanel();
			colorp.setBackground(lookup.get(keys[i]));
			colorp.setMaximumSize(new Dimension(20,20));
			colorp.setPreferredSize(new Dimension(20,20));
			colorp.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			
			editingRow[i].add(colorp);
			editingRow[i].add(editValueFields[i]);
			
			editArea.add(editingRow[i]);
			editingRow[i].setBackground(Color.WHITE);
		}
		SpringUtilities.makeCompactGrid(editArea, keys.length +1 , 1, 6, 6, 6, 6);
		
		editPane = new JScrollPane(editArea);
		editPane.setAutoscrolls(true);
		editPane.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Color.GRAY),
				"Edit Labels", TitledBorder.CENTER,
				TitledBorder.TOP));
		editPane.setBackground(Color.WHITE);
		if(d.getWidth() < 400)
			d.setSize(400, d.getHeight());
		editPane.setMaximumSize(d);
		editPane.setPreferredSize(d);

		JPanel container = new JPanel();
		container.setLayout(new SpringLayout());
		container.add(previewPanel);
		container.add(editPane);
		Dimension cDim = new Dimension((int)d.getWidth()*2  + 40,(int)d.getHeight() + 50);
		
		container.setMaximumSize(cDim);
		container.setPreferredSize(cDim);
		
		SpringUtilities.makeCompactGrid(container, 1, 2, 10, 10, 10,10);
		container.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Preview & Edit", TitledBorder.LEFT, TitledBorder.TOP));
		return container;
	}

	public JPanel getDimensionsPanel(){
		JPanel dimensionsPanel = new JPanel(new SpringLayout());
		dimensionsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Dimensions & Alignment", TitledBorder.LEFT, TitledBorder.TOP));
		
		
		/*
		 * Logical grouping of Radio buttons
		 */
		ButtonGroup radioButtonGroup = new ButtonGroup();
		ascendingOrderRadioButton = new JRadioButton("Ascending");
		descendingOrderRadioButton = new JRadioButton("Descending");
		originalOrderRadioButton = new JRadioButton("Original order as in database");
		
		ascendingOrderRadioButton.setSelected(true);
		radioButtonGroup.add(ascendingOrderRadioButton);
		radioButtonGroup.add(descendingOrderRadioButton);
		radioButtonGroup.add(originalOrderRadioButton);
		
		//physical grouping
		JPanel radioPanel = new JPanel(new FlowLayout());
		radioPanel.add(ascendingOrderRadioButton);
		radioPanel.add(descendingOrderRadioButton);
		radioPanel.add(originalOrderRadioButton);
		
		dimensionsPanel.add(new JLabel("Order the fields"));
		dimensionsPanel.add(radioPanel);
		
		widthField = new JTextField((int)d.getWidth() + "",4);
		widthField.addFocusListener(new FocusAdapter());
		heightField = new JTextField((int)d.getHeight() + "",4);
		heightField.addFocusListener(new FocusAdapter());
		
		dimensionsPanel.add(new JLabel("Width : "));
		dimensionsPanel.add(widthField);
		dimensionsPanel.add(new JLabel("Height: "));
		dimensionsPanel.add(heightField);
		
		positionOfTable = new JComboBox();
		positionOfTable.addItem("Along with mapContent");
		positionOfTable.addItem("In Separate Sidebar");
		
		
		dimensionsPanel.add(new JLabel("Position of the Legend Table : "));
		dimensionsPanel.add(positionOfTable);
		
		dimensionsPanel.setMaximumSize(new Dimension(580,150));
		dimensionsPanel.setPreferredSize(new Dimension(580,150));
		SpringUtilities.makeCompactGrid(dimensionsPanel, 4, 2, 5,5,5,5);
		
		return dimensionsPanel;
	}
	
	public JPanel getButtonsPanel(){
		JPanel buttonp = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		
		
		insertTable = new JButton("Insert Table");
		insertTable.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
					finalizeWizard();
			}
		});
		
		JButton cancle = new JButton("Cancle");
		cancle.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		buttonp.add(insertTable);
		buttonp.add(cancle);
		return buttonp;
	}


	public void finalizeWizard() {
		newLookup = new HashMap<Object, Color>();
		
		if(ascendingOrderRadioButton.isSelected()){
			if(valueChanged){
				for(int i = 0; i < editValueFields.length; i++){
					if(!editValueFields[i].getText().trim().equals(""))
						newLookup.put(editValueFields[i].getText(), ascendingLookup.get(keys[i]));
				}
				newLookup = new TreeMap<Object,Color>(newLookup);
			}
			else
				newLookup = new HashMap<Object, Color>(ascendingLookup);
		}
		else if(descendingOrderRadioButton.isSelected())
		{
			if(valueChanged){
				for(int i = 0; i < editValueFields.length; i++){
					if(!editValueFields[i].getText().trim().equals(""))
						newLookup.put(editValueFields[i].getText(), descendingLookup.get(keys[i]));
				}
				newLookup = new TreeMap<Object,Color>(newLookup).descendingMap();
			}
			else
				newLookup = new TreeMap<Object, Color>(descendingLookup).descendingMap();
		}
		else if(originalOrderRadioButton.isSelected())
			for(int i = 0; i < editValueFields.length; i++){
				if(!editValueFields[i].getText().trim().equals(""))
					newLookup.put(editValueFields[i].getText(), unorderedLookup.get(keys[i]));
			}
		
		JPanel legendTable = new LegendColorTable(newLookup, titleField.getText(), Integer.parseInt(widthField.getText()), Integer.parseInt(heightField.getText()),positionOfTable.getSelectedIndex() == 0 ? true : false);
		
		JMapFrame.setLegendColorTable(legendTable, Integer.parseInt(widthField.getText()), Integer.parseInt(heightField.getText()), positionOfTable.getSelectedIndex() == 0 ? false : true);
		dispose();
	}

}
class FocusValueAdapter implements FocusListener{
		String oldVal;
		JTextField sourceField;
		public FocusValueAdapter(JTextField sourceField){
			this.sourceField = sourceField;
		}
		@Override
		public void focusGained(FocusEvent e) {
			sourceField.selectAll();
			oldVal = sourceField.getText().trim();
		}
		
		@Override
		public void focusLost(FocusEvent e) {
			if(!sourceField.getText().trim().equals(oldVal))
			{
				LegendCreationWizard.valueChanged = true;
				System.out.println(oldVal + " is changed to " + sourceField.getText().trim());
			}
			
		}

}
class FocusAdapter implements FocusListener{
	
	@Override
	public void focusGained(FocusEvent e) {
		JTextField f =  (JTextField)e.getSource();
		f.selectAll();
	}
	
	@Override
	public void focusLost(FocusEvent e) {}
}