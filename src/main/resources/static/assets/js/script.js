
var allTracks = [],		// An array for all the files loaded in the track
	playlist = [], 		// An array for the current playlist
	temporarySearchPlaylist = [],	// A helper array for when we are searching
	i = 0, 				// The number of the current track
	shuffle = false,	// Shuffle flag
	repeat = 0,			// Repeat flag
	lastPlayed = [],	// Array for last played (used when shuffling songs)
	timer = 0;			// An interval for the track's current time.

/*-------------------
	Audio player.
 ------------------*/

var searchInput = $('#searchBox');

soundManager.setup({
  url: '/path/to/swf-files/',
  onready: function() {
    soundManager.setVolume(50);
//    $('#container').removeClass('disabled');
//    var index = getNextTrack();
//    loadTrack(index);
//
//    playTrack(0);
  },
  onfinish: function(){
	$( "#next-button" ).trigger( "click" );
  },

  ontimeout: function() {
    // Hrmm, SM2 could not start. Missing SWF? Flash blocked? Show an error, etc.?
  }
});

var mySound;
// Read file and play it.
// Takes one parameter - the index of the track we want to play.
function showTrackArt(index){

    if(playlist[i]){
        // Set cover art.

        if(playlist[i].picture == 'assets/img/default.png'){
            $('#cover-art-big').css("background", "");
        }  else{
            $('#cover-art-big').css("background-image", "url("+ playlist[i].picture +")").css("background-repeat", "no-repeat").css("background-position", "center");
        }

        $('#cover-art-small').attr('src', playlist[i].picture);

        playlist[i].playing = true;

    }
}


/*---------------------
	Player controls
----------------------*/

// Pressing the 'next' button
// Plays next track in playlist, or if shuffle is on random track.
$('#next-button').on('click', function () {

	if (!shuffle) {
		i++;
		if (i > playlist.length - 1) {
		    var index = getNextTrack();
			loadTrack(index);
		}
	}
	else {
		if (playlist.length > 1) {
			var temp = i;
			while (i == temp) {
				i = Math.floor(Math.random() * playlist.length);
			}
		}
	}

	if(playlist[i]) {
		playTrack(i);
	}

});

// Pressing the 'previous' button.
// If shuffle is off plays previous song from playlist
// If shuffle is on takes song from lastPlayed to keep order.
$('#previous-button').on('click', function () {

	if(!shuffle){
		if(i==0){
			i=playlist.length-1;
		}
		else{
			i--;
		}
	}
	else{
		lastPlayed.pop();
		i = lastPlayed.pop();
	}

	if(i==undefined || i<0){
		i = 0;
	}

	playTrack(i);

});

$('#play-button').on('click', function(){
    mySound.play();

});

$('#pause-button').on('click', function () {
    mySound.pause();

});

$('#stop-button').on('click', function(){

});

var saveVolume;
// Turn volume on and off.
$('#volume-button').on('click', function(){
	var that = $(this);

	if(that.hasClass('active')){
		that.removeClass('active');
		that.attr('title', 'Volume On');
		//hack because wavesurfer is weird for some reason
		saveVolume = savedVolume / 100;
		updateVolume(0);
	}
	else{
		that.addClass('active');
		that.attr('title', 'Volume Off');
		updateVolume(0, saveVolume);
	}
});

// repeat = 0 Repeat is off - when the playlist reaches it's end it will stop
// repeat = 1 Repeat all - when the playlist reaches it's end it will start from begining
// repeat = 2 Repeat Current - repeat track
$('#repeat-button').on('click', function(){

	var that = $(this);

	if(repeat==0){
		that.addClass('active');
		that.attr('title', 'Repeat All');
		repeat = 1;
	}

	else if(repeat==1){
		that.find('span').show();
		that.attr('title', 'Repeat Current');
		repeat = 2;
	}

	else if(repeat==2){
		that.find('span').hide();
		that.removeClass('active');
		that.attr('title', 'Repeat Off');
		repeat = 0;
	}

});


