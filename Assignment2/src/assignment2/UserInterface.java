package assignment2;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class UserInterface extends JFrame implements ActionListener{
	private JLabel enterURL;
	private JTextField textField;
	private JButton click;
	private String storeURL = "";
	private String message;
	
	public UserInterface(){
		setLayout(null);
		setSize(300, 250);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Jacob Brooks Assignment 1");
		enterURL = new JLabel("Enter any URL");
		click = new JButton("Enter");
		textField = new JTextField();
		enterURL.setBounds(60, 30, 120, 30);
		textField.setBounds(80,60,130,30);
		click.setBounds(100,190,60,30);
		click.addActionListener(this);
		add(click);
		add(textField);
		add(enterURL);
	}
	
	public void actionPerformed(ActionEvent e){
		if(e.getSource() == click){
			String URL = textField.getText();
			try {
				message = retrieveData(URL);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			JOptionPane.showMessageDialog(null, message);
			System.exit(0);
		}
	}
	
	public String retrieveData(String s) throws IOException{
		String message = "";
		Data d = new Data(s);
		ArrayList<String> urls = d.getTopURLs();
		int n = 1;
		for(String s1 : urls){
			message += n + "." + s1 + "\n";
			n++;
		}
		return message;
	}
}
