package Assignment1;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.Array;
import java.util.List;
import java.util.Scanner;



import java.sql.Types;

public class Assignment {
	private String conn_str;
	private String user_name;
	private String pass_word;

	public Assignment(String conn_str1,String username,String password) {
		conn_str = conn_str1;
		user_name = username;
		pass_word = password;
		
	}
	
public void fileToDataBase(String path) {
	Connection connection = null;
	PreparedStatement ps = null;
	String line = "";
	try {
		Class.forName("oracle.jdbc.driver.OracleDriver");
		connection = DriverManager.getConnection(conn_str, user_name, pass_word);
		BufferedReader br = new BufferedReader(new FileReader(path));
		while ((line = br.readLine()) != null) {
			String[] item = line.split(",");
			String moviename = item[0];
			int year = Integer.parseInt(item[1]);
			ps = connection.prepareStatement("INSERT INTO MediaItems (TITLE, PROD_YEAR) VALUES (?, ?)");
			ps.setString(1, moviename);
			ps.setInt(2, year);
			ps.executeUpdate();
			connection.commit();
		}
		ps.close();
		br.close();
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	}catch (ClassNotFoundException e) {
		e.printStackTrace();
	}
	catch (SQLException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	} finally {
		try {
			if(ps!=null) {ps.close();}
		} catch (SQLException e) {e.printStackTrace();}
		try {
			if(connection!=null) {connection.close();}
		} catch (SQLException e) {e.printStackTrace();}
		}
	}

public void calculateSimilarity() {
	Connection connection = null;
	PreparedStatement ps = null;
	CallableStatement rsfunc = null;
	CallableStatement simfunc = null;
	try{
	Class.forName("oracle.jdbc.driver.OracleDriver");
	connection = DriverManager.getConnection(conn_str, user_name, pass_word);
	rsfunc = connection.prepareCall("{?= call MaximalDistance()}");
	rsfunc.registerOutParameter(1, Types.INTEGER);
	rsfunc.execute();
	int max_dist = rsfunc.getInt(1);
	
	ps = connection.prepareStatement("SELECT MID FROM MediaItems");
	ResultSet mids = ps.executeQuery();
	List<Long> MidsList = new ArrayList<Long>();
	while(mids.next()) {
		MidsList.add(mids.getLong(1));
	}
	ps.close();
	simfunc = connection.prepareCall("{?= call SimCalculation(?,?,?)}");
	simfunc.registerOutParameter(1, Types.FLOAT);
	simfunc.setInt(4, max_dist);
	ps = connection.prepareStatement("INSERT INTO Similarity (MID1, MID2, SIMILARITY) VALUES (?,?,?)");
	for(int i=0;i<(MidsList.size()-1);i++){
		for(int j=i+1;j<(MidsList.size());j++) {
			simfunc.setLong(2, MidsList.get(i));
			simfunc.setLong(3, MidsList.get(j));
			ps.setLong(1, MidsList.get(i));
			ps.setLong(2, MidsList.get(j));
			simfunc.execute();
			ps.setFloat(3, simfunc.getFloat(1));
			ps.executeUpdate();
			connection.commit();
			
		}
	}
	
	ps.close();
	simfunc.close();
	rsfunc.close();
	}
	catch (ClassNotFoundException e) {
		e.printStackTrace();
	}
	catch (SQLException e) {
		e.printStackTrace();

	}finally{
		try {
			if(ps!=null) {ps.close();}
			if(rsfunc!=null) {rsfunc.close();}
			if(simfunc!=null) {simfunc.close();}
		} catch (SQLException e) {e.printStackTrace();}
		try {
			if(connection!=null) {connection.close();}
		} catch (SQLException e) {e.printStackTrace();}
		}
	}
public void PrintSimilarItems(long mid) {
	Connection connection = null;
	PreparedStatement ps = null;
	ResultSet rs = null;
	try {
		Class.forName("oracle.jdbc.driver.OracleDriver");
		connection = DriverManager.getConnection(conn_str, user_name, pass_word);
		ps = connection.prepareStatement("SELECT TITLE, SIMILARITY FROM SIMILARITY, MediaItems WHERE (SIMILARITY.MID1 = ? OR SIMILARITY.MID2 = ?) AND SIMILARITY.SIMILARITY >= 0.3 AND (MID=MID1 OR MID=MID2) AND MID != ? ORDER BY SIMILARITY ASC");
		ps.setLong(1, mid);
		ps.setLong(2, mid);
		ps.setLong(3, mid);
		rs = ps.executeQuery();
		while(rs.next()) {
			System.out.println(rs.getString(1) + " " + rs.getFloat(2));
		}
		ps.close();
		rs.close();
	}
	catch (ClassNotFoundException e) {
		e.printStackTrace();
	}
	catch (SQLException e) {
		e.printStackTrace();

	}finally{
		try {
			if(ps!=null) {ps.close();}
			
		} catch (SQLException e) {e.printStackTrace();}
		try {
			if(connection!=null) {connection.close();}
		} catch (SQLException e) {e.printStackTrace();}
		}
	
} 


}



