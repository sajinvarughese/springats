package com.hirrr.dao;

import java.io.BufferedReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hirrr.utils.SendMail;

@Component
public class JobDAOImpl implements JobDAO{
	@Autowired private DataSource dataSource;
	
	private static Logger LOGGER =  Logger.getLogger(JobDAOImpl.class.getName()); 

	public String registerUser(HttpServletRequest request) {
		StringBuffer jb = new StringBuffer();
		String line = null;
		String output = "";
		// DataBase connection code...............................
		Connection con = null;
		PreparedStatement stmt = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		// ...................................................>>>>

		try {
			con = dataSource.getConnection();
			// Reading parameters from request and converting it into jsonstring.
			BufferedReader reader = request.getReader();
			while ((line = reader.readLine()) != null)
				jb.append(line);
			JSONObject userdetails = new JSONObject(jb.toString());
			// ........................................................>>>

			String username = userdetails.getString("name");
			String email = userdetails.getString("email");
			String password = userdetails.getString("pwd");
			String datetime = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss")
					.format(new Date());

			// Creating email data for verifying the users registered.
			String stringToEncode = email + "~||~" + datetime;
			byte[] str = Base64.encodeBase64(stringToEncode.getBytes());
			String verification_link = "http://localhost:8080/ats/useractivation.hirrr?verificationLink="
					+ new String(str);
			String mailData = "You need to verify your User name and email address. \n"
					+ "User Name : "
					+ email
					+ "\n"
					+ "Password : "
					+ password
					+ "\n"
					+ "Please click on the link below to continue \n"
					+ verification_link;
			// ....................................................>>>

			String query1 = "select * from register where email = ?";
			stmt = con.prepareStatement(query1);
			stmt.setString(1, email);
			rs = stmt.executeQuery();
			if (rs.next()) { // checking user already registered
				if (rs.getString("user_status").equals("1")) { // checking user
																// already
																// verified
																// his/her
																// emailid
					output = "{\"status\" : \"Emailid Already Registered\"}";
				} else {
					output = "{\"status\" : \"Already Registered. Confirm emailid\"}";
				}
			} else {
				// if user registering for the first time.

				String query = "Insert into register(full_name, email, password, user_status, time) values(?, ?, sha1(?), ?, ?)";
				ps = con.prepareStatement(query);
				ps.setString(1, username);
				ps.setString(2, email);
				ps.setString(3, password);
				ps.setString(4, "0");
				ps.setString(5, datetime);

				int a = ps.executeUpdate();

				if (a > 0) {
					System.out.println("inserted successfully");
					SendMail.sendMail(email, "Email verification", mailData);

					output = "{\"status\" : \"success\"}";
				} else {
					output = "{\"status\" : \"Registration not Successfull\"}";
				}
			}
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
			output = "{\"status\" : \"error\"}";
		} finally {
			// Closing database resources, so that it will return back to the db pool.

		    try { if (rs != null) rs.close(); } catch (Exception e) {e.printStackTrace();};
		    try { if (ps != null) ps.close(); } catch (Exception e) {e.printStackTrace();};
		    try { if (stmt != null) stmt.close(); } catch (Exception e) {e.printStackTrace();};
		    try { if (con != null) con.close(); } catch (Exception e) {e.printStackTrace();};
		}
		
		// TODO Auto-generated method stub
		return output;
	}

	public String loginUser(HttpServletRequest request) {
		// TODO Auto-generated method stub
		String outputString = "";
		StringBuffer jb = new StringBuffer();
		String line = null;
		
		
		HttpSession session = request.getSession(); //creating session object
	
		// database Connection code..........................
		  Connection con = null;
		  ResultSet rs = null;
		  PreparedStatement ps = null;
		 //.......................................> 
	try{  
		con = dataSource.getConnection();
		// Reading parameter from request and convert it to jsonstring.................... 
		BufferedReader reader = request.getReader(); 
		while ((line = reader.readLine()) != null)
			jb.append(line);
	
			JSONObject userdetails = new JSONObject(jb.toString());
		
		//...................................................>>
			
			String email = userdetails.getString("email");
			String password = userdetails.getString("pwd");
	
			String query = "select * from userlogin where emailid = ? and password = sha1(?)";
			ps = con.prepareStatement(query);
			ps.setString(1, email);
			ps.setString(2, password);
			
			rs = ps.executeQuery();
			if(rs.next()){
				//Login Success
				session.setAttribute("email", rs.getString("emailid"));
				session.setAttribute("fullName", rs.getString("full_name"));
				session.setAttribute("sessionid", session.getId());
				session.setAttribute("userType", rs.getString("user_type"));
				if(rs.getString("user_type").equals("Admin")){
					outputString = "{\"status\" : \"admin\"}";
				}else{
					outputString = "{\"status\" : \"success\"}";
				}
			}else{
				outputString = "{\"status\" : \"error\"}";
			}
	
	}catch(Exception e){
		outputString = "{\"status\" : \"error\"}";
		LOGGER.severe(e.getMessage());
	}finally {
		// Closing database resources, so that it will return back to the db pool.
	    try { if (rs != null) rs.close(); } catch (Exception e) {e.printStackTrace();};
	    try { if (ps != null) ps.close(); } catch (Exception e) {e.printStackTrace();};
	    try { if (con != null) con.close(); } catch (Exception e) {e.printStackTrace();};
	}
	
		return outputString;
	}

