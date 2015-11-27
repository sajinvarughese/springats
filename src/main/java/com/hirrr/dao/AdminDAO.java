package com.hirrr.dao;

import javax.servlet.http.HttpServletRequest;

public interface AdminDAO {

	String denyPostedJob(HttpServletRequest request);

	String viewAllJobPosted(HttpServletRequest request);

	String approveJob(HttpServletRequest request);

}
