package org.hackathon.dataconnector;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletConfig;
import javax.servlet.http.*;

@SuppressWarnings("serial")
public class FetchMedicineData extends HttpServlet {	
	
	static Connection conn=null;
	static String mysql_address = "http://ec2-54-152-187-53.compute-1.amazonaws.com/";

	@Override
	public void init(ServletConfig config){
		connectToDatabase();
	}

	@Override
	public void destroy(){
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unused")
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		StringBuffer buff = new StringBuffer(); 
		String retval = "";
		String operation = req.getParameter("operation");
		String medName = req.getParameter("medName");
		String uId = req.getParameter("userid");

		resp.setContentType("text/plain");
		
		try{
			switch(operation){
			case "subscribe": updateDatabase(medName, uId);
				resp.getWriter().println("User subscribed");
				break;
			case "select": String jsonString = queryDatabase(medName, uId);
			resp.getWriter().println(jsonString);
			break;
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
	}

	public void updateDatabase(String medName, String uId) throws SQLException{

		PreparedStatement stmt = (PreparedStatement) conn.prepareStatement("SELECT medId FROM MedicineInfo WHERE name=?");
		stmt.setString(1,medName);

		ResultSet rs = stmt.executeQuery();
		rs.next();
		String mId = rs.getString(1);

		PreparedStatement stmt2 = (PreparedStatement) conn.prepareStatement("INSERT INTO Subscription(userId, medId) VALUES(?, ?)");
		stmt2.setString(1,uId);
		stmt2.setString(2,mId);
		stmt2.executeQuery();
	}

	public String queryDatabase(String medName, String uId) throws SQLException{

		PreparedStatement stmt = (PreparedStatement) conn.prepareStatement("SELECT medId, name, lastFDA, link, safetyRating, "
				+ "bestAlternate, manufacturer FROM MedicineInfo WHERE name=?");
		stmt.setString(1,medName);

		ResultSet rs = stmt.executeQuery();
		rs.next();
		String mId = rs.getString(1);
		String mName = rs.getString(2);
		String lastFDA = rs.getString(3);
		String link = rs.getString(4);
		String safetyRating = rs.getString(5);
		String bestAlternate = rs.getString(6);
		String manufacturer = rs.getString(7);

		String jsonString = convertToJson(mId, mName, lastFDA, link, safetyRating, bestAlternate, manufacturer);
		return jsonString;
	}

	private String convertToJson(String mId, String mName, String lastFDA, String link, String safetyRating, 
			String bestAlternate, String manufacturer){

		StringBuilder json = new StringBuilder();
		json.append("{\"mId\":\"" + mId + "\", ");
		json.append("\"mName\":\"" + mName + "\", ");
		json.append("\"lastFDA\":\"" + lastFDA + "\", ");
		json.append("\"link\":\"" + link + "\", ");
		json.append("\"safetyRating\":\"" + safetyRating + "\", ");
		json.append("\"bestAlternate\":\"" + bestAlternate + "\", ");
		json.append("\"manufacturer\":\"" + manufacturer + "\"}");

		return json.toString();
	}

	public void connectToDatabase(){
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://" + mysql_address  + ":3306/mylan","user","password");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}