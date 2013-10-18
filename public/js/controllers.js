/*global define */

'use strict';

var lcsControllers = angular.module('lcsControllers',[]);

lcsControllers.controller('LcsController',['$scope', '$log', 'lcsService',

    function($scope, $log, lcsService) {

        $scope.setOfStrings = [{value:"sample"}, {value:"sample2"}, {value:"sample3"}, {value:"sample4"}];
        $scope.hasError = false;

        $scope.findLcs = function() {

            $scope.errorData = {};
            $scope.lcsResults = {};
            lcsService.findLongestCommonSubstrings($scope.setOfStrings,
                function onSuccess(data) {
                    $scope.lcsResults = data;
                },
                function onFailure(data){
                    $scope.errorData = data; $scope.hasError = true; $scope.lcsResults = null;
                }
            );
        }
    }
]);
