CreateTile =  Backbone.View.extend({
	className: 'sidebar-cell',
	
	events: {

	},

	initialize: function(){
		this.collection.bind("add", this.render, this);
		this.collection.bind("reset", this.render, this);
		this.render();
	},

	render: function(){
		this.$el.html('<div class="job-add"> + </div>');
	}
});