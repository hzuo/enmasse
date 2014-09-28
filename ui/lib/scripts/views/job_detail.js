JobDetail = Backbone.View.extend({

	id: "content",

	template: _.template($("#job-detail").html()),

	events: {

	},

	initialize: function(){
		this.render();
	},

	render: function(){
		this.$el.html("");
		var data = $.extend(this.model.toJSON(), {
			phase: this.model.getMode(),
			progress: this.model.getProgress()
		});
		this.$el.append(this.template(data));
		return this;
	},
	
});