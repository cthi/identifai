var express = require('express');
var app = express();
var http = require('http').Server(app);
var io = require('socket.io')(http);
var moment = require('moment');

var bodyParser = require('body-parser');
app.use(bodyParser.json()); // support json encoded bodies
app.use(bodyParser.urlencoded({ extended: true })); // support encoded bodies

app.get('/', function (req, res) {
    res.send('Hello World!');
});

app.use('/home', express.static('index.html'));
app.use(express.static(__dirname + '/'));

http.listen(3000, function () {
    console.log('Example app listening on port 3000!');
});

app.post('/identifai', function(req, res) {
	console.log(req.body.pictureLink);
	console.log(moment().format());
	io.emit("New Picture", [req.body.pictureLink, moment().format(), req.body.message]);
	res.send("Message received.");
});

io.on('connection', function(socket){
  console.log('a user connected');
  socket.on('disconnect', function(){
    console.log('user disconnected');
  });
});	