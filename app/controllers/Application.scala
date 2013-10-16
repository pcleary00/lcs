package controllers

import play.api.mvc._
import play.api.libs.json._
import services.{EmptySetException, Strings}
import scala.util.{Success, Failure}
import constants.Messages._


object Application extends Controller {

  case class RequestFailure(error: Int, reason: String, detail: String = "")
  object RequestFailure {

    implicit val requestFailureFormat = Json.format[RequestFailure]
  }

  case class LcsRequest(setOfStrings: Set[String])
  object LcsRequest {

    implicit val setOfStringsFormat = new Format[Set[String]] {

      def reads(json: JsValue): JsResult[Set[String]] = {

        val set = collection.mutable.HashSet[String]()
        (json \\ "value").foreach {
          jsonStr =>
            val str = jsonStr.as[String]
            if (set.contains(str)) return JsError(DUPLICATE_MESSAGE)
            else set.add(str)
        }
        JsSuccess(set.toSet)
      }

      def writes(set: Set[String]): JsValue = {

        Json.arr( for( str <- set ) yield Json.obj(("value", JsString(str))))
      }
    }

    implicit val lcsRequestFormat = Json.format[LcsRequest]
  }

  case class LcsResponse(lcm: List[String] = List.empty)

  object LcsResponse {
    implicit val lcsResponseJsonFormat = Json.format[LcsResponse]
  }

  def index = Action {
    Ok(views.html.index.render("Hello Play Framework"))
  }

  def findLcs = Action { request =>

      request.body.asJson.map {
        json =>
          json.validate[LcsRequest].map {
            case req: LcsRequest => {
              Strings.longestCommonSubstrings(req.setOfStrings) match {
                case Success(results) =>
                  Ok(Json.toJson(new LcsResponse(results)))
                case Failure(ese:EmptySetException) =>
                  fail(BAD_REQUEST, EMPTY_SET_MESSAGE)
                case Failure(ex) =>
                  fail(INTERNAL_SERVER_ERROR, "Encountered unexpected error while calculating the longest common substring" + ex.getMessage)
              }
            }
          }.recoverTotal {
            e => fail(BAD_REQUEST, "Detected error in request:" + JsError.toFlatJson(e))
          }
      }.getOrElse(fail(BAD_REQUEST, "Error in request body"))
  }

  def fail(status: Int, message: String, detail: String = ""): SimpleResult = {

    Application.Status(status)(Json.toJson(RequestFailure(status, message, detail)))
  }
}

