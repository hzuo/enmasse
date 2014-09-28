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
		$("body").append((new Home()).render().el);
	},

	create: function(){
		this.setup();
		var v = new JobCreate();
		v.render();
		$("body").append(v.el);
		v.postRender();
	},

	job: function(id){
		this.setup();
		var job = jobs.get(id);
		var v = new JobDetail({model: job});
		$("body").append(v.el);
	}
});