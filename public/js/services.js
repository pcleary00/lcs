/*global define */

'use strict';

/* Services */

var lcsServices = angular.module('lcsServices', ['ngResource']);

lcsServices.factory('lcsService', ['$http', function($http) {

    var doRequest = function(setOfStrings, successCallback, failureCallback) {

        var docs = []
        for (var i=0; i < setOfStrings.length; i++ ) {
            if ( setOfStrings[i].value.length > 0 ) {
                docs.push({value: setOfStrings[i].value});
            }
        }
        var json = {
            setOfStrings: docs
        }

        $http({
            method: 'POST',
            url: '/lcs',
            data: json,
            headers: { 'Content-Type' : 'application/json' }
        }).success(successCallback).error(failureCallback);
    }
    return {
        findLongestCommonSubstrings: function(setOfStrings,successCallback, failureCallback) { return doRequest(setOfStrings, successCallback, failureCallback, 'findLongestCommonSubstrings'); }
    };
}]);
