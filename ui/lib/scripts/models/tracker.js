ProgressTracker = Backbone.Model.extend({


	initialize: function(){
		this.updateProgress();
	},


	updateProgress: function(){
		var _this = this;
		this.fetch();
		setTimeout((function(){_this.updateProgress();}), 1000);
	},

	fetch: function(){
		_this = this;
		for(var i = 0; i<this.ids.length; i++){
			$.ajax({
				url: "/progress?id=" + ids[i],
				method: "GET",
				success: function(num){
					_this.a[_this.ids[i]] = Math.ceil(Number(num) * 100);
					this.trigger("change:progress:" + this.ids[i]);
					if(_this.a[_this.ids[i]] == 50){
						_this.trigger("change:mode:" + this.ids[i]);
					}
				}
			});
		}
	},

	a: {},
	ids: [],
	getProgress: function(id){
		if(this.a[id] == null){
			this.a[id] = 0;
			this.ids.push(id);
		}
		return this.a[id];
	},

	getMode: function(id){
		return this.getProgess() < 50;	
	}

});