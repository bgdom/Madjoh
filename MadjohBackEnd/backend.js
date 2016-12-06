var mysql = require('mysql');
var https = require('https');
var express = require('express');

// parameters for the MySql connection
var connection = mysql.createConnection({
  host  : 'localhost',
  user  : 'root',
  password  : '',
  charset : 'utf8mb4',
  database : 'MiniProjet'
});
//yandex.com key : trnsl.1.1.20161204T171617Z.4ad4cb89051d9fcd.cf4b65361a7c7ac2c4624a927b324e2f17d19377

connection.connect();// connect to the bdd
var app = express();
app.get('/word/:l', function(req, res1) { // if there is an http get request for the 'mywebsite/word/level' route
    var statement = 'SELECT Mot from Mot ORDER BY RAND() LIMIT 1';
    if(!isNaN(req.params.l)){
        if(req.params.l >= 5){
          console.log('level = difficult');
            statement = 'SELECT Mot from Mot WHERE LENGTH(Mot) >= 8 ORDER BY RAND() LIMIT 1';
        }else if(req.params.l < 0){
          console.log('level = easy');
            statement = 'SELECT Mot from Mot WHERE LENGTH(Mot) <= 6 ORDER BY RAND() LIMIT 1';
        }else{
            statement = 'SELECT Mot from Mot ORDER BY RAND() LIMIT 1';
            console.log('level = normal');
        }
    }

    res1.setHeader('Content-Type', 'text/plain');
    connection.query(statement, function(err, rows, fields){ // query for a random french word
        if(err)
            throw err;
        var mot = rows[0].Mot;
        /* translate part */
        var options = { // options for the https request we plan to make
          host: 'translate.yandex.net',
          port: 443,
          // I used the Yandex.translate API => http://translate.yandex.com/.
          path: '/api/v1.5/tr.json/translate?key=trnsl.1.1.20161204T171617Z.4ad4cb89051d9fcd.cf4b65361a7c7ac2c4624a927b324e2f17d19377&text='+mot+'&lang=fr-en',
          method: 'GET',
        };

        var req = https.request(options, function(res) { // send the request
          console.log(res.statusCode);
            if(res.statusCode == 200){
                res.on('data', function(d) {// catch the https request's reponse and send the http response with french and english word
                    // make the JSON Object
                    var obj = new Object();
                    obj.fr = mot;
                    // Powered by Yandex.Translate => http://translate.yandex.com/.
                    obj.en = JSON.parse(d).text[0];
                    console.log(JSON.stringify(obj)) // to see it
                    res1.status(200).send(JSON.stringify(obj));
                    console.log('https send');
                });
            }else {
                res1.status(404).send("A problem occur");
            }
        });

        req.end();

        req.on('error', function(e) { // send an http error's request if an error occure
            console.log('https error');
        });

    });
});

app.use(function(req, res) {// handle 404 error
      res.status(404).send('404: File Not Found');
});

app.on('close', function (arguments) {
    connection.end(); // close the database connection
});

app.listen(8080); //listen to the 8080 port
