package services

import com.googlecode.concurrenttrees.radix.node.concrete.DefaultCharSequenceNodeFactory
import com.googlecode.concurrenttrees.common.CharSequences
import com.googlecode.concurrenttrees.radix.ConcurrentRadixTree
import com.googlecode.concurrenttrees.radix.node.Node
import scala.collection.mutable
import java.util
import scala.util.Try
import java.lang.Exception

import constants.Messages._

/**
 * Thrown if a request is made and the set provided is empty
 */
class EmptySetException extends Exception(EMPTY_SET_MESSAGE)

/**
 * Provides advanced string functions
 */
object Strings {

  /**
   * Finds the longest common substrings for a set of documents as Strings.<p>
   *   The set of strings must have at least one entry, or else the call will Fail
   * @param documents The documents to be evaulated.  Must contain at least one entry.
   * @return a Try containing an alphabetically ordered list of strings.
   *         If there is an unexpected exception or if the set is empty, this will result in a Failure
   */
  def findLongestCommonSubstrings(documents: Set[String]): Try[List[String]] = {

    Try {
      if ( documents.isEmpty ) throw new EmptySetException

      val tree = new SetBasedSuffixTree(documents)
      tree.findLongestCommonSubstrings()
    }
  }
}

/**
 * A Suffix Tree containing multiple documents. There is only a single tree, ALL strings are added to the same tree.
 * <p>
 *   Each node in the suffix tree has a value, in this case it is a HashSet of all documents that contain that node/suffix.
 *   It is important to conceptualize this suffix tree as a <strong>combined</strong> tree, containing suffixes for all documents
 * @param originalDocuments A set of Strings that are used to build the suffix tree
 */
class SetBasedSuffixTree(originalDocuments: Set[String]) extends ConcurrentRadixTree[java.util.Set[String]](new DefaultCharSequenceNodeFactory()) {

  import scala.collection.JavaConversions._

  // Build the tree with the documents provided
  for (document <- originalDocuments) {

    addSuffixesToRadixTree(document)
  }

  /** Important step, add all of the suffixes for the string to the tree
    * We look up the node that ends in the suffix (contains the suffix)
    * If it exists, we add the current document/string to the HashSet value at that node
    * If it does not exist, then the suffix does not yet exist in the tree.
    * and we create a new empty Set for the node.
    */
  private def addSuffixesToRadixTree(keyAsString: String) {

    val suffixes = CharSequences.generateSuffixes(keyAsString)
    for (suffix <- suffixes) {
      var originalKeyRefs = this.getValueForExactKey(suffix)
      if (originalKeyRefs == null) {
        originalKeyRefs = new util.HashSet[String]()
        this.put(suffix, originalKeyRefs)
      }
      originalKeyRefs += keyAsString
    }
  }

  /**
   * Finds the longest common substrings for the documents provided in the constructor.
   * <p>
   *   We go down the tree, as we go we identify those nodes (suffixes) that are in ALL documents.
   *   If it is, then we add the suffix for that key to the set of strings we are considering
   *   Once the traversal is complete, we only return the <strong>longest</strong> substrings, and return them ordered
   * @return An Alphabetically Sorted List of strings that are common across all documents
   */
  def findLongestCommonSubstrings(): List[String] = {

    val root = this.getNode()
    val substrings = new mutable.HashSet[String]()
    var longestCommonSubstringSoFar: CharSequence = ""
    var longestCommonSubstringSoFarLength = 0

    val nodes = iterableAsScalaIterable(lazyTraverseDescendants("", root))
    for (nodeKeyPair <- nodes) {
      if (nodeKeyPair.key.length >= longestCommonSubstringSoFarLength && matchesAllDocuments(nodeKeyPair.key, nodeKeyPair.node)) {
        longestCommonSubstringSoFarLength = nodeKeyPair.key.length
        longestCommonSubstringSoFar = nodeKeyPair.key

        // Only consider substrings with length longer than 1, otherwise the results could be enormous
        if (longestCommonSubstringSoFarLength > 1) {
          substrings.add(CharSequences.toString(longestCommonSubstringSoFar))
        }
      }
    }
    substrings.filter(_.size == longestCommonSubstringSoFarLength).toList.sorted
  }

  /**
   * Returns true if the given node and its descendants collectively reference all original documents added to
   * the solver.
   * <p/>
   * This method will traverse the entire sub-tree until it has encountered all of the original documents. If
   * it encounters all of the original documents early, before exhausting all nodes, returns early.
   *
   * @param startKey The key associated with the start node (concatenation of edges from root leading to it)
   * @param startNode The root of the sub-tree to traverse
   * @return True if the given node and its descendants collectively reference all original documents added to
   *         the solver, false if the sub-tree does not reference all documents added to the solver
   */
  private def matchesAllDocuments(startKey: CharSequence, startNode: Node): Boolean = {

    val documentsEncounteredSoFar = new java.util.HashSet[String]()
    for (nodeKeyPair <- lazyTraverseDescendants(startKey, startNode)) {

      if (nodeKeyPair.node.getValue != null) {
        documentsEncounteredSoFar.addAll(nodeKeyPair.node.getValue.asInstanceOf[java.util.Set[String]])
        if (documentsEncounteredSoFar.size() == originalDocuments.size) return true
      }
    }
    false
  }
}