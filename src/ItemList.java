import java.io.*;                    // import io library for printing each transaction to transaction log
import java.util.HashMap;            // import hashmap to simulate the database
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;   // make the time formatted

public class ItemList {
    private static HashMap<String, ArrayList<Item>> hash; // the hashmap works like a database to store the list of items for each keyword
    public static PrintWriter transactionLog;
    private static String date; // used for the timestamp

    //constructor
    public ItemList() throws IOException {
        hash = new HashMap<String, ArrayList<Item>>();
        transactionLog = new PrintWriter(new FileWriter(Main.output2, true));
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        date = dateFormat.format(new Date());
    }

    // add a item to the keyword in storage structure
    public static void add(String keyword, Item item) {
        ArrayList<Item> list = hash.get(keyword);
        if (list == null) {
            list = new ArrayList<Item>();
        }
        list.add(item);
        hash.put(keyword, list);
        printToTransactionLog("ADD", item);
    }

    public void delete(String keyword, Item item) {
        hash.get(keyword).remove(item);
        printToTransactionLog("DELETE", item);
    }
    
    public void deleteLocalUserHist() {
    	
    }
    
    // load the local data for the keyword if exist
    public static void load(String keyword) throws Exception{
    	File file = new File(Main.path + "/" + keyword);
    	BufferedReader inFile = new BufferedReader(new FileReader(file));
    	String line;
    	while ((line = inFile.readLine()) != null) {
    		String[] item = line.split("#");
    		add(keyword, new Item(item[0], item[1], item[2], item[3]));
    	}
    	inFile.close();
    }

    // search for the data of a keyword
    public static ArrayList<Item> search(String keyword) {
        ArrayList<Item> list = hash.get(keyword);
        if (list == null) {
            return null;
        }
        for (int i = 0; i < list.size(); i++) {
            printToTransactionLog("SEARCH", list.get(i));
        }
        return list;
    }

    // print every item in the given search keyword to the outFile
    public static void print(String keyword) throws IOException {
    	File filePath = new File(Main.path);
    	if(!filePath.exists()){
            filePath.mkdir();
        }
    	File file = new File(filePath + "/" + keyword);
    	PrintWriter outFile = new PrintWriter(file);
        ArrayList<Item> list = hash.get(keyword);
        for (int i = 0; i < list.size(); i++) {
            outFile.println(list.get(i).getName() + "#" + list.get(i).getColor() + "#" +
                    list.get(i).getPrice() + "#" + list.get(i).getImage());
        }
        outFile.close();
    }

    // print each transaction to the log
    private static void printToTransactionLog(String method, Item item) {
        transactionLog.println(date + " # " + LoginUser.account + " # " + method + " # " + item.getName() + " # " + item.getColor() +
                " # " + item.getPrice() + " # " + item.getImage());
    }
}
