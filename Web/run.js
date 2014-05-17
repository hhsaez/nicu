var express = require('express'),
	request = require('request'),
	robotIp = process.argv[2];

if (!robotIp) {
	console.log("Must provide robot ip");
	return;
}

var app = express();

app.get('/robot/*', function(req, res) {
	var url = 'http://' + robotIp + req.url;
  	req.pipe(request(url)).pipe(res);
});

app.put('/robot/*', function(req, res) {
	var url = 'http://' + robotIp + req.url;
  	req.pipe(request(url)).pipe(res);
});

app.get('/', function(req, res) {
	res.sendfile(__dirname + '/index.html');
});

app.get('/*', function(req, res) {
	res.sendfile(__dirname + req.url);
});

app.listen(8888);

console.log("Static file server running at\n  => http://localhost:8888/\nRobot IP: " + robotIp + "\nCTRL + C to shutdown");
