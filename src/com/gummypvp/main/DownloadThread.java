package com.gummypvp.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class DownloadThread extends Thread {
	
	protected static char[] characterDatabase = new char[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0' };
	
	private static int pics = 0;
	
    //The url of the website
    private final static String webSiteURL = "https://prnt.sc/";
    
    //The path of the folder that you want to save the images to
    private String folderPath = "";
    
    private static String currentImage = "lspa18";
	
    public boolean running = true;
    
    private int amountToDownload = 1;
    
    private int downloaded = 0;
    
    int secondsBetweenPings = 1;
    
	public DownloadThread(String folderPath, int secondsBetweenPings, int amountToDownload) {
		
		if (amountToDownload < 1) {
			amountToDownload = 1;
		}
		
		this.folderPath = folderPath;
		
		this.secondsBetweenPings = secondsBetweenPings;
		
		this.amountToDownload = amountToDownload;
	}
	
	public void start() {
		super.start();
	}

    public void run() {
    	
    	while (running) {
    		
    		if (downloaded >= amountToDownload) {
    			ScraperInterface.outputToLog("Thread completed after " + downloaded + " downloaded pictures");
    			ScraperInterface.runButton.setText("Run");
    			break;
    		}
    		
    		try {
				Thread.sleep(1000 * secondsBetweenPings);
			} catch (InterruptedException e) {
				ScraperInterface.outputToLog(e.getMessage());
				ScraperInterface.runButton.setText("Run");
			}
    		
            try {
            	
            	String tempString = "";
            	
            	for (int i = 0; i < 6; i++) {
            		tempString += characterDatabase[new Random().nextInt(characterDatabase.length)];
            	}
            	
            	currentImage = tempString;
            	
                //Connect to the website and get the html
                Document doc = Jsoup.connect(webSiteURL + currentImage).userAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36").referrer("https://www.google.com").get();
                
                //Get all elements with img tag ,
                Elements img = doc.getElementsByTag("img");
     
                for (Element el : img) {
     
                    //for each element get the srs url
                    String src = el.attr("src");
                    
                    if (src.startsWith("//")) break;
                    
                    ScraperInterface.outputToLog("Image found: " + currentImage);
                    
                    saveImage(src, folderPath);
                    ScraperInterface.outputToLog("" + (++pics));
                    downloaded++;
                    break;
     
                }
     
            } catch (IOException ex) {
                System.err.println("There was an error");
                Logger.getLogger(Downloader.class.getName()).log(Level.SEVERE, null, ex);
                ScraperInterface.outputToLog(ex.getMessage());
                ScraperInterface.runButton.setText("Run");
            }
    	}

    }
    
    public static void saveImage(String imageUrl, String destinationFile) throws IOException {
        URL url = new URL(imageUrl);
        URLConnection connection = url.openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
        connection.connect();
        InputStream is = connection.getInputStream();
        
        OutputStream os = new FileOutputStream(new File(destinationFile + "\\" + currentImage + ".png"));

        byte[] b = new byte[2048];
        int length;

        while ((length = is.read(b)) != -1) {
            os.write(b, 0, length);
        }

        is.close();
        os.close();
    }
	
}
