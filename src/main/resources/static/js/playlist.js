angular.module('playlist', ['utils', 'ngDragDrop'])
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
            }
        )

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