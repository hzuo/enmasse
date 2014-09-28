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

function executeTask(taskSet) {
    var oc = function () {
        return {
            emits: [],
            emit: function (key, value) {
                this.emits.push({
                    k: key,
                    v: value
                });
            }
        };
    };
    var f = eval(taskSet.fn);
    var out = [];
    for (var i = 0; i < taskSet.tasks.length; i++) {
        var outputCollector = oc();
        f(taskSet.tasks[i].k, taskSet.tasks[i].v, outputCollector);
        out.push(
            {
                preimageKey: taskSet.tasks[i].key,
                emits: outputCollector.emits
            }
        );
    }

    return {
        attractorId: self.token,
        jobId: taskSet.jobId,
        mode: taskSet.mode,
        output: out
    };
}

function main() {
    while (true) {
        var option = loadTask();
        if (option.length == 0) {
            setTimeout(self.main, 10000);
            break;
        }
        for (var i = 0; i < option.length; i++) {
            reportResults(executeTask(option[i]));
        }
    }
}

self.onmessage = function (e) {
    self.token = e.data;
    main();
};
