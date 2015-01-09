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
		var _this = this;
        this.ids.forEach(function(id){
            $.ajax({
                url: "/progress?id=" + id,
                method: "GET",
                success: function(num){
                    var old = _this.a[id];
                    _this.a[id] = Math.ceil(Number(num) * 100);
                    if(old != _this.a[id]) {
                        _this.trigger("change:progress:" + id);
                    }
                    if(_this.a[id] == 50){
                        _this.trigger("change:mode:" + id);
                    }
                }
            });
        });
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
		return this.getProgress(id) < 50;
	}

});