package models

import play.api.libs.json._
import constants.Messages._
import play.api.libs.json.JsSuccess

/**
 * A web request for the longest common substring
 * @param setOfStrings The set of strings to be evaluated
 * */
case class LcsRequest(setOfStrings: Seq[String])

/**
 * Stores the json serialization in the companion object so the implicits are automatically picked up
 */
object LcsRequest {

  // Intelligent serialization that will convert the JSON model into a set of strings
  implicit val setOfStringsFormat = new Format[Seq[String]] {

     /**
     * {
        "setOfStrings" : [
        { "value" : "comcast" },
        { "value" : "broadcaster" }
        ]
      }
     */
    def reads(json: JsValue): JsResult[Seq[String]] = {

      val seq = new collection.mutable.ArrayBuffer[String]()
      (json \\ "value").foreach {
        jsonStr =>
          val str = jsonStr.as[String]
          seq.append(str)
      }
      JsSuccess(seq)
    }

    def writes(strings: Seq[String]): JsValue = {

      Json.arr(for (str <- strings) yield Json.obj(("value", JsString(str))))
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
  implicit val lcsResponseJsonFormat = new Format[LcsResponse] {

    def reads(json: JsValue): JsResult[LcsResponse] = {

      val seq = new collection.mutable.ArrayBuffer[String]()
      (json \\ "value").foreach {
        jsonStr =>
          val str = jsonStr.as[String]
          seq.append(str)
      }
      JsSuccess(new LcsResponse(seq.toList))
    }

    def writes(lcs:LcsResponse):JsValue = {
      Json.obj("lcm" ->
        (for( str <- lcs.lcm) yield Json.obj("value" -> str ))
      )
    }
  }
}