/*----------------------
	Playlist navigation
----------------------*/

// Opening and closing the playlist.
$('#track-details').on('click', function () {
	var expandBar = $('#expand-bar');

	if(expandBar.hasClass('hidden')){
		expandBar.removeClass('hidden');
		$(this).attr('title', 'Hide Playlist');
	}
	else{
		expandBar.addClass('hidden');
		$(this).attr('title', 'Show Playlist');
	}
});

$('#playlist').on('click', function (e) {

	// Get the index of the clicked track.

	var target = $(e.target),
		index = target.closest('.track').data('index');

	if(index!=undefined){

		// Selecting Tracks
		if(!target.hasClass('remove-track')){


			// If there was a search made, create a new playlist from the search results.
			if(temporarySearchPlaylist.length){
				playlist = temporarySearchPlaylist.slice(0);
				temporarySearchPlaylist = [];
				lastPlayed = [];
			}

			// Play the newly selected track and set it as currently playing (i).
			i = index;

			playTrack(i);

		}
		// Deleting Tracks
		else{

			var position,
				track;

			// If a track is removed while searching.
			if(temporarySearchPlaylist.length) {
				track = temporarySearchPlaylist[index];
			}
			// If a track is removed from normal playback.
			else {
				track = playlist[index];
			}

			// Remove from allTracks
			position = allTracks.indexOf(track);

			if(position != -1) {
				allTracks.splice(position, 1);
			}

			// Remove from playlist.
			position = playlist.indexOf(track);

			if(position != -1) {
				playlist.splice(position, 1);
			}

			// If we have deleted the currently playing track play next / first
			if (track.playing) {
				if (i >= playlist.length) {
					i = 0;
				}

				playTrack(i);
			}

			// Trigger search to render the new playlist.
			searchInput.trigger('input');

			if(!playlist.length){
				// Playlist is empty - try to generate new playlist from the allTracks array.
				if(allTracks.length){
					playlist = allTracks.slice(0);
					renderTrackList(playlist);
					i = 0;
					playTrack(i);
				}
				// Playlist is empty, allTracks is empty - deactivate player.
				else{

					clearInterval(timer);
					$('#cover-art-big').css("background", "");
					$('#cover-art-small').attr('src', 'assets/img/default.png');
					$('#expand-bar').addClass('hidden');
					$('#track-desc').html('There are no tracks loaded in the player.');
					$('#current').text('-');
					$('#total').text('-');
					$('#container').addClass('disabled');

					startPlayerWhenReady()
				}
			}

		}
	}

});

// Close playlist when clicked on cover art.
$('#container').on('click', function (e) {
	if(e.target==this){
		$('#expand-bar').addClass('hidden');
	}
});



/*----------------------
	Search functionality
-----------------------*/

var clearSearchDelay;

searchInput.on('keydown', function (e) {

	if(e.keyCode == 27){
		$(this).val('');
		$(this).trigger('input');
	}
	else if(e.keyCode == 13) {

		e.preventDefault();

		if ($(this).val().trim().length) {

			var searchString = $(this).val().trim();
			searchTracks(searchString);
			clearTimeout(clearSearchDelay);

		}
	}

});

searchInput.on('input', function(e){
	e.preventDefault();
	var searchStr = $(this).val().trim();

	clearTimeout(clearSearchDelay);

	if(!searchStr.length) {
		searchInput.val('');

		searchTracks();
	}
	else {

		clearSearchDelay = setTimeout(function() {
			if (searchStr.length) {
				searchTracks(searchStr);
			}
		},700);
	}
});

function searchTracks(query){

	query = query || "";
	query = query.toLowerCase();

	temporarySearchPlaylist = allTracks.slice(0);

	if(query.length){
		temporarySearchPlaylist = temporarySearchPlaylist.filter(function (tr) {
			if(tr.artist.toLowerCase().indexOf(query) != -1 || tr.title.toLowerCase().indexOf(query) != -1 || tr.album.toLowerCase().indexOf(query) != -1){
				return tr;
			}
		});
	}

	// Render the newly created search results list.
	renderTrackList(temporarySearchPlaylist);

}

