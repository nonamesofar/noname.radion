//duplicate of playlist.js since I realized I might f it up and don't want to loose that one
angular.module('radioplaylist', ['utils', 'ngDragDrop'])
    .controller('BuildPlaylist', function ($scope, $http) {
        //do I really want to clear it all the time?
        $scope.audioPlaylist = [];
        $http.get('http://localhost:8080/getMusicCollection').
            then (function (response) {
            var i = -1;
                $scope.prefabPlaylist = response.data.map(function (trackInfo){
                    i++;
                    return { src: "http://localhost:8080/listen?id=" + i, type: 'audio/ogg', artist: trackInfo.artistName, title: trackInfo.trackName};
                });

            $scope.index = 0;
            //push 2 tracks in the playlist to avoid later buffering
            $scope.audioPlaylist.push(angular.copy($scope.prefabPlaylist[$scope.index]));
            $scope.mediaPlayer.load(false);
            //$scope.audioPlaylist.push(angular.copy($scope.prefabPlaylist[$scope.index+1]));

              // Setup the playlist display.
            var playlist = $scope.prefabPlaylist;
            playlist.forEach(function(song) {
                var div = document.createElement('div');
                div.className = 'list-song';
                div.innerHTML = song.title;
                 list.appendChild(div);
              });
            }
        )

        $scope.progress = function() {
            var self = $scope.mediaPlayer;

            // Determine our current seek position.
            //mediaPlayer.currentTime*100/mediaPlayer.duration
            var seek = $scope.mediaPlayer.currentTime || 0;
            var duration = $scope.mediaPlayer.duration;
            timer.innerHTML = self.$formatTime(Math.round(seek));
            progress.style.width = (((seek / duration) * 100) || 0) + '%';
            if($scope.mediaPlayer.playing){
                requestAnimationFrame($scope.progress);
            }

        }

        $scope.play = function() {

            var self = $scope.mediaPlayer;

            $scope.mediaPlayer.playPause();

            track.innerHTML = $scope.prefabPlaylist[$scope.index].title;

            // Display the duration.
            duration.innerHTML = self.$formatTime(Math.round($scope.mediaPlayer.duration));

                // Show the pause button.
                if (!$scope.mediaPlayer.playing) {
                  playBtn.style.display = 'none';
                  pauseBtn.style.display = 'block';
                } else {
                   playBtn.style.display = 'block';
                   pauseBtn.style.display = 'none';
                }

        }

        $scope.next = function() {
            $scope.index++;
            if($scope.audioPlaylist.length <= $scope.index){
                //push more tracks
                $scope.audioPlaylist.push(angular.copy($scope.prefabPlaylist[$scope.index]));
                $scope.mediaPlayer.load($scope.prefabPlaylist[$scope.index]);
                //$scope.audioPlaylist.push(angular.copy($scope.prefabPlaylist[$scope.index+1]));
            }

            track.innerHTML = $scope.prefabPlaylist[$scope.index].title;
            $scope.play();

        }

         $scope.togglePlaylist = function() {
            var self = this;
            var display = (playlist.style.display === 'block') ? 'none' : 'block';

            setTimeout(function() {
              playlist.style.display = display;
            }, (display === 'block') ? 0 : 500);
            playlist.className = (display === 'block') ? 'fadein' : 'fadeout';
         }


        $scope.addSong = function (audioElement) {
            $scope.audioPlaylist.push(angular.copy(audioElement));
        };

        $scope.removeSong = function (index) {
            $scope.audioPlaylist.splice(index, 1);
        };

        $scope.dropSong = function (audioElement, index) {
            $scope.audioPlaylist.splice(index, 0, angular.copy(audioElement));
        };
        }
    );