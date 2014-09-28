JobCreate = Backbone.View.extend({

	id: "content",

	template: _.template($("#job-create").html()),

	events: {
		"click .create-job-button": "newJob"
	},

	initialize: function(){
		this.render();
	},

	newJob: function() {
		var name = $(".job-name").val();
		var dataloc = $(".job-dataloc").val();
		var mapCode = this.mapEditor.getSession().getValue();
		var reduceCode = this.reduceEditor.getSession().getValue();
		Job.create(name, dataloc, mapCode, reduceCode);
	},

	postRender: function() {
	    this.mapEditor = ace.edit("map-editor");
	    this.mapEditor.setTheme("ace/theme/monokai");
	    this.mapEditor.getSession().setMode("ace/mode/javascript");

	    this.reduceEditor = ace.edit("reduce-editor");
	    this.reduceEditor.setTheme("ace/theme/monokai");
	    this.reduceEditor.getSession().setMode("ace/mode/javascript");
	    return this;
	},

	render: function(){
		this.$el.html(this.template());	    
		return this;
	},

});