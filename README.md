## Find the Longest Common Substring(s) for a Set of Strings.

This is a little application that allows the user to enter 1-4 strings to determine the longest common substring(s)  
in the set provided.

The application was built using the [Play Framework](http://www.playframework.com/), with the front-end developed using [AngularJS](http://angularjs.org/).

The Gentleman that you will see on the site is **Esko Ukkonen**, who developed an algorithm for constructing suffix trees in linear time.  For this application I used  [Google Code Concurrent Trees](https://code.google.com/p/concurrent-trees/), which is a Java implementation of Suffix and Radix Trees.  Concurrent Trees provided an example on solving the _Longest Common Substring_ problem.  I adapted the method to Scala, cleaned it up a lot, and added the ability to return more than one substring.  The Google Code example only returned the last substring found, this application will return all of the longest common substrings in a set of strings of the same length.

### (Attempted) explanation of the algorithm
> The premise of the algorithm is that we create a single, unified Suffix Tree that contains **ALL** of the suffixes from each of the strings being evaluated.  Each suffix (edge) between a parent and all child nodes is unique (as per the rules of suffix trees).  Also, each node in the tree represents a concatenation of all nodes from the root to the current node.  Finally, each suffix (ending node) that is in the tree is capable of having a value associated with it.  The algorithm here works by having each suffix contain a logical "pointer" to all strings that contain that suffix.

#### Suffix Tree Creation
* for each string being evaluated
  * for each suffix in the string
    *  if the suffix exists in the tree, add a reference to the string in a Set attached to the suffix
    *  else no other strings loaded so far have this suffix, so create a new set and add the current string to it

#### Longest Common Substring 
* starting at the root, inspect each child node
  * if the child node references all of the strings loaded into the tree (i.e. the value associated with node is a set referencing all strings that were loaded into the string
  * the suffix for the child node is contained in all strings, consider it for the longest common substring


### Rules to Play (no pun intended)
1. You cannot have a duplicate entry in your submission.  If you do, you will get an error message indicating that you cannot have duplicate entries in the set of strings you are evaluating.
2. You must enter at least one string in your submission.  If you do not enter at least one string, you will get an error indicating that at least one string must be provided.
3. The application will only return common substrings that are greater than one character.

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
* **/test** - contains different flavors of tests
 * **/test/ApplicationSpec** - tests the Web API itself, sending various JSON requests and testing the responses
 * **/test/JsonSpec** - tests some JSON serialization
 * **/test/StringsSpec** - tests the functionality of the Strings class with a few scenarios

#### Playing with the code (_I can go all day_)
I use IntelliJ as my editor, I am not a big fan of the Scala IDE (yet), I find IntelliJ a much nicer experience when working with Scala.  I simply use the IntelliJ Community Edition with the JetBrains Scala Plugin, that's all.
> As of this writing, the SBT plug-in for IntelliJ was at version 1.55 and was rather unstable.  As such, I recommend generating the idea project through Play.

1. Download and install the Play Framework here: http://www.playframework.com/download
 On that link, you will see instructions to try and use the Typesafe Activator.  Admittedly, the Activator is pretty cool, but I rather enjoy just using Play by itself, so I use the *Classic Distribution* in these instructions.
 Once you install Play, just make sure it is available on your path.
2. Clone this repository (just making sure you are following along)
3. Go into the lcs directory, and as follows:

```bash
$ cd ~/workspace/lcs
$ play gen-idea
```

This will create the IntelliJ IDEA project for you.  You can then open the project and have at it!

#### Running the application
Running the application, simply:
```bash
$ cd ~/workspace/lcs
$ play run
```

This will start play up on the default port 9000, you can access the application at http://localhost:9000

### Ruminations
1. I was rather unsatisfied with Play's Request / Response handling to support the API. I am likely to head over to [Spray](http://spray.io/) for a tour after this.  The application definitely did not require Play, I chose it based on familiarity.
2. AngularJS makes me like JavaScript :)

 

 


