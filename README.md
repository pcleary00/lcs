## Find the Longest Common Substring(s) for a Set of Strings.

This is a little application that allows the user to enter 1-4 strings to determine the longest common substring(s)  
in the set provided.

The application was built using the [Play Framework](http://www.playframework.com/), with the front-end developed using [AngularJS](http://angularjs.org/).

The Gentleman that you will see on the site is **Esko Ukkonen**, who developed an algorithm for constructing suffix trees in linear time.  For this application I used  [Google Code Concurrent Trees](https://code.google.com/p/concurrent-trees/), which is a Java implementation of Suffix and Radix Trees.  Concurrent Trees provided an example on solving the _Longest Common Substring_ problem.  I adapted the method to Scala, cleaned it up a lot, and added the ability to return more than one substring.  The Google Code example only returned the last substring found, this application will return all of the longest common substrings in a set of strings of the same length.

### Rules to Play (no pun intended)
1. You cannot have a duplicate entry in your submission.  If you do, you will get an error message indicating that you cannot have duplicate entries in the set of strings you are evaluating.
2. You must enter at least one string in your submission.  If you do not enter at least one string, you will get an error indicating that at least one string must be provided.

### Code Overview
#### AngularJS

The AngularJS code resides in the **public** directory of the code.  Following the AngularJS seed project, the following files are used:
* **js/app.js** - boot straps the Angular app
* **js/controllers.js** - contains the only controller I use, tied to the single page
* **js/services.js** - defines the service call to the LCS service

> **Note:**I did not use directives or filters in this excercise, as those are blank.

#### Play Application

The Play Application is setup like a very typical Play application.  There is nothing surprising or outlandish here.  Here is where you can find what:
* **/app** - contains the crux of the application code
 * **app/constants** - simply contains message constants
 * **/app/controllers** - here you will find a single Application controller, which has by-in-large a single method _findLcs_
 * **/app/models** - contains the communication model classes for handling requests, as well as the JSON serialization
 * **/app/services** - contains our lone service, **_Strings_** that has the method for finding the longest common substrings
* **/conf** - contains the application configuration
 * **/conf/routes** - this is the Play _magic_ that routes incoming requests to the Controller

 
 

