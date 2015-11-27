package com.hirrr.controllers;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.hirrr.dao.JobDAO;



@Controller
public class UserController {
	@Autowired public JobDAO dao;
	
	private static Logger LOGGER =  Logger.getLogger(UserController.class.getName()); 
	
	@RequestMapping(value = "/userregistration.hirrr", method = RequestMethod.POST)
	public @ResponseBody String userRegistration(HttpServletRequest request, HttpServletResponse response) throws Exception {
		LOGGER.info("Registration process started");
		String jsonValue = dao.registerUser(request);
		return jsonValue;
	}
	
	@RequestMapping(value = "/userlogin.hirrr", method = RequestMethod.POST)
	public @ResponseBody String userLogin(HttpServletRequest request, HttpServletResponse response){
		LOGGER.info("Login process started");
		String jsonValue = dao.loginUser(request);
		return jsonValue;
	}
	
	@RequestMapping(value = "/postjob.hirrr", method = RequestMethod.POST)
	public @ResponseBody String postJob(HttpServletRequest request, HttpServletResponse response){
		LOGGER.info("job posting process started");
		String jsonValue = dao.postaJob(request);
		return jsonValue;
	}	
	
	@RequestMapping(value = "/fetchjobs.hirrr" , method = RequestMethod.GET)
	public @ResponseBody String fetchJobs(HttpServletRequest request, HttpServletResponse response){
		LOGGER.info("job fetch process started");
		String jsonValue = dao.fetchJobs(request);
		return jsonValue;
	}
	
	@RequestMapping(value = "/jobdetails.hirrr" , method = RequestMethod.GET)
	public @ResponseBody String fetchJobDetails(HttpServletRequest request, HttpServletResponse response){
		LOGGER.info("job details display process started");
		String jsonValue = dao.fetchJobDetails(request);
		return jsonValue;
	}
	
	@RequestMapping(value = "/deletepostedjob.hirrr" , method = RequestMethod.GET)
	public @ResponseBody String deletePostedJob(HttpServletRequest request, HttpServletResponse response){
		LOGGER.info("delete Posted job process started");
		String jsonValue = dao.deletePostedJob(request);
		return jsonValue;
	}
	
	@RequestMapping(value = "/signout.hirrr" , method = RequestMethod.GET)
	public @ResponseBody String signOut(HttpServletRequest request, HttpServletResponse response){
		LOGGER.info("Sign Out process started");
		String jsonValue = dao.signOut(request);
		return jsonValue;
	}
	
	@RequestMapping(value = "/useractivation.hirrr" , method = RequestMethod.GET)
	public ModelAndView activateUser(HttpServletRequest request, HttpServletResponse response){
		LOGGER.info("Sign Out process started");
		dao.activateUser(request);
		ModelAndView mv = new ModelAndView();
		mv.setViewName("login");
		return mv;
	}
	
}
