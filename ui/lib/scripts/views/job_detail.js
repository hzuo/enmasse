JobDetail = Backbone.View.extend({

	id: "content",

	template: _.template($("#job-detail").html()),

	events: {
		"change:progress": "render",
		"change:mode": "render"
	},

	initialize: function(){
		this.render();
	},

	render: function(){
		this.$el.html("");
		var _this = this;
		var status;
		var progress = _this.model.getProgress();
		if (Number(progress) === 100) {
			status = "<span='status-complete'>complete</div>";
		} else {
			status = "in progress";
		}
		var data = $.extend(this.model.toJSON(), {
			status: status,
			phase: _this.model.getMode(),
			progress: progress
		});
		this.$el.append(this.template(data));
		return this;
	},
	
});