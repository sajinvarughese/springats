/**
 * http://usejsdoc.org/
 */
var jobDelete = Backbone.Model.extend({ url: './deletepostedjob.hirrr' });

var jobDetails = Backbone.Model.extend({ url: './jobdetails.hirrr' }); // Admin and Employer using this model

var denyJob = Backbone.Model.extend({ url : './denyjob.hirrr' }); // Admin using this model.

var confirmJob = Backbone.Model.extend({ url : './approvejob.hirrr' }); // Admin using this model.


var deletejob = new jobDelete();

var jobdetails = new jobDetails();

var denyjob = new denyJob();

var confirmjob = new confirmJob();
