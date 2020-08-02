import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
	static String input = ""; // store input file name
	static String output1 = ""; // store output file name
	static String output2 = ""; // store the transaction log file name
	static String path = ""; // store the path of download data
	static String accFile = ""; // store the account file name
	static String htmlPath = ""; // store the html file path
	static String imagePath = ""; // store the image file path
	static boolean p = false; // if directs the program to process the input and print to output
	                          // without starting up the GUIs.
	
    public static void main(String args[]) throws Exception {
		for (int i = 0; i < args.length; i++) {
			if (args[i].startsWith("-i=")) {
				input = args[i].substring(3);
			} else if (args[i].startsWith("-o=")) {
				output1 = args[i].substring(3);
			} else if (args[i].startsWith("-o2=")) {
				output2 = args[i].substring(4);
			} else if (args[i].startsWith("-path=")) {
				path = args[i].substring(6);
			} else if (args[i].startsWith("-a=")) {
				accFile = args[i].substring(3);
			} else if (args[i].startsWith("-h=")) {
				htmlPath = args[i].substring(3);
			} else if (args[i].startsWith("-img=")) {
				imagePath = args[i].substring(5);
			} else if (args[i].startsWith("-p=")) {
				p = Boolean.parseBoolean(args[i].substring(3));
			} else {
				System.out.println("invalid arguments");
			}
		} // for
		
		if (output2.equals("")) {
			output2 = "transactionLog.txt";
		} else if (path.equals("")) {
			path = "itemList";
		} else if (accFile.equals("")) {
			accFile = "acc.info";
		} else if (htmlPath.equals("")) {
			htmlPath = "html";
		} else if (imagePath.equals("")) {
			imagePath = "image";
		}
    	
		if (p) {
			directProcess(input, output1);
		} else {
			new ItemList();
	    	new UserAccount();
	    	UserAccount.updateAccount("admin", "nimda", "admin", new ArrayList<String>());
			new UserInterface();
		}
    } // main
    
    public static void directProcess(String input, String output) {
    	try {
    		BufferedReader inFile = new BufferedReader(new FileReader(input));
    		String url;
    		while ((url = inFile.readLine()) != null) {
    			// create the stream that read and write the input and output
    			BufferedReader reader = new BufferedReader(new InputStreamReader(new URL("https://www.endclothing.com/us/catalogsearch/result/?q=" + url).openStream()));
    			PrintWriter outFile = new PrintWriter(new FileWriter(output, true));
    			// get the html data and store it into variable page
    			String line;
    			StringBuilder page = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    page.append(line);
                } // while
                
                // use regex to print the information that we need into the output file
    			Matcher nameMat = Pattern.compile("<span data-test=\"ProductCard__PlpName\" class=\"sc-5sgtnq-3 dMAnEc\">(.*?)</span>").matcher(page);
    			Matcher colorMat = Pattern.compile("<span data-test=\"ProductCard__ProductColor\" class=\"sc-5sgtnq-4 dMAnEd\">(.*?)</span>").matcher(page);
    			Matcher priceMat = Pattern.compile("<span data-test=\"ProductCard__ProductFinalPrice\">(.*?)</span>").matcher(page);
    			while (nameMat.find() && colorMat.find() && priceMat.find()) {
    				String name = nameMat.group(1);
    				String color = colorMat.group(1).replace("amp;", "");
    				String price = priceMat.group(1);
    				outFile.println(name + " | " + color + " | " + price);
    			}
    			
    			reader.close();
    			outFile.close();
    		}
    		inFile.close();
    	} catch (Exception ex) {}
    }
}