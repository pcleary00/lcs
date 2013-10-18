'use strict';

/* http://docs.angularjs.org/guide/dev_guide.e2e-testing */

describe('Lcs App', function() {

  describe('Finding Lcs', function() {

    var finder = null
    beforeEach(function() {
      browser().navigateTo('/index.html');
      finder = jQuery('#findButton')
    });


    it('should return \'case\' when searching for \'comcast\' and \'broadcast\'', function() {

      input('.lcsEntry li:nth-child(1)').enter('comcast');
      input('.lcsEntry li:nth-child(2)').enter('broadcaster');

      expect(repeater('.linenums li').count()).toBe(1);
    });

  });
});
