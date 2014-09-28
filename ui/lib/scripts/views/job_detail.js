JobDetail = Backbone.View.extend({

	id: "content",

	template: _.template($("#job-detail").html()),

	events: {
		"click .detail" : "download"
	},

	initialize: function(){
		this.model.bind("change:progress", this.render, this);
		this.model.bind("change:mode", this.render, this);
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
			phase: _this.model.getMode() ? "map" : "reduce",
			progress: progress
		});
		this.$el.append(this.template(data));
		return this;
	},

	download: function(){
		this.model.download();
	}
	
});