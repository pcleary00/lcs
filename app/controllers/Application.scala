package controllers

import play.api.mvc._
import play.api.libs.json._
import services.{EmptySetException, Strings}
import scala.util.{Success, Failure}
import constants.Messages._
import models.{LcsResponse, LcsRequest}


object Application extends Controller {

  case class RequestFailure(error: Int, reason: String, detail: String = "")

  object RequestFailure {

    implicit val requestFailureFormat = Json.format[RequestFailure]
  }

  // Finds the longest common substrings in a set of documents
  def findLcs = Action {
    request =>

      request.body.asJson.map {
        json => json.validate[LcsRequest].map {
          case req: LcsRequest => {

            val set = req.setOfStrings.toSet

            // If the set is not of the same size, then we dropped a duplicate
            if (set.size != req.setOfStrings.size) {
              fail(UNPROCESSABLE_ENTITY, DUPLICATE_MESSAGE)
            }
            else {
              Strings.findLongestCommonSubstrings(set) match {

                case Success(results) => Ok(Json.toJson(new LcsResponse(results)))
                case Failure(ese: EmptySetException) => fail(UNPROCESSABLE_ENTITY, EMPTY_SET_MESSAGE)
                case Failure(ex) => fail(INTERNAL_SERVER_ERROR, "Encountered unexpected error while calculating the longest common substring" + ex.getMessage)
              }
            }
          }
        }.recoverTotal {
          e => fail(BAD_REQUEST, "Unable to read request", e.toString)
        }
      }.getOrElse(fail(BAD_REQUEST, "Unable to parse request"))
  }

  // Convenience method to ensure that all failures are returned to the client in a consistent format
  private def fail(status: Int, message: String, detail: String = ""): SimpleResult = {

    Application.Status(status)(Json.toJson(RequestFailure(status, message, detail)))
  }
}

