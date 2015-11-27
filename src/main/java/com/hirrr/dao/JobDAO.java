package com.hirrr.dao;

import javax.servlet.http.HttpServletRequest;

public interface JobDAO {
	String registerUser(HttpServletRequest request);
	String loginUser(HttpServletRequest request);
	String postaJob(HttpServletRequest request);
	String fetchJobs(HttpServletRequest request);
	String fetchJobDetails(HttpServletRequest request);
	String deletePostedJob(HttpServletRequest request);
	String signOut(HttpServletRequest request);
	String activateUser(HttpServletRequest request);
}
