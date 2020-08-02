import java.util.ArrayList;
import java.util.Properties;
import javax.swing.JOptionPane;
import java.io.*;

public class UserAccount {
	private static Properties pps; // used for storing the account information
	private static String fileName = Main.accFile; // store the account information to this file
	
	// constructor
	public UserAccount() {
		pps = new Properties();
		try {
			File file = new File(fileName);
			if (file.exists()) {
				BufferedReader inFile = new BufferedReader(new FileReader(fileName));
				pps.load(inFile);
				inFile.close();
			} else {
				file.createNewFile();
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "File read error");
			System.exit(0);
		}
	}
	
	// check if the account exist.
	public static boolean checkIfExist(String acc) {
		String pas = pps.getProperty(acc);
		if (pas == null && !acc.equals("admin")) {
			return false;
		}
		return true;
	}
	
	// check password and account if exist and correct
	public static boolean checkPassword(String acc, String pas) {
		if (acc.equals("admin") && pas.equals("nimda")) {
			return true;
		}
		String p = pps.getProperty(acc);
		if (p == null || !pas.equals(p.split("#")[0])) {
			return false;
		}
		return true;
	}
	
	// get user's search history
	public static ArrayList<String> getUserHistory(String acc) {
		ArrayList<String> hist = new ArrayList<String>();
		for(int i = 2; i < pps.getProperty(acc).split("#").length; i++) {
			hist.add(pps.getProperty(acc).split("#")[i]);
		}
		return hist;
	}
	
	// store user's account, password, name and history to local file
	public static void storeAccount() {
		PrintStream ps = null;
		try {
			ps = new PrintStream(fileName);
			pps.list(ps);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "File write error");
			System.exit(0);
		} finally {
			ps.close();
		}
	}
	
	// delete user's search history
	public static void deleteAccountHistory() {
		LoginUser.history.clear();
		updateAccount(LoginUser.account, LoginUser.password, LoginUser.name, LoginUser.history);
		
		try {
			ItemList.transactionLog.close();
			BufferedReader inFile = new BufferedReader(new FileReader(Main.output2));
			String line, file = "";
			while ((line = inFile.readLine()) != null) {
				if (line.split(" # ").length >= 2 && !line.split(" # ")[1].equals(LoginUser.account)) {
					file = file + line + "\n";
				}
			}
			inFile.close();
			ItemList.transactionLog = new PrintWriter(new FileWriter(Main.output2));
			ItemList.transactionLog.print(file);
		} catch (IOException ex) {}
	}
	
	// add a user's account
	public static void updateAccount(String account, String password, String name, ArrayList<String> history) {
		String pas = password + "#" + name;
		if (history != null) {
			for (int i = 0; i < history.size(); i++) {
				pas = pas + "#" + history.get(i);
			}
		}
		pps.setProperty(account, pas);
		storeAccount();
	}
	
	public static String getName(String account) {
		if (account.equals("admin")) return "admin";
		return pps.getProperty(account).split("#")[1];
	}
}
