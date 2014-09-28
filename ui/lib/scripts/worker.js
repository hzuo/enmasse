self.urlBase = "//ec2-54-209-233-14.compute-1.amazonaws.com:9000";

function loadTask() {
    var req = new XMLHttpRequest();
    req.open('GET', self.urlBase + '/work', false);
    req.setRequestHeader('X-PINGOTHER', 'pingpong');
    req.setRequestHeader('Content-Type', 'application/json');
    req.send();
    return req.results;
}

function reportResults(result) {
    var req = new XMLHttpRequest();
    req.open('POST', self.urlBase + '/work', false);
    req.setRequestHeader('X-PINGOTHER', 'pingpong');
    req.setRequestHeader('Content-Type', 'application/json');
    req.send(result);
}

function executeTask(task) {
    var oc = function(){
        return {
            emits: [],
            collect: function (key, value) {
                this.emits.push({
                    k: key,
                    v: value
                });
            }
        };
    };
    var f = eval(task.fn);
    var out = [];
    for (var i = 0; i < task.input.length; i++) {
        var outputCollector = oc();
        f(task.input[i].k, task.input[i].v, outputCollector);
        out.push(
            {
                id: task.input[i].id,
                emits: outputCollector.emits
            }
        );
    }

    return {
        mode: task.mode,
        attractorId: self.token,
        output: out
    };
}

function main() {
    while (true) {
        reportResults(executeTask(loadTask()));
    }
}

self.onmessage = function (e) {
    self.token = e.data;
    main();
};
