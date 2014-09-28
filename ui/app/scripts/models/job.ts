Job = Backbone.Model.extend({

	defaults: {
		id: 1,
		name: "job",		
		map: "function map(k, v, outputCollector){}",
		reduce: "function reduce(k, vs, outputCollector){}",
		dataOrigin: "",
		createdAt: new Date()
	},

	tracker: null,

	initialize: function(){
		this.set({"createdAt": new Date(this.get("createdAt"))});
	},

	setProgressTracker: function(tracker){
		_this = this;
		this.tracker = tracker;
		tracker.bind("change:mode:"+this.get("id"), function(){_this.trigger("change:mode");});
		tracker.bind("change:progress:"+this.get("id"), function(){_this.trigger("change:progress");});
	},

	getMode: function(){
		if(this.tracker != null){
			return this.tracker.getMode(this.get("id"));
		}else{
			return 0;
		}
	},

	getProgress: function(){
		if(this.tracker != null){
			return this.tracker.getProgress(this.get("id"));
		}else{
			return 0;
		}
	}

});