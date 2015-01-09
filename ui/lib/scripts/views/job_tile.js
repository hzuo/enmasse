JobTile = Backbone.View.extend({
	className: 'sidebar-cell job',

	template: _.template($("#job-cell").html()),

	events: {
		"click" : "detail"
	},

	initialize: function(){
		this.model.bind("change:progress", this.updateProgress, this);
		this.render();
	},

	render: function(){
		this.$el.html(this.template({
			id: Math.floor(Math.abs(Number(this.model.get("id")) / 10000) % 10000),
			name: this.model.get("name")
		}));
		this.updateProgress();
		return this;
	},

	updateProgress: function(){
		var progress = this.model.getProgress();
		this.$el.css("background-image", function(){
			var i = (100 - progress).toFixed(2);
			var s = "-webkit-linear-gradient(right, transparent " + i + "%,rgba(107,213,255,0.7) " + i + "%, rgba(107,213,255,0.7) 100%)";
			return s;
		});
	},

	detail: function(){
		router.navigate("detail/" + this.model.get("id"), {trigger:true});
	}

});