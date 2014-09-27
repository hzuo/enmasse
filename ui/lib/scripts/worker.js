
function loadTask(){
	var req = new XMLHttpRequest();
	var url = '//10.144.27.103:9000/worker.js';
    req.open('GET', url, false);
    req.setRequestHeader('X-PINGOTHER', 'pingpong');
    req.setRequestHeader('Content-Type', 'application/javascript');
    req.send(); 
    return req.results;
}

// 	var task = {
// 		uid: "",
// 		code: "",
// 		type: "",
// 		data: [{key:0,value:5}]
// 	};

// 	return task;
// }

function reportResults(result){
	var req = new XMLHttpRequest();
	var url = '//10.144.27.103:9000/worker.js';
    req.open('post', url, false);
    req.setRequestHeader('X-PINGOTHER', 'pingpong');
    req.setRequestHeader('Content-Type', 'application/json');
    req.send(results); 
}

function executeTask(task){
	var outputCollector = {
		results: {
			uid: task.uid,
			attractorToken: self.token,
			type: task.type,
			output: []
		}
		collect: function(key, value){
			this.results.output.push({
				k: key,
				v: value
			});
		}
	}
	var f = eval(task.code);
	for (var i = 0; i < task.data.length; i++) {
		f(data[i].key, data[i].value, outputCollector);
	}
	return outputCollector.results;
}

function main(){
	while(true){
		reportResults(executeTask(loadTask()));
	}
}

self.onmessage = function(e) {
	self.token = e.data;
	main();
};