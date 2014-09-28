CreateTile =  Backbone.View.extend({
	className: 'sidebar-cell',
	
	events: {
		"click": "makeJob"
	},

	initialize: function(){
		this.render();
	},

	makeJob: function() {
		router.navigate("create", {trigger: true});
	},

	render: function(){
		this.$el.html('<div class="job-add"> + </div>');
	}
});