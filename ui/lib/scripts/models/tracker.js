ProgressTracker = Backbone.Model.extend({


	initialize: function(){
		this.updateProgress();
	},


	updateProgress: function(){
		var _this = this;
		this.fetch();
		setTimeout((function(){_this.updateProgress();}), 10000);
	},

	fetch: function(){
		_this = this;

		$.ajax({
			url: "/url",
			method: "GET",
			success: function(data){
				diff = [];


				for(var i=0; i<diff.length; i++){
					_this.trigger("change:progress:" + diff[i]);
				}
			}
		});
	}

});