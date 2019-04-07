var messagesRef = firebase.database().ref('Music');
var uploadbtn = document.getElementById('uploadbtn');
var mfiles = document.getElementById('mfiles');
var uploader = document.getElementById('uploader');
var thumbnail = document.getElementById('tn');


function getInputVal(id){
	return document.getElementById(id).value;
}

function saveMessage(song,album, artist, year, genre){
	var newMessageRef = messagesRef.push();
	if(song.split('.')[1]=="wav" || song.split('.')[1]=="mp3" || song.split('.')[1]=="wma" ){
	newMessageRef.set({
		Song : song.split('.')[0],
		Album : album,
		Artist : artist,
		Year : year,
		Genre: genre
	});
	}
}

//Handle waiting to upload each file using promise
function uploadMultipleFiles(File, album) {
    return new Promise(function (resolve, reject) {


	    if(File.name.split('.')[1]=="wav" || File.name.split('.')[1]=="mp3" || File.name.split('.')[1]=="wma" ){
			var storageRef = firebase.storage().ref( "Songs/"+ File.name.toLowerCase());
		}
		else if(File.name.split('.')[1]=="png" || File.name.split('.')[1]=="jpeg" || File.name.split('.')[1]=="jpg") {
			var thname = album + "." +File.name.split('.')[1];
			var storageRef = firebase.storage().ref( "Thumbnail/"+ thname);
		}
		    
	    //Upload file
	    var task = storageRef.put(File);

	    //Update progress bar
	    task.on('state_changed', function progress(snapshot){
	        var percentage = snapshot.bytesTransferred / snapshot.totalBytes * 100;
	        uploader.value = percentage;
	    },
	    function error(err){
	    	alert("Update Form Again");
	    },
	    function complete(){
	        var downloadURL = task.snapshot.downloadURL;
	        uploader.value = 0;

	    }
	    );
	});
}

uploadbtn.addEventListener("click", function(e){
	e.preventDefault();

	//Get values
	var album = getInputVal('album');
	var artist = getInputVal('artist');
	var year = getInputVal('year');
	var genre = getInputVal('genre');

	album = album.toLowerCase();
	artist = artist.toLowerCase();
	genre = genre.toLowerCase();

	function uploadfiles(mfiles){
		for (var i = 0; i < mfiles.files.length; i++) {
    		var File = mfiles.files[i];
			uploadMultipleFiles(File, album);
			// File.name = File.name.toLowerCase();
			saveMessage(File.name.toLowerCase(), album, artist, year, genre);

    	} 	
	}

	uploadfiles(mfiles);

	document.getElementById('uploadform').reset();
});




