// necessary libraries for the GUI class and some button algorithm
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class UserInterface{
	// elements for GUI
	private JFrame frame;
	private JPanel loginPanel;
	private JPanel registerPanel;
	private JPanel searchPanel;
	public static JPanel resultPanel;
	public static JPanel itemPanel;
	public static JScrollPane scroll;
	private GridBagLayout gbl_contentPane = new GridBagLayout();
	
	private JLabel lbAccount = new JLabel("Account");
	private JTextField tfAccount = new JTextField(20);
	private JLabel lbPassword = new JLabel("password");
	private JPasswordField pfPassword = new JPasswordField(20);
	private JLabel lbName = new JLabel("name");
	private JTextField tfName = new JTextField(10);
	
	private JButton btLogin = new JButton("Login");
	private JButton btRegisterInLogin = new JButton("Create an account");
	private JButton btLoginInRegister = new JButton("Login");
	private JButton btRegister = new JButton("Register");
	private JButton btExit = new JButton("Exit");
	private JButton btContinueAsGuest = new JButton("Continue as guest");
	
	private String logo = "END.";
	private String keyword = "";
	private JLabel lbLogo = new JLabel(logo);
	private JTextField tfKeyword = new JTextField(40);
	private JButton btSearch = new JButton("Search");
	private JLabel lbSearchHist = new JLabel("Keyword search history:");
	private JLabel[] lbHist;
	private JButton btTheLastHour = new JButton("Search history for the last hour");
	private JButton btTheLast24Hours = new JButton("Search history for the last 24 hours");
	private JButton btTheLastWeek = new JButton("Search history for the last week");
	private JButton btTheLastMonth = new JButton("Search history for the last month");
	private JButton btDeleteHist = new JButton("Delete account search history");
	private JButton btDeleteAllLocalData = new JButton("Delete all local data");
	
	private JButton btBack = new JButton("Back");
	private JButton btLogOut = new JButton("Log out");
	
	// GUI constructor
	public UserInterface() {
		// set the layout of the panel
		gbl_contentPane.columnWidths = new int[]{320, 320, 320, 320, 0};
		gbl_contentPane.rowHeights = new int[]{50, 50, 50, 50, 50, 50, 50};
		gbl_contentPane.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		
		// the logo of my website
		lbLogo.setFont(new Font("Tahoma", Font.PLAIN, 50));
		frame = new JFrame("END.");
		frame.setBounds(250, 150, 1280, 800);
		login(); // generate all elements and layout for login page
		frame.setContentPane(loginPanel); // initial panel is login page
		
		
		
		/// button action listeners
		
		// read user's input for account and password, then check if it is correct. if so, then generate search page
		btLogin.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				String account = tfAccount.getText();
				String password = new String(pfPassword.getPassword());
				if (!UserAccount.checkPassword(account, password)) {
					JOptionPane.showMessageDialog(frame, "Incorrect account or password");
				} else {
					new LoginUser(account, password, UserAccount.getName(account), UserAccount.getUserHistory(account));
					JOptionPane.showMessageDialog(frame, "Login Successfully");
					search();
					try {
						userHistory();
					} catch (Exception ex) {}
					frame.getContentPane().removeAll();
					frame.setContentPane(searchPanel);
					frame.validate();
					frame.repaint();
				}
			}
		});
		
		// load the register panel when the user in login panel
		btRegisterInLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				register();
				frame.getContentPane().removeAll();
				frame.setContentPane(registerPanel);
				frame.validate();
				frame.repaint();
			}
		});
		
		// load the login panel when the user in register panel
		btLoginInRegister.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				login();
				frame.getContentPane().removeAll();
				frame.setContentPane(loginPanel);
				frame.validate();
				frame.repaint();
			}
		});
		
		// read user's input of account and password, and check if they are valid. if valid, then store the account information
		btRegister.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String account = tfAccount.getText();
				String password = new String(pfPassword.getPassword());
				String name = tfName.getText();
				if (account.equals("") || password.equals("") || name.equals("")) {
					JOptionPane.showMessageDialog(frame, "account, password or name cannot be empty");
				} else if (UserAccount.checkIfExist(account)) {
					JOptionPane.showMessageDialog(frame, "Sorry, this account has already existed");
				} else {
					UserAccount.updateAccount(account, password, name, null);
					JOptionPane.showMessageDialog(frame, "Register successfully");
				}
			}
		});
		
		// exit the program
		btExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(frame, "Thank you!");
				ItemList.transactionLog.close();
				System.exit(0);
			}
		});
		
		// load the search page as a guest, no need for login
		btContinueAsGuest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				search();
				frame.getContentPane().removeAll();
				frame.setContentPane(searchPanel);
				frame.validate();
				frame.repaint();
			}
		});
		
		// read the user's input and make a request to the website to download the information of the keyword, then load the result panel
		btSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				keyword = tfKeyword.getText().replace(' ', '+');
				try {
					if (LoginUser.account != null) {
						LoginUser.add(keyword);
						UserAccount.updateAccount(LoginUser.account, LoginUser.password, LoginUser.name, LoginUser.history);
					}
					result();
					File file = new File(Main.htmlPath + "/" + keyword + ".html");
					if (!file.exists()) {
						new WebpageReader("https://www.endclothing.com/us/catalogsearch/result/?q=" + keyword, Main.output2, Main.htmlPath);
					}
					new ExtractData(keyword);
					resultPanel.validate();
				} catch (Exception ex) {}
			}
		});
		
		// go back to search panel
		btBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				search();
				if (LoginUser.account != null) {
					try {
						userHistory();
					} catch (Exception ex) {}
				}
				frame.getContentPane().removeAll();
				frame.setContentPane(searchPanel);
				frame.validate();
				frame.repaint();
			}
		});
		
		btLogOut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				LoginUser.setAccount(null);
				LoginUser.setPassword(null);
				LoginUser.setName(null);
				LoginUser.setHistory(null);
				login();
				frame.getContentPane().removeAll();
				frame.setContentPane(loginPanel);
				frame.validate();
				frame.repaint();
			}
		});
		
		// for user only, look for search history in the last hour
		btTheLastHour.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					BufferedReader inFile = new BufferedReader(new FileReader(Main.output2));
					String line;
					result();
					while ((line = inFile.readLine()) != null) {
						String[] data = line.split(" # "); // data[]{0:time, 1:user account, 2:method, 3:item name, 4:item color, 5:item price, 6:path of item image}
						
						// get transaction time
						SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
						Date date = dateFormat.parse(data[0]);
						// get the time an hour before current time
						Calendar calendar = Calendar.getInstance();
						calendar.add(Calendar.HOUR_OF_DAY, -1);
						// if the transaction is made by this user and transaction time is in last hour
						if (data[1].equals(LoginUser.account) && date.after(calendar.getTime())) {
							addToSearchPage(data[3], data[4], data[5], new ImageIcon(data[6]));
							ExtractData.itemNumber++;
						}
					}
					ExtractData.itemNumber = 0;
					resultPanel.validate();
					inFile.close();
				} catch (Exception ex) {
					
				}
			}
		});
		
		// for user only, look for search history in the last 24 hour
		btTheLast24Hours.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					BufferedReader inFile = new BufferedReader(new FileReader(Main.output2));
					String line;
					result();
					while ((line = inFile.readLine()) != null) {
						String[] data = line.split(" # "); // data[]{0:time, 1:user account, 2:method, 3:item name, 4:item color, 5:item price, 6:path of item image}
						
						// get transaction time
						SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
						Date date = dateFormat.parse(data[0]);
						// get the time a day before current time
						Calendar calendar = Calendar.getInstance();
						calendar.add(Calendar.DAY_OF_YEAR, -1);
						// if the transaction is made by this user and transaction time is in last day
						if (data[1].equals(LoginUser.account) && date.after(calendar.getTime())) {
							addToSearchPage(data[3], data[4], data[5], new ImageIcon(data[6]));
							ExtractData.itemNumber++;
						}
					}
					ExtractData.itemNumber = 0;
					resultPanel.validate();
					inFile.close();
				} catch (Exception ex) {
					
				}
			}
		});
		
		// for user only, look for search history in the last week
		btTheLastWeek.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					BufferedReader inFile = new BufferedReader(new FileReader(Main.output2));
					String line;
					result();
					while ((line = inFile.readLine()) != null) {
						String[] data = line.split(" # "); // data[]{0:time, 1:user account, 2:method, 3:item name, 4:item color, 5:item price, 6:path of item image}
						
						// get transaction time
						SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
						Date date = dateFormat.parse(data[0]);
						// get the time 7 days before current time
						Calendar calendar = Calendar.getInstance();
						calendar.add(Calendar.DAY_OF_YEAR, -7);
						// if the transaction is made by this user and transaction time is in last week
						if (data[1].equals(LoginUser.account) && date.after(calendar.getTime())) {
							addToSearchPage(data[3], data[4], data[5], new ImageIcon(data[6]));
							ExtractData.itemNumber++;
						}
					}
					ExtractData.itemNumber = 0;
					resultPanel.validate();
					inFile.close();
				} catch (Exception ex) {
					
				}
			}
		});
		
		// for user only, look for search history in the last month
		btTheLastMonth.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					BufferedReader inFile = new BufferedReader(new FileReader(Main.output2));
					String line;
					result();
					while ((line = inFile.readLine()) != null) {
						String[] data = line.split(" # "); // data[]{0:time, 1:user account, 2:method, 3:item name, 4:item color, 5:item price, 6:path of item image}
						
						// get transaction time
						SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
						Date date = dateFormat.parse(data[0]);
						// get the time 30 days before current time
						Calendar calendar = Calendar.getInstance();
						calendar.add(Calendar.DAY_OF_YEAR, -30);
						// if the transaction is made by this user and transaction time is in last month
						if (data[1].equals(LoginUser.account) && date.after(calendar.getTime())) {
							addToSearchPage(data[3], data[4], data[5], new ImageIcon(data[6]));
							ExtractData.itemNumber++;
						}
					}
					ExtractData.itemNumber = 0;
					resultPanel.validate();
					inFile.close();
				} catch (Exception ex) {
					
				}
			}
		});
		
		// for user only, delete user's search history
		btDeleteHist.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				UserAccount.deleteAccountHistory();
				search();
				try {
					userHistory();
				} catch (Exception ex) {}
				frame.getContentPane().removeAll();
				frame.setContentPane(searchPanel);
				frame.validate();
				frame.repaint();
			}
		});
		
		// for admin only, delete all data that this program produced
		btDeleteAllLocalData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteDirectory(new File(Main.htmlPath));
				deleteDirectory(new File(Main.imagePath));
				deleteDirectory(new File(Main.path));
				deleteDirectory(new File(Main.accFile));
				ItemList.transactionLog.close();
				deleteDirectory(new File(Main.output2));
				try {
					ItemList.transactionLog = new PrintWriter(new FileWriter(Main.output2));
				} catch(Exception ex) {}
			}
		});
		
		// end of button action listeners
		
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(true);
	}
	
	// delete all data that the program produced
	private void deleteDirectory (File dir) { 
		if (dir.isDirectory()) { 
			File[] children = dir.listFiles(); 
			for (int i = 0; i < children.length; i++) { 
				deleteDirectory(children[i]); 
			} 
		}
		dir.delete();
	}
	
	// set up basic elements for login panel
	private void login() {
		loginPanel = new JPanel();
		loginPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		loginPanel.setLayout(gbl_contentPane);
		
		GridBagConstraints gbc_lbLogo = new GridBagConstraints();
		gbc_lbLogo.anchor = GridBagConstraints.EAST;
		gbc_lbLogo.insets = new Insets(0, 0, 5, 5);
		gbc_lbLogo.gridx = 1;
		gbc_lbLogo.gridy = 0;
		loginPanel.add(lbLogo, gbc_lbLogo);
		
		GridBagConstraints gbc_lbAccount = new GridBagConstraints();
		gbc_lbAccount.anchor = GridBagConstraints.CENTER;
		gbc_lbAccount.insets = new Insets(0, 0, 5, 5);
		gbc_lbAccount.gridx = 1;
		gbc_lbAccount.gridy = 1;
		loginPanel.add(lbAccount, gbc_lbAccount);
		
		GridBagConstraints gbc_tfAccount = new GridBagConstraints();
		gbc_tfAccount.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfAccount.insets = new Insets(0, 0, 5, 0);
		gbc_tfAccount.gridx = 2;
		gbc_tfAccount.gridy = 1;
		loginPanel.add(tfAccount, gbc_tfAccount);
		
		GridBagConstraints gbc_lbPassword = new GridBagConstraints();
		gbc_lbPassword.anchor = GridBagConstraints.CENTER;
		gbc_lbPassword.insets = new Insets(0, 0, 5, 5);
		gbc_lbPassword.gridx = 1;
		gbc_lbPassword.gridy = 2;
		loginPanel.add(lbPassword, gbc_lbPassword);
		
		GridBagConstraints gbc_pfPassword = new GridBagConstraints();
		gbc_pfPassword.fill = GridBagConstraints.HORIZONTAL;
		gbc_pfPassword.insets = new Insets(0, 0, 5, 0);
		gbc_pfPassword.gridx = 2;
		gbc_pfPassword.gridy = 2;
		loginPanel.add(pfPassword, gbc_pfPassword);
		
		GridBagConstraints gbc_btLogin = new GridBagConstraints();
		gbc_btLogin.insets = new Insets(0, 0, 5, 5);
		gbc_btLogin.gridx = 1;
		gbc_btLogin.gridy = 3;
		loginPanel.add(btLogin, gbc_btLogin);
		
		GridBagConstraints gbc_btRegisterInLogin = new GridBagConstraints();
		gbc_btRegisterInLogin.insets = new Insets(0, 0, 5, 0);
		gbc_btRegisterInLogin.gridx = 2;
		gbc_btRegisterInLogin.gridy = 3;
		loginPanel.add(btRegisterInLogin, gbc_btRegisterInLogin);
		
		GridBagConstraints gbc_btExit = new GridBagConstraints();
		gbc_btExit.insets = new Insets(0, 0, 0, 5);
		gbc_btExit.gridx = 1;
		gbc_btExit.gridy = 4;
		loginPanel.add(btExit, gbc_btExit);
		
		GridBagConstraints gbc_btContinueAsGuest = new GridBagConstraints();
		gbc_btContinueAsGuest.gridx = 2;
		gbc_btContinueAsGuest.gridy = 4;
		loginPanel.add(btContinueAsGuest, gbc_btContinueAsGuest);
	}
	
	// set up basic elements for register panel
	private void register() {
		registerPanel = new JPanel();
		registerPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		registerPanel.setLayout(gbl_contentPane);
		
		JLabel lbSignUp = new JLabel("Sign Up");
		GridBagConstraints gbc_lbSignUp = new GridBagConstraints();
		gbc_lbSignUp.anchor = GridBagConstraints.EAST;
		gbc_lbSignUp.insets = new Insets(0, 0, 5, 5);
		gbc_lbSignUp.gridx = 1;
		gbc_lbSignUp.gridy = 0;
		registerPanel.add(lbSignUp, gbc_lbSignUp);
		
		GridBagConstraints gbc_lbAccount = new GridBagConstraints();
		gbc_lbAccount.anchor = GridBagConstraints.CENTER;
		gbc_lbAccount.insets = new Insets(0, 0, 5, 5);
		gbc_lbAccount.gridx = 1;
		gbc_lbAccount.gridy = 1;
		registerPanel.add(lbAccount, gbc_lbAccount);
		
		GridBagConstraints gbc_tfAccount = new GridBagConstraints();
		gbc_tfAccount.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfAccount.insets = new Insets(0, 0, 5, 0);
		gbc_tfAccount.gridx = 2;
		gbc_tfAccount.gridy = 1;
		registerPanel.add(tfAccount, gbc_tfAccount);
		
		GridBagConstraints gbc_lbName = new GridBagConstraints();
		gbc_lbName.anchor = GridBagConstraints.CENTER;
		gbc_lbName.insets = new Insets(0, 0, 5, 5);
		gbc_lbName.gridx = 1;
		gbc_lbName.gridy = 2;
		registerPanel.add(lbName, gbc_lbName);
		
		GridBagConstraints gbc_tfName = new GridBagConstraints();
		gbc_tfName.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfName.insets = new Insets(0, 0, 5, 0);
		gbc_tfName.gridx = 2;
		gbc_tfName.gridy = 2;
		registerPanel.add(tfName, gbc_tfName);
		
		GridBagConstraints gbc_lbPassword = new GridBagConstraints();
		gbc_lbPassword.anchor = GridBagConstraints.CENTER;
		gbc_lbPassword.insets = new Insets(0, 0, 5, 5);
		gbc_lbPassword.gridx = 1;
		gbc_lbPassword.gridy = 3;
		registerPanel.add(lbPassword, gbc_lbPassword);
		
		GridBagConstraints gbc_pfPassword = new GridBagConstraints();
		gbc_pfPassword.fill = GridBagConstraints.HORIZONTAL;
		gbc_pfPassword.insets = new Insets(0, 0, 5, 0);
		gbc_pfPassword.gridx = 2;
		gbc_pfPassword.gridy = 3;
		registerPanel.add(pfPassword, gbc_pfPassword);
		
		GridBagConstraints gbc_btRegister = new GridBagConstraints();
		gbc_btRegister.insets = new Insets(0, 0, 5, 5);
		gbc_btRegister.gridx = 1;
		gbc_btRegister.gridy = 4;
		registerPanel.add(btRegister, gbc_btRegister);
		
		GridBagConstraints gbc_btLoginInRegister = new GridBagConstraints();
		gbc_btLoginInRegister.insets = new Insets(0, 0, 5, 0);
		gbc_btLoginInRegister.gridx = 2;
		gbc_btLoginInRegister.gridy = 4;
		registerPanel.add(btLoginInRegister, gbc_btLoginInRegister);
		
		GridBagConstraints gbc_btExit = new GridBagConstraints();
		gbc_btExit.insets = new Insets(0, 0, 0, 5);
		gbc_btExit.gridx = 1;
		gbc_btExit.gridy = 5;
		registerPanel.add(btExit, gbc_btExit);
		
		GridBagConstraints gbc_btContinueAsGuest = new GridBagConstraints();
		gbc_btContinueAsGuest.gridx = 2;
		gbc_btContinueAsGuest.gridy = 5;
		registerPanel.add(btContinueAsGuest, gbc_btContinueAsGuest);
	}
	
	// set up basic elements for search panel
	private void search() {
		searchPanel = new JPanel();
		searchPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		searchPanel.setLayout(gbl_contentPane);
		
		GridBagConstraints gbc_lbLogo = new GridBagConstraints();
		gbc_lbLogo.anchor = GridBagConstraints.WEST;
		gbc_lbLogo.insets = new Insets(0, 0, 5, 5);
		gbc_lbLogo.gridx = 2;
		gbc_lbLogo.gridy = 0;
		searchPanel.add(lbLogo, gbc_lbLogo);

		GridBagConstraints gbc_tfKeyword = new GridBagConstraints();
		gbc_tfKeyword.gridwidth = 3;
		gbc_tfKeyword.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfKeyword.insets = new Insets(0, 0, 5, 5);
		gbc_tfKeyword.gridx = 0;
		gbc_tfKeyword.gridy = 1;
		searchPanel.add(tfKeyword, gbc_tfKeyword);
		
		GridBagConstraints gbc_btSearch = new GridBagConstraints();
		gbc_btSearch.insets = new Insets(0, 0, 5, 0);
		gbc_btSearch.gridx = 3;
		gbc_btSearch.gridy = 1;
		searchPanel.add(btSearch, gbc_btSearch);
		
		GridBagConstraints gbc_btLogOut = new GridBagConstraints();
		gbc_btLogOut.insets = new Insets(0, 0, 5, 0);
		gbc_btLogOut.gridx = 2;
		gbc_btLogOut.gridy = 3;
		searchPanel.add(btLogOut, gbc_btLogOut);
		
		GridBagConstraints gbc_btExit = new GridBagConstraints();
		gbc_btExit.insets = new Insets(0, 0, 5, 0);
		gbc_btExit.gridx = 3;
		gbc_btExit.gridy = 3;
		searchPanel.add(btExit, gbc_btExit);
	}
	
	// set up basic elements for search panel that is for user only
	public void userHistory() throws Exception {
		JLabel name = new JLabel("Hello, " + LoginUser.name);
		GridBagConstraints gbc_name = new GridBagConstraints();
		gbc_name.insets = new Insets(0, 0, 5, 5);
		gbc_name.gridx = 3;
		gbc_name.gridy = 0;
		searchPanel.add(name, gbc_name);
		
		if (LoginUser.isAdmin) {
			GridBagConstraints gbc_btDeleteAllLocalData = new GridBagConstraints();
			gbc_btDeleteAllLocalData.insets = new Insets(0, 0, 5, 5);
			gbc_btDeleteAllLocalData.gridx = 1;
			gbc_btDeleteAllLocalData.gridy = 3;
			searchPanel.add(btDeleteAllLocalData, gbc_btDeleteAllLocalData);
		}
		
		GridBagConstraints gbc_btTheLastHour = new GridBagConstraints();
		gbc_btTheLastHour.insets = new Insets(0, 0, 5, 5);
		gbc_btTheLastHour.gridx = 0;
		gbc_btTheLastHour.gridy = 2;
		searchPanel.add(btTheLastHour, gbc_btTheLastHour);
		
		GridBagConstraints gbc_btTheLast24Hours = new GridBagConstraints();
		gbc_btTheLast24Hours.insets = new Insets(0, 0, 5, 5);
		gbc_btTheLast24Hours.gridx = 1;
		gbc_btTheLast24Hours.gridy = 2;
		searchPanel.add(btTheLast24Hours, gbc_btTheLast24Hours);
		
		GridBagConstraints gbc_btTheLastWeek = new GridBagConstraints();
		gbc_btTheLastWeek.insets = new Insets(0, 0, 5, 5);
		gbc_btTheLastWeek.gridx = 2;
		gbc_btTheLastWeek.gridy = 2;
		searchPanel.add(btTheLastWeek, gbc_btTheLastWeek);
		
		GridBagConstraints gbc_btTheLastMonth = new GridBagConstraints();
		gbc_btTheLastMonth.insets = new Insets(0, 0, 5, 0);
		gbc_btTheLastMonth.gridx = 3;
		gbc_btTheLastMonth.gridy = 2;
		searchPanel.add(btTheLastMonth, gbc_btTheLastMonth);
		
		GridBagConstraints gbc_btDeleteHist = new GridBagConstraints();
		gbc_btDeleteHist.insets = new Insets(0, 0, 5, 5);
		gbc_btDeleteHist.gridx = 0;
		gbc_btDeleteHist.gridy = 3;
		searchPanel.add(btDeleteHist, gbc_btDeleteHist);
		
		GridBagConstraints gbc_lbSearchHist = new GridBagConstraints();
		gbc_lbSearchHist.insets = new Insets(0, 0, 5, 0);
		gbc_lbSearchHist.gridx = 0;
		gbc_lbSearchHist.gridy = 4;
		searchPanel.add(lbSearchHist, gbc_lbSearchHist);
		
		if (LoginUser.history != null) {
			lbHist = new JLabel[LoginUser.history.size()];
			for (int i = 0; i < LoginUser.history.size(); i++) {
				lbHist[i] = new JLabel(LoginUser.history.get(i));
				GridBagConstraints gbc_lbHist = new GridBagConstraints();
				gbc_lbHist.insets = new Insets(0, 0, 0, 5);
				gbc_lbHist.gridx = i % 4;
				gbc_lbHist.gridy = 5 + i / 4;
				searchPanel.add(lbHist[i], gbc_lbHist);
			}
		}
	}
	
	// set up the result panel
	private void result() {
		JPanel mainPanel = new JPanel();
		mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		resultPanel = new JPanel();
		resultPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		resultPanel.setLayout(gbl_contentPane);
		
		JScrollPane scrollPane = new JScrollPane(resultPanel, 
				   ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,  
				   ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		mainPanel.add(scrollPane);
		
		GridBagConstraints gbc_btBack = new GridBagConstraints();
		gbc_btBack.insets = new Insets(0, 0, 0, 0);
		gbc_btBack.gridx = 1;
		gbc_btBack.gridy = 0;
		resultPanel.add(btBack, gbc_btBack);
		
		frame.getContentPane().removeAll();
		frame.setContentPane(mainPanel);
		frame.setLayout(new GridLayout(1,1));
		frame.validate();
		frame.repaint();
	}
	
	// add the each item up to the result panel
	public static void addToSearchPage(String name, String color, String price, ImageIcon img) {
		JLabel lbName = new JLabel(name);
		JLabel lbColor = new JLabel(color);
		JLabel lbPrice = new JLabel(price);
		JLabel lbImage = new JLabel(new ImageIcon(img.getImage().getScaledInstance(120, 120, Image.SCALE_DEFAULT)));
		
		GridBagConstraints gbc_lbName = new GridBagConstraints();
		gbc_lbName.insets = new Insets(0, 0, 5, 5);
		gbc_lbName.gridx = 1;
		gbc_lbName.gridy = ExtractData.itemNumber + 1;
		resultPanel.add(lbName, gbc_lbName);
		
		GridBagConstraints gbc_lbColor = new GridBagConstraints();
		gbc_lbColor.insets = new Insets(0, 0, 5, 5);
		gbc_lbColor.gridx = 2;
		gbc_lbColor.gridy = ExtractData.itemNumber + 1;
		resultPanel.add(lbColor, gbc_lbColor);
		
		GridBagConstraints gbc_lbPrice = new GridBagConstraints();
		gbc_lbPrice.insets = new Insets(0, 0, 5, 5);
		gbc_lbPrice.gridx = 3;
		gbc_lbPrice.gridy = ExtractData.itemNumber + 1;
		resultPanel.add(lbPrice, gbc_lbPrice);
		
		GridBagConstraints gbc_lbImage = new GridBagConstraints();
		gbc_lbImage.insets = new Insets(0, 0, 5, 5);
		gbc_lbImage.gridx = 0;
		gbc_lbImage.gridy = ExtractData.itemNumber + 1;
		resultPanel.add(lbImage, gbc_lbImage);
	}
}