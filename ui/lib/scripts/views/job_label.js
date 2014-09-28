JobLabel = Backbone.View.extend({

	id: "sidebar-label",

	events: {

	},

	initialize: function(){

		this.render();
	},

	render: function(){
		this.$el.html("jobs");
		return this;
	},
});