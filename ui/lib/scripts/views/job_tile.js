JobTile = Backbone.View.extend({
	className: 'sidebar-cell job',

	template: _.template($("#job-cell").html()),

	events: {

	},

	initialize: function(){
		this.render();
	},

	render: function(){
		this.$el.html(this.template({
			id: this.model.get("id"),
			name: this.model.get("name")
		}));
		return this;
	},

});