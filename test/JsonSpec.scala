import models.LcsRequest
import org.specs2.mutable.Specification
import play.api.libs.json.Json

class JsonSpec extends Specification {


  val goodJson =
    """
      |{
      |  "setOfStrings" : [ {
      |    "value" : "comcast"
      |  }, {
      |    "value" : "broadcaster"
      |  } ]
      |}
    """.stripMargin

  val dupJson =
    """
      |{
      |  "setOfStrings" : [ {
      |    "value" : "comcast"
      |  }, {
      |    "value" : "comcast"
      |  } ]
      |}
    """.stripMargin

  "Good Json" should {

    "be able to be read into a set of strings" in {

      val sos = Json.parse(goodJson).as[LcsRequest]
      sos.setOfStrings.size must beEqualTo(2)
      sos.setOfStrings must contain("comcast") and contain("broadcaster")
    }
  }
  "Duplicate json" should  {

    "throw a duplicate exception" in {

      val resultedInFailure = Json.parse(dupJson).validate[LcsRequest].fold( invalid =(e => true), valid = (res => false))
      resultedInFailure must beTrue
    }
  }
}
