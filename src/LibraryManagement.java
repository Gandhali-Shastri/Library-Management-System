import java.io.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.Date;
import java.util.Scanner;

public class LibraryManagement {
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	static final String DB_URL = "jdbc:mysql://localhost/";
	static BufferedReader br;

	public static boolean insertBulkData() throws Exception {
		String inputFile;
		String splitter = ",";
		BufferedReader br = null;
		String line = "";
		Class.forName("com.mysql.jdbc.Driver");
		Connection conn = DriverManager.getConnection(DB_URL, "root", "root");
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			// Insert into table book
			inputFile = "Book.csv";
			br = new BufferedReader(new FileReader(inputFile));
			while ((line = br.readLine()) != null) {
				// use comma as separator
				String[] book = line.split(splitter);
				
				String query = "insert into universitylibrarysystem.books values ('" + book[0] +"', '"+ book[1] + "' , '" + book[2] +"' , '"+ book[3] +"' , '"+ book[4] +"' , '"+ book[5] +"' , '"+ book[6] +"' , '"+ book[7] + "');"; 
				stmt.execute(query);
			}

			//Insert into table members
			inputFile = "Members.csv";
			br = new BufferedReader(new FileReader(inputFile));
			while ((line = br.readLine()) != null) {
				// use comma as separator
				String[] members = line.split(splitter);
				
				String query = "insert into universitylibrarysystem.members (`Member_No`, `SSN`, `First_Name`, `Last_Name`, `Campus_Address`, `Mailing_Address`, `Phone_No`, `Photo_ID`, `books_borrowed`, `Membership_Startdate`, `Mem_Type`, `Mem_Status`) "
						+ "values ('" + members[0] +"', "+ Integer.parseInt(members[1]) +" , '"+ members[2] +"' , '"+ members[3] +"' , '"+ members[4] +"' , '"+ members[5] +"' , '"+ members[6] +
						"' , '" + members[7] +"' , '"+ Integer.parseInt(members[8]) +"' , '"+ members[9] +"' , '"+ members[10] +"' , '"+ members[11] +"');";
				//String query = "insert into universitylibrarysystem..members values (﻿'10', 564891 , 'Gandhali' , 'Shastri' , '417  Summit blvd   no. 5.' , '417  Summit blvd   no. 5.' , '6825546612' , 'pic.jpg' , '0' , '2019-04-21' , 'S' , 'Active');";
				
				stmt.execute(query);
			}

//			// Insert into table Borrowed
//			inputFile = "borrowed.csv";
//			br = new BufferedReader(new FileReader(inputFile));
//			while ((line = br.readLine()) != null) {
//				// use comma as separator
//				String[] match = line.split(splitter);
//				String query = "insert into universitylibrarysystem.borrowed values (" + Integer.parseInt(match[0]) +", "+ Integer.parseInt(match[1]) +" , "+ match[2] +" , "+ match[3] +");"; 
//				stmt.execute(query);
//			}




		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return true;


	}

	public static void main(String args[]) throws Exception {
		int choice=0;

		br = new BufferedReader(new InputStreamReader(System.in));


		while(true) {
			
			displayOverDueBooks();
			displayMemberExpiration();

			System.out.println("\nEnter Choice:");
			System.out.println("1. Add Member");
			System.out.println("2. Add Book");
			System.out.println("3. Add Borrow Transaction");
			System.out.println("4. Return Book");
			System.out.println("5. Renew A Membership");
			System.out.println("6. Insert Bulk Records");
			System.out.println("7. Exit");

			choice = Integer.parseInt(br.readLine());
			String ans;

			switch(choice) {
			case 1:	insertMember();

			break;

			case 2:	insertBook();
			break;

			case 3:	insertBorrowed();
			break;

			case 4:	alterBorrowed();
			break;

			case 5: alterMember();
			break;

			case 6:insertBulkData();
			break;

			case 7:	System.exit(0);
			break;

			default: System.out.println("Wrong Choice!");


			}
		}
	}
	
