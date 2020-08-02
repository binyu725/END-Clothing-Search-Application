import java.util.ArrayList;

// this class stands for the user who is currently logged in
public class LoginUser {
	public static String account;
	public static String password;
	public static String name;
	public static ArrayList<String> history;
	public static boolean isAdmin = false;
	
	public LoginUser(String acc, String pas, String n , ArrayList<String> his) {
		account = acc;
		password = pas;
		name = n;
		history = his;
		if (account.equals("admin") && password.equals("nimda")) {
			isAdmin = true;
		}
	}
	
	public static void add(String keyword) {
		if (!history.contains(keyword)) {
			history.add(keyword);
		}
	}
	
	public static String getAccount() {
		return account;
	}
	
	public static String getName() {
		return name;
	}
	
	public static ArrayList<String> getHistory() {
		return history;
	}
	
	public static void setAccount(String acc) {
		account = acc;
	}
	
	public static void setPassword(String pass) {
		password = pass;
	}
	
	public static void setName(String n) {
		name = n;
	}
	
	public static void setHistory(ArrayList<String> hist) {
		history = hist;
	}
}
