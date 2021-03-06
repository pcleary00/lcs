import controllers.Application.RequestFailure
import models.{LcsResponse, LcsRequest}
import org.specs2.mutable._

import play.api.libs.json.Json

import play.api.test._
import play.api.test.Helpers._

class ApplicationSpec extends Specification {

  "Finding the LCS" should {

    "Return 'cast' for strings 'comcast' and 'broadcaster'" in new WithApplication {

      val lcsRequest = new LcsRequest(Seq("comcast","broadcaster"))
      val js = Json.toJson(lcsRequest)

      val req = FakeRequest(POST, "/lcs").withJsonBody(js)
      val response = route(req).get

      status(response) must beEqualTo(OK)
      contentType(response) must beSome.which(_ == "application/json")

      val calculatedLcs = contentAsJson(response).as[LcsResponse]
      calculatedLcs.lcm(0) must beEqualTo("cast")
    }

    "Return the appropriate response if no body is provided" in new WithApplication {

      val emptyRequest = FakeRequest(POST,"/lcs").withHeaders(CONTENT_TYPE -> "application/json")
      val response = route(emptyRequest).get
      status(response) must beEqualTo(BAD_REQUEST)
    }

    "Return a duplicate error if the request is not a set" in new WithApplication {

      val json = Json.obj(
        "setOfStrings" -> Json.arr (
          Json.obj("value" -> "comcast"),
          Json.obj("value" -> "comcast")
        )
      )

      val nonSetRequest = FakeRequest(POST,"/lcs").withJsonBody(json)
      val response = route(nonSetRequest).get

      status(response) must beEqualTo(UNPROCESSABLE_ENTITY)

      val failure = contentAsJson(response).as[RequestFailure]
      failure.error must beEqualTo(UNPROCESSABLE_ENTITY)
      failure.reason must contain(constants.Messages.DUPLICATE_MESSAGE)
    }
  }
}
