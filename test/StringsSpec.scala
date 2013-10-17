import org.specs2.mutable.Specification
import services.Strings

class StringsSpec extends Specification {

  "Finding the LCS of 'comcast' and 'broadcaster'" should {

    val results = Strings.findLongestCommonSubstrings(Set("comcast", "broadcaster"))
    "return 'cast'" in {

      results must beSuccessfulTry
      results.get.size must beGreaterThan(0)
      results.get(0) must beEqualTo("cast")
    }
  }
  "Finding the LCS that has multiple substrings of the same length 'xfinity' and 'fintipity'" should {

    val results = Strings.findLongestCommonSubstrings(Set("xfinity", "fintipity"))
    "return 2 results" in {

      results must beSuccessfulTry
      results.get.size must beEqualTo(2)
    }
    "return sorted having 'fin' before 'ity'" in {

      results.get(0) must beEqualTo("fin")
      results.get(1) must beEqualTo("ity")
    }
  }
}
