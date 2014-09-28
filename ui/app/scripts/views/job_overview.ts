JobOverview = Backbone.View.extend({
	id: 'sidebar',
	
	events: {

	},

	initialize: function(){
		this.collection.bind("add", this.render, this);
		this.collection.bind("reset", this.render, this);
		this.render();
	},

	render: function(){
		this.$el.html("");
		_this = this;
		this.collection.forEach(function(model){
			var v = new JobTile({model:model});
			_this.$el.append(v.el);
		});
		var v = new CreateTile();
		this.$el.append(v.el);
	}
});