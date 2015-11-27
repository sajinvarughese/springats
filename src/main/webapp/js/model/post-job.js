/**
 * http://usejsdoc.org/
 */

var jobModel = Backbone.Model.extend({
	
	defaults : {

		jobTitle : "",

		careerLevel : "",

		noOfVacancies : "",
		
		location : "",
		
		skills : "",
		
		positionType : "",
		
		description : "",
		
		functions : "",
		
		industries : "",
		
		requiredExperience : "",
		
		education : "",
		
		receiveFrom : "",
		
		languagesKnown : "",
		
		annualSalary : "",
		
		gender : "",
		
		jobExpiryDate : "",
		
		changeEmail : ""

	},
	
	// First execution starts here
	initialize : function() {

		console.log("initialize client");

	},
	
	// automatically take this url when we invoke save method for this model
	url : "./postjob.hirrr",

	
});

var JobPostCollection = Backbone.Collection.extend({
	model : jobModel,
	url : './fetchjobs.hirrr'
	
});

var approveJobCollection = Backbone.Collection.extend({
	model : jobModel,
	url : './viewallposted.hirrr'
});

var approvejobcollection = new approveJobCollection();

var jobpostcollection = new JobPostCollection();

var jobPostModel = new jobModel();

