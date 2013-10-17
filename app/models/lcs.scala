package models

import play.api.libs.json._
import constants.Messages._
import play.api.libs.json.JsSuccess

/**
 * A web request for the longest common substring
 * @param setOfStrings The set of strings to be evaluated
 * */
case class LcsRequest(setOfStrings: Set[String])

/**
 * Stores the json serialization in the companion object so the implicits are automatically picked up
 */
object LcsRequest {

  // Intelligent serialization that will convert the JSON model into a set of strings
  implicit val setOfStringsFormat = new Format[Set[String]] {

     /**
     * {
        "setOfStrings" : [
        { "value" : "comcast" },
        { "value" : "broadcaster" }
        ]
      }
     */
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

      Json.arr(for (str <- set) yield Json.obj(("value", JsString(str))))
    }
  }

  implicit val lcsRequestFormat = Json.format[LcsRequest]
}

/**
 * Stores the result from a longest common substring request
 * @param lcm The longest common sub-strings that were found in the request.
 *            There can be more than one if multiple sub-strings have the same length and
 *            are common across the strings in the request
 */
case class LcsResponse(lcm: List[String] = List.empty)

object LcsResponse {
  implicit val lcsResponseJsonFormat = Json.format[LcsResponse]
}
