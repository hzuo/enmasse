Job = Backbone.Model.extend({

	defaults: {
		id: 1,
		name: "job",		
		map: "function map(k, v, outputCollector){}",
		reduce: "function reduce(k, vs, outputCollector){}",
		dataOrigin: "",
		createdAt: new Date()
	},

	initialize: function(){
		this.set({"createdAt": new Date(this.get("createdAt"))});
		var _this = this;
		tracker.bind("change:mode:"+this.get("id"), function(){_this.trigger("change:mode");});
		tracker.bind("change:progress:"+this.get("id"), function(){_this.trigger("change:progress");});
	},

	setProgressTracker: function(tracker){
		var _this = this;
		this.tracker = tracker;
	},

	getMode: function(){
		if(tracker != null){
			return tracker.getMode(this.get("id"));
		}else{
			return 0;
		}
	},

	getProgress: function(){
		if (tracker != null) {
			var progress = tracker.getProgress(this.get("id"));
			return progress;
		} else {
			return 0;
		}
	},

	download: function(){
		var hiddenIFrameID = 'hiddenDownloader',
        iframe = document.getElementById(hiddenIFrameID);
	    if (iframe === null) {
	        iframe = document.createElement('iframe');
	        iframe.id = hiddenIFrameID;
	        iframe.style.display = 'none';
	        document.body.appendChild(iframe);
	    }
	    iframe.src = "/export?id=" + this.get('id');
	}
},{
    
    create : function(name, dataLoc, map, reduce){
        $.ajax({
        	method: "POST",
        	url: "/jobs",
        	dataType: "json",
        	data: JSON.stringify({name: name, dataOrigin: dataLoc, map: map, reduce: reduce }),
        	success: function(data){
        		var j = new Job();
        		j.set(data);
        		jobs.add(j);
                console.log(j);
                router.navigate("detail/" + this.model.get("id"), {trigger:true});
        	}
        });
    }
    
});