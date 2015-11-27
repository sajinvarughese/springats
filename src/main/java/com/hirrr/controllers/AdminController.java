package com.hirrr.controllers;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hirrr.dao.AdminDAO;


@Controller
public class AdminController {
	@Autowired private AdminDAO dao;
	
	private static Logger LOGGER =  Logger.getLogger(AdminController.class.getName()); 
	
	@RequestMapping(value = "/denyjob.hirrr", method = RequestMethod.GET)
	public @ResponseBody String denyJob(HttpServletRequest request, HttpServletResponse response) throws Exception {
		LOGGER.info("Deny Job process started");
		String jsonValue = dao.denyPostedJob(request);
		return jsonValue;
	}
	
	@RequestMapping(value = "/viewallposted.hirrr", method = RequestMethod.GET)
	public @ResponseBody String viewAllPosted(HttpServletRequest request, HttpServletResponse response) throws Exception {
		LOGGER.info("fetch all job posted process started");
		String jsonValue = dao.viewAllJobPosted(request);
		return jsonValue;
	}
	
	
	@RequestMapping(value = "/approvejob.hirrr", method = RequestMethod.GET)
	public @ResponseBody String approveJob(HttpServletRequest request, HttpServletResponse response) throws Exception {
		LOGGER.info("approve process started");
		String jsonValue = dao.approveJob(request);
		return jsonValue;
	}
	
}
