urlBase = "//ec2-54-209-233-14.compute-1.amazonaws.com:9000";

function loadTask() {
    var req = new XMLHttpRequest();
    req.open('GET', self.urlBase + '/worker.js', false);
    req.setRequestHeader('X-PINGOTHER', 'pingpong');
    req.setRequestHeader('Content-Type', 'application/javascript');
    req.send();
    return req.results;
}

function reportResults(result) {
    var req = new XMLHttpRequest();
    req.open('POST', self.urlBase + '/worker.js', false);
    req.setRequestHeader('X-PINGOTHER', 'pingpong');
    req.setRequestHeader('Content-Type', 'application/json');
    req.send(results);
}

function executeTask(task) {
    var outputCollector = {
        results: {
            uid: task.uid,
            attractorToken: self.token,
            type: task.type,
            output: []
        },
        collect: function (key, value) {
            this.results.output.push({
                k: key,
                v: value
            });
        }
    };
    var f = eval(task.code);
    for (var i = 0; i < task.data.length; i++) {
        f(data[i].key, data[i].value, outputCollector);
    }
    return outputCollector.results;
}

function main() {
    for (var i = 0; i < 10; i++) {
        console.log("greg");
    }
    //while (true) {
        //reportResults(executeTask(loadTask()));
        //console.log("greg");
    //}
}

self.onmessage = function (e) {
    self.token = e.data;
    main();
};
