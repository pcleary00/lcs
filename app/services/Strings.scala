package services

import com.googlecode.concurrenttrees.radix.node.concrete.DefaultCharSequenceNodeFactory
import com.googlecode.concurrenttrees.common.CharSequences
import com.googlecode.concurrenttrees.radix.node.NodeFactory
import com.googlecode.concurrenttrees.radix.ConcurrentRadixTree
import com.googlecode.concurrenttrees.radix.node.Node
import com.googlecode.concurrenttrees.radix.ConcurrentRadixTree.NodeKeyPair
import scala.collection.mutable
import java.util
import scala.util.Try
import java.lang.Exception

import constants.Messages._


class EmptySetException extends Exception(EMPTY_SET_MESSAGE)

object Strings {

  def longestCommonSubstrings(documents:Set[String]):Try[List[String]] = {

    Try {
      new LCSMultipleResultSolver(new DefaultCharSequenceNodeFactory(),documents).getLongestCommonSubstrings()
    }
  }
}

class LCSMultipleResultSolver(nodeFactory: NodeFactory, val originalDocuments:Set[String]) {

  import scala.collection.JavaConversions._

  private val suffixTree = new ConcurrentSuffixTreeImpl[java.util.Set[String]](nodeFactory)

  for(document <- originalDocuments) {

    suffixTree.acquireWriteLock()
    try {
      // Key was added to set, now add to tree...
      addSuffixesToRadixTree(document)
    } catch {
      case e:Throwable => e.printStackTrace()
    }
    finally {
      suffixTree.releaseWriteLock()
    }
  }

  def addSuffixesToRadixTree(keyAsString:String) {

    val suffixes = CharSequences.generateSuffixes(keyAsString)
    for (suffix <- suffixes) {
     var originalKeyRefs = suffixTree.getValueForExactKey(suffix)
      if (originalKeyRefs == null) {
        originalKeyRefs = new util.HashSet[String]()
        suffixTree.put(suffix, originalKeyRefs)
      }
      originalKeyRefs += keyAsString
    }
  }

  def getLongestCommonSubstrings():List[String] = {
    suffixTree.getLongestCommonSubstring().toList.sorted
  }

  class ConcurrentSuffixTreeImpl[V](nodeFactory:NodeFactory) extends ConcurrentRadixTree[V](nodeFactory) {

    override def acquireWriteLock() = {
      super.acquireWriteLock()
    }


    override def releaseWriteLock() = {
      super.releaseWriteLock()
    }

    // Override to make accessible to outer class...
    override def lazyTraverseDescendants(startKey:CharSequence, startNode:Node):java.lang.Iterable[NodeKeyPair] = {
      super.lazyTraverseDescendants(startKey, startNode)
    }

    def getLongestCommonSubstring():mutable.Set[String] = {

      val root = suffixTree.getNode()
      val substrings = new mutable.HashSet[String]()
      var longestCommonSubstringSoFar:CharSequence = ""
      var longestCommonSubstringSoFarLength = 0

      val nodes = iterableAsScalaIterable(lazyTraverseDescendants("", root))
      for (nodeKeyPair <- nodes) {
        if (nodeKeyPair.key.length >= longestCommonSubstringSoFarLength && subTreeReferencesAllOriginalDocuments(nodeKeyPair.key, nodeKeyPair.node)) {
          longestCommonSubstringSoFarLength = nodeKeyPair.key.length
          longestCommonSubstringSoFar = nodeKeyPair.key

          // Only consider substrings with length longer than 1
          if ( longestCommonSubstringSoFarLength > 1 ) {
            substrings.add(CharSequences.toString(longestCommonSubstringSoFar))
          }
        }
      }
      substrings.filter(_.size == longestCommonSubstringSoFarLength)
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
    private def subTreeReferencesAllOriginalDocuments(startKey: CharSequence, startNode: Node): Boolean = {
      import scala.collection.JavaConversions._

      val documentsEncounteredSoFar = new java.util.HashSet[String]()
      val result: Array[Boolean] = Array[Boolean](false)
      for (nodeKeyPair <- lazyTraverseDescendants(startKey, startNode)) {

        @SuppressWarnings(Array("unchecked"))
        val documentsReferencedByThisNode:java.util.Set[String] = nodeKeyPair.node.getValue.asInstanceOf[java.util.Set[String]]
        if (documentsReferencedByThisNode != null) {
          documentsEncounteredSoFar.addAll(documentsReferencedByThisNode)
          if (documentsEncounteredSoFar.size() == originalDocuments.size) {
            result(0) = true
            return true
          }
        }
      }
      result(0)
    }
  }
}