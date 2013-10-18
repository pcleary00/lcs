# Find the Longest Common Substring(s) for a Set of Strings.

This is a little application that allows the user to enter 1-4 strings to determine the longest common substring(s)  
in the set provided.

The application was built using the [Play Framework](http://www.playframework.com/), with the front-end developed using [AngularJS](http://angularjs.org/).

The Gentleman that you will see on the site is **Esko Ukkonen**, who developed an algorithm for constructing suffix trees in linear time.  For this application I used  [Google Code Concurrent Trees](https://code.google.com/p/concurrent-trees/), which is a Java implementation of Suffix and Radix Trees.  Concurrent Trees provided an example on solving the _Longest Common Substring_ problem.  I adapted the method to Scala, cleaned it up a lot, and added the ability to return more than one substring.  The Google Code example only returned the last substring found, this application will return all of the longest common substrings in a set of strings of the same length.

## Rules to Play (no pun intended)
1. You cannot have a duplicate entry in your submission.  If you do, you will get an error message indicating that you cannot have duplicate entries in the set of strings you are evaluating.
2. You must enter at least one string in your submission.  If you do not enter at least one string, you will get an error indicating that at least one string must be provided.

## Code Overview
