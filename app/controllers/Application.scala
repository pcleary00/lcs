package controllers

import play.api.mvc._
import play.api.libs.json._
import services.{EmptySetException, Strings}
import scala.util.{Success, Failure}
import constants.Messages._
import models.{LcsResponse, LcsRequest}
import play.api.Routes


object Application extends Controller {

  case class RequestFailure(error: Int, reason: String, detail: String = "")
  object RequestFailure {

    implicit val requestFailureFormat = Json.format[RequestFailure]
  }

  // Finds the longest common substrings in a set of documents
  def findLcs = Action(parse.json) { request =>

    request.body.validate[LcsRequest].map {
      case req: LcsRequest => {
        Strings.findLongestCommonSubstrings(req.setOfStrings) match {

          case Success(results) => Ok(Json.toJson(new LcsResponse(results)))
          case Failure(ese:EmptySetException) => fail(BAD_REQUEST, EMPTY_SET_MESSAGE)
          case Failure(ex) => fail(INTERNAL_SERVER_ERROR, "Encountered unexpected error while calculating the longest common substring" + ex.getMessage)
        }
      }
    }.recoverTotal {
      e => fail(BAD_REQUEST, "Detected error in request:" + JsError.toFlatJson(e))
    }
  }

  // Convenience method to ensure that all failures are returned to the client in a consistent format
  private def fail(status: Int, message: String, detail: String = ""): SimpleResult = {

    Application.Status(status)(Json.toJson(RequestFailure(status, message, detail)))
  }
}