	public String postaJob(HttpServletRequest request) {
		// TODO Auto-generated method stub
		HttpSession session = request.getSession();
		String useremail = session.getAttribute("email").toString(); // taking emailid from Session
		
		String outputString = "";
		StringBuffer jb = new StringBuffer();
		String line = null;
	
		// DataBase connection code...............................
		Connection con = null;
		PreparedStatement stmt = null;

		// ...................................................>>>>
		
		try {
			con = dataSource.getConnection();
			// Reading parameters from request and converting it into jsonstring.
			BufferedReader reader = request.getReader();
			while ((line = reader.readLine()) != null)
				jb.append(line);
			JSONObject userdetails = new JSONObject(jb.toString());
			// ........................................................>>>
			
			// Reading data from Json String..............................
			String jobTitle = userdetails.getString("jobTitle");
			String careerLevel = userdetails.getString("careerLevel");
			String noOfVacancies = userdetails.getString("noOfVacancies");
			String location = userdetails.getString("location");
			String skills = userdetails.getString("skills");
			String positionType = userdetails.getString("positionType");
			String description = userdetails.getString("description");
			JSONArray functionsArray = userdetails.getJSONArray("functions");
			String functions = functionsArray.toString();
			JSONArray industriesArray = userdetails.getJSONArray("industries");
			String industries = industriesArray.toString();
			String requiredExperience = userdetails.getString("requiredExperience");
			String education = userdetails.getString("education");
			String receiveFrom = userdetails.getString("receiveFrom");
			JSONArray languagesKnownArray = userdetails.optJSONArray("languagesKnown");
			boolean languagenullflag = false;
			String languagesKnown = "";

			if(languagesKnownArray == null){
				languagenullflag = true;
			}else{
			languagesKnown = languagesKnownArray.toString();
			}
			String annualSalary = userdetails.getString("annualSalary");
			String gender = userdetails.getString("gender");
			String jobExpiryDate = userdetails.getString("jobExpiryDate");
			String changeEmail = userdetails.getString("changeEmail");
			//..................................................>>>
			
			//..... Insert Job Details into Database......................
			String query1 = "insert into jobpost_details(job_title, career_level, no_of_vacancies, job_location, "
					+ "position_type, job_description, desired_skills, required_exp, industry, function, education, sex, languages, "
					+ "receive_application, annual_salary, job_expiry_on, to_email, date_first_posted, job_posted_by, job_status) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now(),?,?)";
			stmt = con.prepareStatement(query1);
			stmt.setString(1, jobTitle);
			stmt.setString(2, careerLevel);
			stmt.setString(3, noOfVacancies);
			stmt.setString(4, location);
			stmt.setString(5, positionType);
			stmt.setString(6, description);
			stmt.setString(7, skills);
			stmt.setString(8, requiredExperience);
			stmt.setString(9, industries);
			stmt.setString(10, functions);
			stmt.setString(11, education);
			stmt.setString(12, gender);
			if(languagenullflag == true){
				stmt.setString(13, "");
			}else{
				stmt.setString(13, languagesKnown);
			}
			stmt.setString(14, receiveFrom);
			stmt.setString(15, annualSalary);
			stmt.setString(16, jobExpiryDate);
			stmt.setString(17, changeEmail);
			stmt.setString(18, useremail);
			stmt.setString(19, "Pending");
			
			int a = stmt.executeUpdate();
			
			if(a > 0){
				LOGGER.info("success");
				outputString = "{\"status\" : \"success\"}";
			}else{
				outputString = "{\"status\" : \"error\"}";
			}
			//......................................>>>
			
		}catch(Exception e){
			
			outputString = "{\"status\" : \"error\"}";
			LOGGER.severe(e.getMessage());
			
		}finally {
			// Closing database resources, so that it will return back to the db pool.
		    try { if (stmt != null) stmt.close(); } catch (Exception e) {e.printStackTrace();};
		    try { if (con != null) con.close(); } catch (Exception e) {e.printStackTrace();};
		}
		return outputString;
	}

