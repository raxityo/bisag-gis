package org.bisag.test;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;


public class LayoutTest extends JFrame {
	JPanel cards;
	JButton back,next;
	int currentCard = 0;
	
	public LayoutTest(){
		
		JPanel editPanel  = new JPanel(new FlowLayout(FlowLayout.CENTER));
		editPanel.add(new JButton("Foo"));
		editPanel.add(new JButton("Foo"));
		editPanel.add(new JButton("Foo"));
		editPanel.add(new JButton("Foo"));
		editPanel.add(new JButton("Foo"));
		
		JPanel secondPanel = new JPanel(new FlowLayout());
		secondPanel.add(new JLabel("Foo"));
		secondPanel.add(new JLabel("Foo"));
		secondPanel.add(new JLabel("Foo"));
		secondPanel.add(new JLabel("Foo"));
		secondPanel.add(new JLabel("Foo"));
		secondPanel.add(new JLabel("Foo"));
		
		cards = new JPanel();
		
		cards.setLayout(new CardLayout());
		
		cards.add(editPanel,"0");
		cards.add(secondPanel,"1");
		
		Container c = getContentPane();
		c.setLayout(new FlowLayout());
		c.add(cards);
		back = new JButton("< Back");
		back.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
					CardLayout cl = (CardLayout)(cards.getLayout());
					currentCard--;
			        cl.show(cards,currentCard + "");
			        if(currentCard == 0){
			        	back.setEnabled(false);
			        }
			        if(next.getText().equals("Finish")){
			        	next.setText("Next >");
			        }
				
			}
		});
		back.setEnabled(false);
		
		next = new JButton("Next >");
		next.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(next.getText().equals("Next >")){
					CardLayout cl = (CardLayout)(cards.getLayout());
					currentCard++;
			        cl.show(cards, currentCard+"");
			        if(currentCard == 1){
			        	next.setText("Finish");
			        }
			        back.setEnabled(true);
				}
				else{
					System.exit(0);
				}
				
			}
		});

		
		
		c.add(back);
		c.add(next);
		
		this.pack();
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
	}
	
	public static void main(String a[]){
		new LayoutTest();
	}
	
}