$('.muted').click(function () {
    audio.muted = !audio.muted;
    return false;
});


/*-------------------
 	Volume bar
--------------------*/
//volume bar event
var volumeDrag = false;

$('.volume').on('mousedown', function (e) {
    volumeDrag = true;
    $('.sound').removeClass('muted');
    updateVolume(e.pageX);
});
$(document).on('mouseup', function (e) {
    if (volumeDrag) {
        volumeDrag = false;
        updateVolume(e.pageX);
    }
});
$(document).on('mousemove', function (e) {
    if (volumeDrag) {
        updateVolume(e.pageX);
    }
});
//we start with max volume woop
var savedVolume = 100;
var updateVolume = function (x, vol) {
    var volume = $('.volume');
    var percentage;
    //if only volume have specificed
    //then direct update volume
    if (vol) {
        percentage = vol * 100;
    } else {
        var position = x - volume.offset().left;
        percentage = 100 * position / volume.width();
    }

    savedVolume = percentage;

    if (percentage > 100) {
        percentage = 100;
    }
    if (percentage < 0) {
        percentage = 0;
    }

    //update volume bar and video volume
    $('.volumeBar').css('width', percentage + '%');


    var volValue = percentage;
    soundManager.setVolume(volValue);

    //change sound icon based on volume
    if (volValue == 0) {
        $("#volume-button i").removeClass('fa fa-volume-up').addClass('fa fa-volume-off');
    } else {
        $("#volume-button i").removeClass('fa fa-volume-off').addClass('fa fa-volume-up');
    }

};


/*-------------------
 	Helper Functions
--------------------*/

//Automatically start playlist on file load.
function startPlayerWhenReady(){


	var interval = setInterval(function () {
		if(playlist[0]){
			playTrack(0);
			$('#container').removeClass('disabled');
			clearInterval(interval);
		}
		else {
		    var i = getNextTrack();
            loadTrack(i);
		}
	},200);
}

function getNextTrack(){
    var service="http://localhost:8080/nextTrack";
    var index;
    jQuery.ajax({
        url: service,
        success: function(html) {
            index = html;
        },
        async:false
    });
    return index;
}

function loadTrack(index){
    var service = "http://localhost:8080/trackInfo?id="+index;
    var song;
    //async until I figure out how I can properyl do this :(
    jQuery.ajax({
        url: service,
        success: function(html) {
            song = html;
        },
        async:false
    });
    allTracks.push(song);
    playlist.push(song);
    $('#list').append($(returnTrackHTML(song, playlist.length-1)));
}


// Creates html for a track in the playlist.
function returnTrackHTML(song, index){

	var html = '<li class="track';

	if(song.playing){
		html+= ' active'
	}

	html+='" data-index="'+ index +'">' +
	'<div>' +
	'<span class="overlay"><i class="fa fa-play"></i></span>' +
	'<img src="' + song.picture + '"/>' +
	'</div>' +
	'<div>'	+
	'<p class="title">' + song.title + '</p>' +
	'<p class="artist">' + song.artist + '</p>' +
	'<span title="Remove Track From Player" class="remove-track">×</span>' +
	'</div>' +
	'</li>';

	return html;
}


// Write the contents of a playlist into the playlist tab in the html.
function renderTrackList(list){
	$('.track').remove();

	var html = list.map(function (tr,index) {
		return returnTrackHTML(tr,index);
	}).join('');

	$('#list').append($(html));
}


// Format time in minutes:seconds
function formatTime(time){
	time = Math.round(time);

	var minutes = Math.floor(time / 60),
		seconds = time - minutes * 60;

	seconds = seconds < 10 ? '0' + seconds : seconds;

	return minutes + ":" + seconds;
}


// Wavesurfer responsiveness
$(window).on('resize', function(){
	if($('#wave').is(":visible")) {


	}
});