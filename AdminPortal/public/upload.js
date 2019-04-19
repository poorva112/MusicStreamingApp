var messagesRef = firebase.database().ref('Music');
var uploadbtn = document.getElementById('uploadbtn');
var mfiles = document.getElementById('mfiles');
var uploader = document.getElementById('uploader');
var thumbnail = document.getElementById('tn');
// var curUrl = "ok";

function getInputVal(id){
	return document.getElementById(id).value;
}

function saveMessage(song,album, artist, year, genre, url){
	var newMessageRef = messagesRef.push();						//key missing
	if(song.split('.')[1]=="wav" || song.split('.')[1]=="mp3" || song.split('.')[1]=="wma" ){
		newMessageRef.set({
			Song : song.split('.')[0],
			Album : album,
			Artist : artist,
			Year : year,
			Genre: genre,
			Url : url
		});
	}
}

//Handle waiting to upload each file using promise
function uploadMultipleFiles(File, album, artist, year, genre) {
    return new Promise(function (resolve, reject) {

    	//Send audio file to 'Songs' reference
    	//Send images to 'Thumbnail' reference
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
		    	
		    	// get the uploaded url when upload is complete
		    	task.snapshot.ref.getDownloadURL().then(function(downloadURL){
		    		console.log('File available at', downloadURL);
		    				    		
               		var curUrl = downloadURL; 
               		saveMessage(File.name.toLowerCase(), album, artist, year, genre, curUrl);
		    	});
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
			
			uploadMultipleFiles(File, album, artist, year, genre);	
			// alert(curUrl);	
			// saveMessage(File.name.toLowerCase(), album, artist, year, genre, curUrl);


			if(i == mfiles.files.length-1){
				alert("Upload is complete");
				document.getElementById('uploadform').reset();
			}
    	} 	
	}

	uploadfiles(mfiles);

	
});





