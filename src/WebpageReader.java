import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.sql.Date;

import javax.imageio.ImageIO;


public class WebpageReader{
    public WebpageReader(String urlString, String output, String path) throws Exception {
		File filePath = new File(path);
		if(!filePath.exists()){
            filePath.mkdir();
        }
        if (urlString == null) {
            System.out.println("No URL inputted.");
            System.exit(1);
        }
        
        URL url = new URL(urlString);
		URLConnection connection = url.openConnection();
		PrintWriter outputFile = new PrintWriter(new FileWriter(output, true));
		printURLinfo(connection, outputFile);
        
        String nameOfFile;
        if (urlString.contains("us/catalogsearch/result/?")) {
			nameOfFile = "/" + urlString.substring(urlString.lastIndexOf('=') + 1) + ".html";
		} else {
			nameOfFile = urlString.substring(urlString.lastIndexOf('/'));
		}
		
		// if the url is html, htm or txt files
        if (urlString.endsWith(".html") || urlString.endsWith(".htm") || urlString.endsWith(".txt") || urlString.contains("us/catalogsearch/result/?")) {
            BufferedReader reader = read(urlString);
            File file = new File(filePath + nameOfFile);
            PrintWriter outFile = new PrintWriter(file);
            String line;
            while ((line = reader.readLine()) != null) {
                outFile.println(line);
            } // while
            reader.close();
            outFile.close();
            
    	// if the url is image files
        } else if (urlString.endsWith(".jpg") || urlString.endsWith(".jpeg") || urlString.endsWith(".gif")) {
            File outputImageFile = new File(filePath + nameOfFile);
            BufferedImage image = ImageIO.read(url);
            ImageIO.write(image, "jpg", outputImageFile);
			outputFile.println("Name of file: " + nameOfFile.substring(1));
			
		// if the url is pdf or doc files
        } else if (urlString.endsWith(".pdf") || urlString.endsWith(".docx") || urlString.endsWith(".doc")) {
			InputStream inFile = url.openStream();
			FileOutputStream outFile = new FileOutputStream(new File(filePath + nameOfFile));
			int length = -1;
			byte[] buffer = new byte[1024];
			while ((length = inFile.read(buffer)) > -1) {
				outFile.write(buffer, 0, length);
			}
			inFile.close();
			outFile.close();
		}
    } // contructor

    private BufferedReader read(String urlString) throws Exception {
        return new BufferedReader(
                new InputStreamReader(
                        new URL(urlString).openStream()));
    } // read
    
	private void printURLinfo(URLConnection uc, PrintWriter outFile) throws IOException {
		outFile.println();
		outFile.println(uc.getURL().toExternalForm() + ":");
		outFile.println("Content Type: " + uc.getContentType());
		outFile.println("Content Length: " + uc.getContentLength());
		outFile.println("Last Modified: " + new Date(uc.getLastModified()));
		outFile.println("Expiration: " + (double)uc.getExpiration() / 1000 / 60 / 60 + " hours");
		outFile.println("Content Encoding: " + uc.getContentEncoding());
	} // printURLinfo
} // WebpageReader