	public String fetchJobs(HttpServletRequest request) {
		// TODO Auto-generated method stub
		HttpSession session = request.getSession();
		JSONArray obj = null;
		// DataBase connection code...............................
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;	
		// ...................................................>>>>
		try{
	    con = dataSource.getConnection();
		String useremail = session.getAttribute("email").toString();
		String query = "select jobid, job_title, job_status, job_location, job_expiry_on  from jobpost_details where job_posted_by = ?";
		ps = con.prepareStatement(query);
		ps.setString(1, useremail);
		rs = ps.executeQuery();
		obj = new JSONArray();
		while(rs.next()){
			JSONObject list1 = new JSONObject();
            list1.put("jobId",rs.getString("jobid"));
            list1.put("jobTitle",rs.getString("job_title"));
            list1.put("jobStatus",rs.getString("job_status"));
            list1.put("jobLocation", rs.getString("job_location"));
            list1.put("jobExpiry", rs.getString("job_expiry_on"));
            
            obj.put(list1);
		}
		
		
		}catch(Exception e){
			LOGGER.severe(e.getMessage());
		}finally{
			// Closing database resources, so that it will return back to the db pool.
		    try { if (rs != null) rs.close(); } catch (Exception e) {e.printStackTrace();};
		    try { if (ps != null) ps.close(); } catch (Exception e) {e.printStackTrace();};
		    try { if (con != null) con.close(); } catch (Exception e) {e.printStackTrace();};	
		}

		return obj.toString();
	}

	public String fetchJobDetails(HttpServletRequest request) {
		// TODO Auto-generated method stub
		HttpSession session = request.getSession();
		String jobid = request.getParameter("jobid") == null ? "" : request.getParameter("jobid");
		String useremail = session.getAttribute("email") == null ? "" : session.getAttribute("email").toString();
		String userType = session.getAttribute("userType") == null ? "" : session.getAttribute("userType").toString();
		JSONObject obj = null;
		// DataBase connection code...............................
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;	
		// ...................................................>>>>
		
		try{
			con = dataSource.getConnection();
			String query = "";
			
			// Admin can see job details of all jobs. but Employers only see the job they posted.
			if(userType.equals("Admin")){
				query = "select * from jobpost_details where jobid = ?";
			}else{
				query = "select * from jobpost_details where jobid = ? and job_posted_by = ?";
			}
			ps = con.prepareStatement(query);
			ps.setString(1, jobid);
			
			if(userType.equals("Employer")){ 
			ps.setString(2, useremail);
			}
			
			rs = ps.executeQuery();
			
			obj = new JSONObject();
			while(rs.next()){
				obj.put("jobId",rs.getString("jobid"));
				obj.put("jobTitle",rs.getString("job_title"));
				obj.put("jobStatus",rs.getString("job_status"));
				obj.put("jobLocation", rs.getString("job_location"));
				obj.put("jobExpiry", rs.getString("job_expiry_on"));
				obj.put("reqExperience", rs.getString("required_exp"));
				obj.put("noOfVacancies", rs.getString("no_of_vacancies"));
				obj.put("jobDescription", rs.getString("job_description"));
				obj.put("jobEducation", rs.getString("education"));
				obj.put("jobSkills", rs.getString("desired_skills"));
			}
			
			
		}catch(Exception e){
			
			LOGGER.severe(e.getMessage());
			
		}finally{
			// Closing database resources, so that it will return back to the db pool.
		    try { if (rs != null) rs.close(); } catch (Exception e) {e.printStackTrace();};
		    try { if (ps != null) ps.close(); } catch (Exception e) {e.printStackTrace();};
		    try { if (con != null) con.close(); } catch (Exception e) {e.printStackTrace();};	
		}
		
		return obj.toString();
	}

