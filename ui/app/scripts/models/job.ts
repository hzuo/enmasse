Job = Backbone.Model.extend({

	defaults: {
		id: 1,
		name: "job",		
		map: "function map(k, v, outputCollector){}",
		reduce: "function reduce(k, vs, outputCollector){}",
		dataOrigin: "",
		createdAt: new Date()
	}

});