	private static void displayMemberExpiration() throws Exception {
		Class.forName("com.mysql.jdbc.Driver");
		Connection conn = DriverManager.getConnection(DB_URL, "root", "root");
		Statement stmt = conn.createStatement();
		System.out.println("Membership Expiring:\n");
		System.out.println("Name\t Member Id\t Membership Start Date");
		try{
			String query = "Select concat(first_name,' ', last_name) as Name, member_no as 'Member ID', membership_startdate as 'Member Start Date' from universitylibrarysystem.members where datediff(curdate(),membership_startdate) > 1430 order by membership_startdate;";
			
			ResultSet rs = stmt.executeQuery(query);
		      
		      // iterate through the java resultset
		      while (rs.next())
		      {
		        
		        String name = rs.getString("Name");
		        String memberId = rs.getString("Member ID");
		        String dateStart = rs.getString("Member Start Date");
		        
		        		        
		        // print the results
		        System.out.format("%s\t %s\t %s\t\n", name, memberId, dateStart );
		      }
		      stmt.close();
		} catch(SQLException e){			
			e.printStackTrace();
		}
	}
	
	private static void displayOverDueBooks() throws Exception {
		Class.forName("com.mysql.jdbc.Driver");
		Connection conn = DriverManager.getConnection(DB_URL, "root", "root");
		Statement stmt = conn.createStatement();
		System.out.println("Overdue Books:\n");
		System.out.println("Name\t\t Member Id\t Date Issued\t title");
		try{
			String query = "select concat(first_name,' ', last_name) as Name, m.member_no as 'Member ID', b.issue_date as 'Date Issued', bo.title as 'Title' from universitylibrarysystem.members m inner join universitylibrarysystem.borrowed b on m.member_no = b.member_no inner join universitylibrarysystem.books bo on b.isbn=bo.isbn  where datediff(curdate(),b.issue_date) > 14 and b.is_returned = 0;";
			
			ResultSet rs = stmt.executeQuery(query);
		      
		      // iterate through the java resultset
		      while (rs.next())
		      {
		        
		        String name = rs.getString("Name");
		        String memberId = rs.getString("Member ID");
		        String dateIssued = rs.getString("Date Issued");
		        String title = rs.getString("Title");
		        		        
		        // print the results
		        System.out.format("%s\t %s\t %s\t %s\t\n", name, memberId, dateIssued, title);
		      }
		      stmt.close();
		} catch(SQLException e){			
			e.printStackTrace();
		}
	}

	private static void alterMember() throws Exception {
		br = new BufferedReader(new InputStreamReader(System.in));

		System.out.println("Enter Member ID: ");
		int id = Integer.parseInt(br.readLine());

		Class.forName("com.mysql.jdbc.Driver");
		Connection conn = DriverManager.getConnection(DB_URL, "root", "root");
		Statement stmt = null;
		stmt = conn.createStatement();
		String strtDate = LocalDate.now().getYear() + "-" + LocalDate.now().getMonthValue() + "-" + LocalDate.now().getDayOfMonth();
		try{
			String query = "UPDATE universitylibrarysystem.Members " +
					"SET Membership_Startdate = '" + strtDate + "' WHERE member_no = '" + id + "';";
			
			stmt.executeUpdate(query);
		} catch(SQLException e){
			if(e.getErrorCode() == 1062 ){
				System.out.println("Book already exists");
			} else {
				e.printStackTrace();
			}
		}

	}

