import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.StringBuilder;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

// extract data for the keyword from local or url
public class ExtractData {
	public static int itemNumber = 0; // count the number of item whenever load result page. It is for the layout of the panel
	
	public ExtractData(String keyword) throws Exception{
		// the file path of the local data
		File f = new File(Main.path + "/" + keyword);
		if (f.exists()) { // if local exist the data, then take the data from local
			ItemList.load(keyword);
			ArrayList<Item> list = ItemList.search(keyword);
			for (int i = 0; i < list.size(); i++) {
				ImageIcon image = new ImageIcon(list.get(i).getImage());
				UserInterface.addToSearchPage(list.get(i).getName(), list.get(i).getColor(), list.get(i).getPrice(), image);
				itemNumber++;
			}
			itemNumber = 0;
		} else { // if local does not exist the data, then take the data from the html file that stored by WebpageReader class
			BufferedReader inFile = new BufferedReader(new FileReader(Main.htmlPath + "/" + keyword + ".html"));
			StringBuilder searchPage = new StringBuilder();
			String line;
			while ((line = inFile.readLine()) != null) {
				searchPage.append(line);
			}
			
			// use regex to get the information that we need
			Matcher nameMat = Pattern.compile("<span data-test=\"ProductCard__PlpName\" class=\"sc-5sgtnq-3 dMAnEc\">(.*?)</span>").matcher(searchPage);
			Matcher colorMat = Pattern.compile("<span data-test=\"ProductCard__ProductColor\" class=\"sc-5sgtnq-4 dMAnEd\">(.*?)</span>").matcher(searchPage);
			Matcher priceMat = Pattern.compile("<span data-test=\"ProductCard__ProductFinalPrice\">(.*?)</span>").matcher(searchPage);
			Matcher imageMat = Pattern.compile("\"small_image\":\"(.*?)\"").matcher(searchPage);
			while (nameMat.find() && colorMat.find() && priceMat.find() && imageMat.find()) {
				String name = nameMat.group(1);
				String color = colorMat.group(1).replace("amp;", "");
				String price = priceMat.group(1);
				String image = imageMat.group(1);
				
				// store the information to item class
				Item item = new Item(name, color, price, Main.imagePath + "/" + image.substring(image.lastIndexOf('/') + 1));
				ItemList.add(keyword, item);
				
				// read image from url
				URL url = new URL("https://media.endclothing.com/media/f_auto,q_auto:eco,w_400,h_400/prodmedia/media/catalog/product" + image);
				BufferedImage img = ImageIO.read(url);
				UserInterface.addToSearchPage(name, color, price, new ImageIcon(img));
				itemNumber++;
				
				// store the image to local file
				File filePath = new File(Main.imagePath + "/");
				if(!filePath.exists()){
					filePath.mkdir();
		        }
				File file = new File(filePath + image.substring(image.lastIndexOf('/')));
				ImageIO.write(img, "jpg", file);
			}
			itemNumber = 0;
			ItemList.print(keyword);
			inFile.close();
		}
	}
}
