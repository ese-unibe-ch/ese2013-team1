
// Use Parse.Cloud.define to define as many cloud functions as you want.
// For example:
Parse.Cloud.define("hello", function(request, response) {
  response.success("Hello world!");
});

Parse.Cloud.afterSave ("Favorite",function (request) {
	Parse.Push.send ({
		//Selecting the already existing Push Channel
		channels: ["JGAchtPush"], //This has to be the name of your push channel!!
        data: {
            //Selecting the Key inside the Class
            alert: request.object.get ("AusfallInfo")
        }
    }, {
        success: function () {
		
        },
        error: function (error) {
            throw "Got and error" + error.code + " : " + error.message;
        }
    });
});