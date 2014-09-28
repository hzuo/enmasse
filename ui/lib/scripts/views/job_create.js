JobCreate = Backbone.View.extend({

	id: "content",

	template: _.template($("#job-create").html()),

	events: {

	},

	initialize: function(){
		this.render();
	},

	postRender: function() {
	    var mapEditor = ace.edit("map-editor");
	    mapEditor.setTheme("ace/theme/monokai");
	    mapEditor.getSession().setMode("ace/mode/javascript");

	    var reduceEditor = ace.edit("reduce-editor");
	    reduceEditor.setTheme("ace/theme/monokai");
	    reduceEditor.getSession().setMode("ace/mode/javascript");
	    return this;
	},

	render: function(){
		this.$el.html(this.template());	    
		return this;
	},

});