package org.bisag.test;

import javax.swing.*;
import java.util.*;

public class ArraySortingLab {

	public static void main(String args[]){
		int i = 0;
		String[] str = new String[10];
		JOptionPane.showInputDialog("TEST");
		while(i<10)
		{
			str[i] = new String(JOptionPane.showInputDialog("Enter element number: "+(i)));
			System.out.println(str[i++]);
		}
		Arrays.sort(str);
		System.out.println("After Sorting : ");
		for(i = 0;i<10;i++)
			System.out.println(str[i]);
		
	}
}
