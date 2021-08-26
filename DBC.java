package src;

import java.sql.*;
import java.util.Properties;




public abstract class DBC {
	protected Connection conn;
	public DBC () {
		
	}
	
	public void connect() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Properties p = new Properties();
			p.put("user", "root");
			p.put("password", "Datdatserver");
			conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1/Datdatprosjekt?allowPublicKeyRetrieval=true&autoReconnect=true&useSSL=false",p); 
		} catch (Exception e)
		{
			throw new RuntimeException("Unable to connect", e);
		}
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("hello world");
	}

}
