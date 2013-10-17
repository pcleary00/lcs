/*global define */

'use strict';

/* Services */

var lcsServices = angular.module('lcsServices', ['ngResource']);

lcsServices.factory('lcsService', ['$http', function($http) {

    var doRequest = function(setOfStrings) {

        var docs = []
        for (var i=0; i < setOfStrings.length; i++ ) {
            docs.push({value: setOfStrings[i].value})
        }
        var json = {
            setOfStrings: docs
        }

        return $http({
            method: 'POST',
            url: '/lcs',
            data: json,
            headers: { 'Content-Type' : 'application/json' }
        });
    }
    return {
        findLongestCommonSubstrings: function(setOfStrings) { return doRequest(setOfStrings, 'findLongestCommonSubstrings'); }
    };
}]);
