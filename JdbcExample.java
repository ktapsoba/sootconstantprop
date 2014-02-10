

//STEP 1. Import required packages
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import resources.State;

public class JdbcExample {
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	static final String DB_URL = "jdbc:mysql://localhost:3306/test";

	//  Database credentials
	static final String USER = "testUser";
	static final String PASS = "testUser";
 
	public static void main(String[] args) {
		
		String one = "Somethind";
		one.toLowerCase().toCharArray();
		
		String lower = one.toLowerCase();
		
		State st = new State("KEN");
		
		Connection conn = null;
		Statement stmt = null;
		try{
			//STEP 2: Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			//STEP 3: Open a connection
			System.out.println("Connecting to database...");
			
			conn = DriverManager.getConnection(DB_URL,USER,PASS);

			//STEP 4: Execute a query
			System.out.println("Creating statement...");
			
			stmt = conn.createStatement();
			
			String sql;
			sql = "SELECT * from country";
			ResultSet rs = stmt.executeQuery(sql);

			//STEP 5: Extract data from result set
			while(rs.next()){
				
				//Retrieve by column name
				String code = rs.getString("Code");
				String name = rs.getString("Name");
				int population = rs.getInt("Population");

		       //Display values
				System.out.print("Code: " + code);
				System.out.print(", Name: " + name);
				System.out.print(", Population: " + population);
				System.out.println();
			}
			
		    //STEP 6: Clean-up environment
		    rs.close();
		    stmt.close();
		    conn.close();
		    
		}catch(SQLException se){
		    //Handle errors for JDBC
		    se.printStackTrace();
		    
		}catch(Exception e){
		    //Handle errors for Class.forName
		    e.printStackTrace();
		    
		}finally{
			
			//finally block used to close resources
			try{
				if(stmt!=null)
					stmt.close();
				
			}catch(SQLException se2){
				
			}// nothing we can do
			
			try{
				if(conn!=null)
					conn.close();
				
			}catch(SQLException se){
				se.printStackTrace();
				
			}//end finally try
			
		}//end finally
		
		System.out.println("Goodbye!");
		
	}//end main
}
