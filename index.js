var express = require('express');
var app = express();

var bodyParser = require('body-parser');
app.use(bodyParser.json()); // support json encoded bodies
app.use(bodyParser.urlencoded({ extended: true })); // support encoded bodies

app.get('/', function (req, res) {
    res.send('Hello World!');
});

app.use('/home', express.static('index.html'));
app.use(express.static(__dirname + '/'));

app.listen(3000, function () {
    console.log('Example app listening on port 3000!');
});

app.post('/identifai', function(req, res) {
	console.log(req.body.hello);
	res.send("dick");
});