	private static void alterBorrowed() throws Exception {
		br = new BufferedReader(new InputStreamReader(System.in));

		System.out.println("Enter Book ISBN: ");
		String isbn = br.readLine();

		System.out.println("Enter Member ID: ");
		int id = Integer.parseInt(br.readLine());

		Class.forName("com.mysql.jdbc.Driver");
		Connection conn = DriverManager.getConnection(DB_URL, "root", "root");
		Statement stmt = null;
		stmt = conn.createStatement();
		try{
			String query = "UPDATE universitylibrarysystem.borrowed " +
					"SET is_returned = 1 WHERE isbn = " + isbn + " and member_no = " + id;
			int i = stmt.executeUpdate(query);
			if (i!=0) {
				String detail = "Select * from universitylibrarysystem.books where isbn = " + isbn;
				ResultSet det = stmt.executeQuery(detail);
				String [] book = new String[3];

				while(det.next()) {
					book[0]=det.getString("isbn");
					book[1]=det.getString("title");
					book[2]=det.getString("author");
				}
				System.out.println("Book Returned.\nBook Name: " + book[1] + "\nISBN: " + book[0] + "\nAuthor: " + book[2] );
				detail = "Select * from universitylibrarysystem.borrowed where isbn = " + isbn + " and member_no = " + id + " and is_returned = 1";
				det = stmt.executeQuery(detail);
				String [] borrow = new String [1];

				while(det.next()) {
					borrow[0]=det.getString("issue_date");
					
				}
				System.out.println("Book Issue Date: " + borrow[0] + "\nBook Returned on: " + LocalDate.now().getYear() + "-" + LocalDate.now().getMonthValue() + "-" + LocalDate.now().getDayOfMonth() + "\n");
			}
		} catch(SQLException e){
			if(e.getErrorCode() == 1062 ){
				System.out.println("Book already exists");
			} else {
				e.printStackTrace();
			}
		}


	}

	private static void insertBorrowed() throws Exception {
		br = new BufferedReader(new InputStreamReader(System.in));

		System.out.println("Enter Book ISBN: ");
		String isbn = br.readLine();

		System.out.println("Enter Member ID: ");
		int id = Integer.parseInt(br.readLine());

		String issuedate = LocalDate.now().getYear() + "-" + LocalDate.now().getMonthValue()+ "-" + LocalDate.now().getDayOfMonth();

		Class.forName("com.mysql.jdbc.Driver");
		Connection conn = DriverManager.getConnection(DB_URL, "root", "root");
		Statement stmt = null;
		stmt = conn.createStatement();
		
		try{
			String query="Select is_rent from universitylibrarysystem.books where isbn="+ isbn +";";
			stmt.execute(query);
			ResultSet rs = stmt.executeQuery(query);
		      
		      // iterate through the java resultset
		      while (rs.next())
		      {		        
		        String isrentable = rs.getString("is_rent");
		        if(isrentable.equals("0")) {
		        	System.out.println("Book cannot be rented");
		        	return;
		        }
		      }
		} catch(SQLException e){
			e.printStackTrace();
		}
		
		
		try{
			String query="Select count(member_no) as count1 from universitylibrarysystem.borrowed where member_no="+ id + " and is_returned = 0;";
			stmt.execute(query);
			ResultSet rs = stmt.executeQuery(query);
		      
		      // iterate through the java resultset
		      while (rs.next())
		      {		        
		        String count = rs.getString("count1");
		        if(Integer.parseInt(count)>5) {
		        	System.out.println("Borrow limit reached");
		        	return;
		        }
		      }
		} catch(SQLException e){
			e.printStackTrace();
		}
		
		try{
			String query="INSERT INTO `universitylibrarysystem`.`borrowed` (`Member_No`, `ISBN`, `Issue_date`, `is_returned`) "
					+ "VALUES ('" + id + "', '" + isbn + "', '" + issuedate + "', '0'" + ");";
			stmt.execute(query);
		} catch(SQLException e){
			if(e.getErrorCode() == 1062 ){
				System.out.println("Book already exists");
			} else {
				e.printStackTrace();
			}
		}



	}

