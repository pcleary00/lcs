/*global define */

'use strict';

var lcsControllers = angular.module('lcsControllers',[]);

lcsControllers.controller('LcsController',['$scope', 'lcsService',

    function($scope, lcsService) {

        $scope.setOfStrings = [{value:"sample"}, {value:"sample2"}, {value:"sample3"}, {value:"sample4"}];
        $scope.name = 'your name here'

        $scope.findLcs = function() {

            $scope.lcm = lcsService.findLongestCommonSubstrings($scope.setOfStrings)
        }
    }
]);
