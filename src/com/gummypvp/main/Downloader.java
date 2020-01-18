package com.gummypvp.main;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Downloader {
	
	public static Set<DownloadThread> threads;
    
    public static void main(String[] args) {
    	
    	threads = new HashSet<DownloadThread>();
    	
    	new ScraperInterface();
    	
    }
}

class ScraperInterface extends JFrame {
	
	private static DirectoryTextField directoryField;
	private static OutputField output;
	private static DelayInputField delayInputField;
	private static AmountInputField amountInputField;
	public static RunButton runButton;
	
	public ScraperInterface() {
		super("Lightshot Scraper");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(600, 800);
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		JPanel header = new JPanel();
		header.setMaximumSize(new Dimension(600, 250));
		header.setVisible(true);
		setIconImage(new ImageIcon(Downloader.class.getResource("/resources/lightshot.png")).getImage());
		JLabel image = new JLabel(new ImageIcon(Downloader.class.getResource("/resources/lightshot.png")));
		image.setMinimumSize(new Dimension(600, 200));
		image.setAlignmentX(CENTER_ALIGNMENT);
		header.add(image);
		String myString = 
			    "<html><p>This program generates random valid Lightshot URLs and downloads the pictures associated with them. </p>" + 
			    "<p>Abuse will result in a Cloudflare IP ban.</p>" +
			    "<p>Use at own risk.</p>" +
			    "<p>Hover over components to recieve instructions on what they do." +
			    "<p>All rights reserved. Jeremy Gooch 2020</p></html>";
		JLabel caption = new JLabel(myString);
		caption.setMinimumSize(new Dimension(600, 200));
		caption.setAlignmentX(CENTER_ALIGNMENT);
		header.add(caption);
		add(header);
		directoryField = new DirectoryTextField();
		add(directoryField);
		ConfigurationPanel panel = new ConfigurationPanel();
		add(panel);
		delayInputField = new DelayInputField();
		amountInputField = new AmountInputField();
		panel.add(new FileButton());
		runButton = new RunButton();
		panel.add(runButton);
		panel.add(delayInputField);
		panel.add(amountInputField);
		output = new OutputField();
		JScrollPane scrollPane = new JScrollPane(output);
		scrollPane.setMaximumSize(new Dimension(600, 400));
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		add(scrollPane);
		setResizable(false);
		setVisible(true);
	}
	
	public static void outputToLog(String text) {
		output.append("\n" + text);
	}
	
	class OutputField extends JTextArea {
		public OutputField() {
			super("Output...");
			setToolTipText("Verbose readout on what the program is currently doing");
			setAlignmentX(CENTER_ALIGNMENT);
			setMinimumSize(new Dimension(600, 400));
			setMaximumSize(new Dimension(600, 400));
			setEditable(false);
		}
	}
	
	class DelayInputField extends JTextField {
		public DelayInputField() {
			super("1");
			setColumns(4);
			setToolTipText("The amount of time between pings, in seconds - note that very low values could result in faster/immediate bans");
			setAlignmentX(CENTER_ALIGNMENT);
			setEditable(true);
		}
	}
	
	class AmountInputField extends JTextField {
		public AmountInputField() {
			super("1");
			setColumns(4);
			setToolTipText("The amount of pictures to download");
			setAlignmentX(CENTER_ALIGNMENT);
			setEditable(true);
		}
	}
	
	class DirectoryTextField extends JTextField {
		public DirectoryTextField() {
			super("Choose file location to save images...");
			setToolTipText("This is the directory where images will be saved to");
			setAlignmentX(CENTER_ALIGNMENT);
			setMaximumSize(new Dimension(600, 25));
			setEditable(false);
		}
	}
	
	class ConfigurationPanel extends JPanel {
		
		public ConfigurationPanel() {
			setLayout(new FlowLayout());
			setMaximumSize(new Dimension(600, 50));
			setVisible(true);
		}
		
	}
	
	class RunButton extends JButton {
		public RunButton() {
			super("Run");
			setToolTipText("Press this button to run/stop the program");
			setAlignmentX(CENTER_ALIGNMENT);
			addActionListener(new RunButtonListener());
		}
	}
	
	class FileButton extends JButton {
		public FileButton() {
			super("Open folder...");
			setToolTipText("Press this button to choose where the images should be saved to");
			setAlignmentX(CENTER_ALIGNMENT);
			addActionListener(new FileButtonListener(directoryField));
		}
	}
	
	class FileButtonListener implements ActionListener {
		
		final DirectoryTextField field;
		
		public FileButtonListener(DirectoryTextField field) {
			this.field = field;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			
			if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				field.setText(fileChooser.getSelectedFile().getAbsolutePath());
			}
			
		}
		
	}
	
	class RunButtonListener implements ActionListener {
		
		public RunButtonListener() {
			
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			
			RunButton button = (RunButton) e.getSource();
			
			if (button.getText().equalsIgnoreCase("Run")) {
				
				if (!isInt(delayInputField.getText())) {
					ScraperInterface.outputToLog("Please specify a valid integer for delay input field");
					return;
				}
				
				if (!isInt(amountInputField.getText())) {
					ScraperInterface.outputToLog("Please specify a valid integer for amount input field");
					return;
				}
				
				int delayInput = Integer.parseInt(delayInputField.getText());
				int amountInput = Integer.parseInt(amountInputField.getText());
				
				DownloadThread thread = new DownloadThread(directoryField.getText(), delayInput, amountInput);
				
				Downloader.threads.add(thread);
				
				for (DownloadThread initThread : Downloader.threads) {
					initThread.start();
				}
				
				button.setText("Stop");
				
				return;
			}
			
			for (DownloadThread thread : Downloader.threads) {
				thread.running = false;
			}
			
		}
		
		public boolean isInt(String text) {
			
			try {
				Integer.parseInt(text);
				return true;
			} catch (NumberFormatException e) {
				return false;
			}
			
		}
		
	}
	
}