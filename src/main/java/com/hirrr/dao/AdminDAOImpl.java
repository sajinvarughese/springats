package com.hirrr.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AdminDAOImpl implements AdminDAO{
@Autowired private DataSource dataSource;
	private static Logger LOGGER =  Logger.getLogger(AdminDAOImpl.class.getName()); 

	
	public String denyPostedJob(HttpServletRequest request) {
		// TODO Auto-generated method stub
		HttpSession session = request.getSession();
		String userType = session.getAttribute("userType") == null ? "" : session.getAttribute("userType").toString();
		String outputString = "";
		
		if(userType.equals("Admin")){
			String jobId = request.getParameter("jobid");
			String status = request.getParameter("status") == null ? "" : request.getParameter("status");
			
			
			// DataBase connection code...............................
			Connection con = null;
			PreparedStatement ps = null;
			// ...................................................>>>>
			try{			
			con = dataSource.getConnection();

			String sql1 = "";
			if(status.equals("All")){
				sql1 = "update jobpost_details set job_status = ? where job_posted_by = (select * from (select job_posted_by  from jobpost_details where jobid = ?) as x)";
			}else{
				sql1 = "update jobpost_details set job_status = ? where jobid = ?";			
			}
			ps = con.prepareStatement(sql1);
			ps.setString(1, "Denied");
			ps.setString(2, jobId);
	
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

	
	public String viewAllJobPosted(HttpServletRequest request) {
		// TODO Auto-generated method stub
		HttpSession session = request.getSession();
		JSONArray obj = null;
		// DataBase connection code...............................
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement ps = null;

		// ...................................................>>>>
		try{
		con = dataSource.getConnection();
		String userType = session.getAttribute("userType").toString();
		if(userType.equals("Admin")){
		
		String query = "select jobid, job_title, job_location, job_expiry_on, job_posted_by   from jobpost_details where job_status = ?";
		ps = con.prepareStatement(query);
		ps.setString(1, "Pending");
		rs = ps.executeQuery();
		obj = new JSONArray();
		while(rs.next()){
			JSONObject list1 = new JSONObject();
            list1.put("jobId",rs.getString("jobid"));
            list1.put("jobTitle",rs.getString("job_title"));
            list1.put("jobLocation",rs.getString("job_location"));
            list1.put("jobExpiry", rs.getString("job_expiry_on"));
            list1.put("jobPostedBy", rs.getString("job_posted_by"));
            
            obj.put(list1);
		}
		
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


	public String approveJob(HttpServletRequest request) {
		// TODO Auto-generated method stub
		HttpSession session = request.getSession();
		String userType = session.getAttribute("userType") == null ? "" : session.getAttribute("userType").toString();
		
		String outputString = "";
		if(userType.equals("Admin")){
			String jobId = request.getParameter("jobid");
			String status = request.getParameter("status") == null ? "" : request.getParameter("status");
			
			
			// DataBase connection code...............................
			Connection con = null;
			PreparedStatement ps = null;
	
			// ...................................................>>>>
			try{			
				con = dataSource.getConnection();
			String sql1 = "";
			if(status.equals("All")){
				sql1 = "update jobpost_details set job_status = ? where job_posted_by = (select * from (select job_posted_by  from jobpost_details where jobid = ?) as x)";
			}else{
				sql1 = "update jobpost_details set job_status = ? where jobid = ?";			
			}
			ps = con.prepareStatement(sql1);
			ps.setString(1, "Approved");
			ps.setString(2, jobId);
	
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

}
