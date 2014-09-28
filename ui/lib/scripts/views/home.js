Home = Backbone.View.extend({

	id: "content",

	template: _.template($("#home-template").html()),

	events: {

	},

	initialize: function(){
		this.render();
	},

	render: function(){
		this.$el.html(this.template());
		return this;
	},

});