angular
.module('utils', ['mediaPlayer'])
    .config(function ($interpolateProvider) {
      $interpolateProvider.startSymbol('[[');
      $interpolateProvider.endSymbol(']]');
    })
    .run(function ($rootScope) {
          // helper function to seek to a percentage
          $rootScope.seekPercentage = function ($event) {
            var percentage = ($event.offsetX / $event.target.offsetWidth);
            if (percentage <= 1) {
              return percentage;
            } else {
              return 0;
            }
          };
        }
    )
    .controller('getTrackInfo', function($scope, $http) {
            $http.get('http://localhost:8080/info').
                then(function(response) {
                    $scope.trackInfo=response.data;
                }
                )
        }
    );
