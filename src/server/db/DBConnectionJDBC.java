package server.db;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DBConnectionJDBC {
	private static Connection con;
	
	public static Connection getConnection(){
		try {
			Class.forName("org.gjt.mm.mysql.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		try {
			con=DriverManager.getConnection("jdbc:mysql://localhost:3306/emailserver","root","123456");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return con;
	}
}