	public String deletePostedJob(HttpServletRequest request) {
		// TODO Auto-generated method stub
		HttpSession session = request.getSession();
		String sessionid = session.getAttribute("sessionid") == null ? "" : session.getAttribute("sessionid").toString();
		LOGGER.info("session id :::: "+sessionid);
		String outputString = "";
		if(sessionid.equals(session.getId())){
			String jobId = request.getParameter("jobid");
			String useremail = session.getAttribute("email").toString();
			
			LOGGER.info(jobId + " :: "+useremail);
			
			// DataBase connection code...............................
			Connection con = null;
			PreparedStatement ps = null;
			
			// ...................................................>>>>
			try{	
			con = dataSource.getConnection();	
			String sql1 = "delete from jobpost_details where jobid = ? and job_posted_by = ?";
			ps = con.prepareStatement(sql1);
			ps.setString(1, jobId);
			ps.setString(2, useremail);
				
			int a = ps.executeUpdate();
			
			if(a > 0){
				outputString = "{\"status\" : \"success\"}";
			}else{
				outputString = "{\"status\" : \"error\"}";
			}
			
			}catch(Exception e){
				LOGGER.info("Error:" + e.getMessage());
				outputString = "{\"status\" : \"error\"}";
			}finally{
			// Closing database resources, so that it will return back to the db pool.
		    try { if (ps != null) ps.close(); } catch (Exception e) {e.printStackTrace();};
		    try { if (con != null) con.close(); } catch (Exception e) {e.printStackTrace();};	
		}
	
		}else{
			try{
				outputString = "{\"status\" : \"error\"}";
			
			}catch(Exception e){
				LOGGER.severe(e.getMessage());
			}
		}
		return outputString;
	}

	public String signOut(HttpServletRequest request) {
		// TODO Auto-generated method stub
		HttpSession session = request.getSession();
		String outputString = "";
		session.invalidate();
		LOGGER.info("User Session Deleted..");
		
		outputString = "{}";
		return outputString;
	}

	public String activateUser(HttpServletRequest request) {
		// TODO Auto-generated method stub
		LOGGER.info("inside Servlet");
		String emailId = "";
		String time = "";
		String key = request.getParameter("verificationLink");
		byte[] value = Base64.decodeBase64(key);

		String decodedString = new String(value); // Decoded Email~||~time id
		System.out.println(decodedString);
		if(decodedString.contains("~||~")){
		 emailId = decodedString.split("\\~\\|\\|\\~")[0];
		 time = decodedString.split("\\~\\|\\|\\~")[1];
		}

		HttpSession session = request.getSession();
		/* Changing the user status from 0 to 1 if user exists. if already verified nothing doing */
		
		ResultSet rs = null;
		ResultSet rs1 = null;
		PreparedStatement ps2 = null;
		PreparedStatement ps3 = null;
		PreparedStatement ps4 = null;
		PreparedStatement ps = null;
		Connection con = null;
		
		try {
			con = dataSource.getConnection();
			String query1 = "select * from register where email = ? and time = ?";
			ps2 = con.prepareStatement(query1);
			ps2.setString(1, emailId);
			ps2.setString(2, time);
			rs = ps2.executeQuery();

			if (rs.next()) {
				String str = rs.getString("user_status");
				if (str.equals("1")) {
					LOGGER.info("Emailid already verified.");
					session.setAttribute("alertString","Emailid already verified.");
				} else {
					String query = "update register set user_status = ? where email = ?";
					ps = con.prepareStatement(query);
					ps.setString(1, "1");
					ps.setString(2, emailId);

					int a = ps.executeUpdate();

					if (a > 0) {
						
						/* Moving data from register table to userlogin table*/
						String query2 = "select * from register where user_status = ? and email = ?";
						ps3 = con.prepareStatement(query2);
						ps3.setString(1, "1");
						ps3.setString(2, emailId);
						rs1 = ps3.executeQuery();
						if(rs1.next()){
							String query3 = "insert into userlogin(user_id, full_name, emailid, password, user_type) values(?, ?, ?, ?, ?)";
							ps4 = con.prepareStatement(query3);
							ps4.setString(1, rs1.getString("user_id"));
							ps4.setString(2, rs1.getString("full_name"));
							ps4.setString(3, rs1.getString("email"));
							ps4.setString(4, rs1.getString("password"));
							ps4.setString(5, "Employer");
							
							int b = ps4.executeUpdate();
							if(b > 0){
								// data successfully moved.
								LOGGER.info("Emailid verified.");
								session.setAttribute("alertString","Emailid verified.");
								
							}
							
						}
						
						
					}
				}
			}
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());;
		}finally {
			// Closing database resources, so that it will return back to the db pool.

		    try { if (rs != null) rs.close(); } catch (Exception e) {e.printStackTrace();};
		    try { if (rs1 != null) rs1.close(); } catch (Exception e) {e.printStackTrace();};
		    try { if (ps != null) ps.close(); } catch (Exception e) {e.printStackTrace();};
		    try { if (ps2 != null) ps2.close(); } catch (Exception e) {e.printStackTrace();};
		    try { if (ps3 != null) ps3.close(); } catch (Exception e) {e.printStackTrace();};
		    try { if (ps4 != null) ps4.close(); } catch (Exception e) {e.printStackTrace();};
		    try { if (con != null) con.close(); } catch (Exception e) {e.printStackTrace();};
		}
		
		
		return "success";
	}

	
}
