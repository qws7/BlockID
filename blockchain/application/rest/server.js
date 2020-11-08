const express = require('express');
const app = express();

var path = require('path');
var sdk = require('./sdk');

const PORT = 8080;
const HOST = 'localhost';

app.get('/api/queryAllCars', function (req, res) {
    let args = [];
    sdk.send(false, 'queryAllCars', args, res);
});
app.use(express.static(path.join(__dirname, './client')));

app.listen(PORT, HOST);
console.log(`Running on http://${HOST}:${PORT}`);