	private static void insertBook() throws Exception {
		br = new BufferedReader(new InputStreamReader(System.in));

		System.out.println("Enter Book ISBN: ");
		String isbn = br.readLine();

		System.out.println("Enter Book Name: ");
		String name = br.readLine();

		System.out.println("Enter Author's Last Name: ");
		String author = br.readLine();

		System.out.println("Enter Book's Subject Area: ");
		String sArea = br.readLine();

		System.out.println("Enter Description: ");
		//String description = br.lines().toString();
		//		Scanner s = new Scanner(System.in);
		//		System.out.println(s.nextLine());
		String description = "";
		String s;
		while (!(s = br.readLine()).equals("")) {
			description = description.concat(s + "\n");
			System.out.println(description);
		}

		System.out.println("Is the book rentable(Y/N): ");
		String rentable = br.readLine();
		int isrentable;
		if(rentable.equalsIgnoreCase("Y")) {
			isrentable = 0;
		} else {
			isrentable = 1;
		}

		System.out.println("Enter Quantity: ");
		int quantity = Integer.parseInt(br.readLine());

		Class.forName("com.mysql.jdbc.Driver");
		Connection conn = DriverManager.getConnection(DB_URL, "root", "root");
		Statement stmt = null;
		stmt = conn.createStatement();
		try{
			String query="INSERT INTO `universitylibrarysystem`.`books` (`ISBN`, `Title`, `Author`, `Sub_Area`, `Desc`, `is_rent`, `Quantity`, `Books_rented`) "
					+ "VALUES ('" + isbn + "', '" + name + "', '" + author + "', '" + sArea + "', '" + description + "', '" + isrentable + "', '" + quantity + "', '0');";
			stmt.execute(query);
		} catch(SQLException e){
			if(e.getErrorCode() == 1062 ){
				System.out.println("Book already exists");
			} else {
				e.printStackTrace();
			}
		}

	}

	private static void insertMember() throws Exception {
		br = new BufferedReader(new InputStreamReader(System.in));

		System.out.println("Enter Member ID: ");
		int id = Integer.parseInt(br.readLine());

		System.out.println("Enter Member SSN: ");
		int SSN = Integer.parseInt(br.readLine());

		System.out.println("Enter Member First Name: ");
		String fname = br.readLine();

		System.out.println("Enter Member Last Name: ");
		String lname = br.readLine();


		String caddress = "";


		String memberType = "";

		while (!memberType.equalsIgnoreCase("S") && !memberType.equalsIgnoreCase("P") && !memberType.equalsIgnoreCase("M")) {
			System.out.println("Enter Member Type (S/P/M): ");
			memberType = br.readLine();
			//System.out.println(memberType.equalsIgnoreCase("S"));
		}


		System.out.println("Enter Member Mailing Address: ");
		String maddress = br.readLine();

		if(memberType.equalsIgnoreCase("M")) {
			System.out.println("Enter Member Campus Address: ");
			caddress = br.readLine();
		} else {
			caddress = maddress;
		}

		System.out.println("Enter Member Phone No.: ");
		String phoneno = br.readLine();

		System.out.println("Enter Member Photo ID File Address: ");
		String photoid = br.readLine();

		String sdate = LocalDate.now().getYear() + "-" + LocalDate.now().getMonthValue()+ "-" + LocalDate.now().getDayOfMonth();

		Class.forName("com.mysql.jdbc.Driver");
		Connection conn = DriverManager.getConnection(DB_URL, "root", "root");
		Statement stmt = null;
		stmt = conn.createStatement();
		try{
			String query="INSERT INTO `universitylibrarysystem`.`members` (`Member_No`, `SSN`, `First_Name`, `Last_Name`, `Campus_Address`, `Mailing_Address`, `Phone_No`, `Photo_ID`, `books_borrowed`, `Membership_Startdate`, `Mem_Type`, `Mem_Status`) "
					+ "VALUES ('" + id + "', '" + SSN + "', '" + fname + "', '" + lname + "', '" + caddress + "', '" + maddress + "', '" + phoneno + "', '" + photoid + "', '0', '" + sdate + "', '" + memberType + "', 'Active');";
			stmt.execute(query);
		} catch(SQLException e){
			e.printStackTrace();
			if(e.getErrorCode() == 1062 ){
				System.out.println("Member ID already used");
			}
		}
	}
}
