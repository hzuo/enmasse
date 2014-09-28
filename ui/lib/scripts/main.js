$(document).ready(function(){
	tracker = new ProgressTracker();
	router = new AppRouter();
	jobs = new Jobs();
	jobs.fetch();
	Backbone.history.start({pushState: true}); 
});