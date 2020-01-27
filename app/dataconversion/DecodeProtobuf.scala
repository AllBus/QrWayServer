package dataconversion


import akka.util.ByteString
import play.api.http.Writeable
import play.api.mvc._
import scalapb.{GeneratedMessage, GeneratedMessageCompanion, Message}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by Kos on 14.12.2017.
  */
trait DecodeProtobuf[P <: GeneratedMessage, A] extends Action[A]

class ParseProtoException(msg:String) extends Exception(msg)

object DecodeProtobuf extends BodyParserUtils {

	implicit def writeableOf_Protobuf: Writeable[GeneratedMessage] = {
		Writeable(a => ByteString(a.toByteArray),Option("application/x-protobuf"))
	}

	def base64decode(code: String) = new sun.misc.BASE64Decoder().decodeBuffer(code)


	def parseProto[A <:GeneratedMessage with Message[A]]
	(gm: GeneratedMessageCompanion[A])(implicit controller:BaseControllerHelpers, executor:ExecutionContext):BodyParser[A] = controller.parse.using{ request ⇒
		controller.parse.raw.map(x ⇒ x.asBytes().map {bytes ⇒ gm.parseFrom(bytes.toArray)
		}.getOrElse(throw new ParseProtoException("no parse proto")))
	}

}