var messagesRef = firebase.database().ref('Music');
var imageRef = firebase.database().ref('Thumbnail');
var uploadbtn = document.getElementById('uploadbtn');
var mfiles = document.getElementById('mfiles');
var uploader = document.getElementById('uploader');
var thumb_file = document.getElementById('thumb_file');
var thumb_uploader = document.getElementById('thumb_uploader');


function getInputVal(id){
	return document.getElementById(id).value;
}

function saveMessage(song, album, artist, year, genre, url){
	var songname = song.split('.')[0]; 
	//the song uploaded is stripped from its end extension to add to the database
	// only selected formats are allowed
	if(song.split('.')[1]=="wav" || song.split('.')[1]=="mp3" || song.split('.')[1]=="wma" ){
		var newMessageRef = messagesRef.child(songname+'|'+album);	// they are sent to a parent named songname|album 					
		newMessageRef.set({
			Song : songname,
			Album : album,
			Artist : artist,
			Year : year,
			Genre: genre,
			Url : url
		});
	}
	else if(song.split('.')[1]=="png" || song.split('.')[1]=="jpg" || song.split('.')[1]=="jpeg"){
		var newImageRef = imageRef.child(album);

		newImageRef.set({
			Album : album, 
			Url : url
		});
	}
}

//Handle waiting to upload each file using promise
function uploadMultipleFiles(File, album, artist, year, genre, i, flength) {
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
		    		// console.log('File available at', downloadURL);
		    				    		
               		var curUrl = downloadURL; 		//and then update database with metadata + download url               		
               		saveMessage(File.name.toLowerCase(), album, artist, year, genre, curUrl);
		    	});
		        uploader.value = 0;

		        if(i == flength){		//if it's the last file, give alert and reset form
		        	// + 1 for thumbnail
		        	alert("Upload is complete");
					document.getElementById('uploadform').reset();
		        }		   		
		        
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


	//Converted to lowercase to avoid redundancies
	album = album.toLowerCase();
	artist = artist.toLowerCase();
	genre = genre.toLowerCase();

	var flength = mfiles.files.length;  
	function uploadfiles(mfiles){
		// var flength = mfiles.files.length;  
		for (var i = 0; i < flength; i++) {
    		var File = mfiles.files[i];
			
			uploadMultipleFiles(File, album, artist, year, genre, i, flength-1);		//upload them one by one
			
    	} 	
	}

	uploadfiles(mfiles); 	//mfiles  = all the files taken together in a list
	
	//check if thumbnail file exists
	if(thumb_file.files[0] != null){
		uploadMultipleFiles(thumb_file.files[0], album, artist, year, genre, 0, flength);
	}
});





