AppRouter = Backbone.Router.extend({

	routes: {
		"detail/:id" : "job",
		"create" : "create",
		"*actions": "home"
	},

	setup: function(){
		if($("#sidebar").length == 0){
			var v = new JobOverview({collection: jobs});
			$("body").append(v.el);
		}

		$("#content").remove();

	},


	home: function(){
		this.setup();
	},

	create: function(){
		this.setup();
		var v = new JobCreate();
		$("body").append(v.el);
	},

	job: function(id){
		this.setup();
		var job = jobs.get(id);
		var v = new JobDetail({model: job});
		$("body").append(v.el);
	}
});