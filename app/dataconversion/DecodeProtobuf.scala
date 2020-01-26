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

//	def parseProto[A <: com.trueaccord.scalapb.GeneratedMessage with com.trueaccord.scalapb.Message[A]](gm: GeneratedMessageCompanion[A]):BodyParser[A] = parse.using{ request ⇒
//		parse.raw.map(x ⇒ x.asBytes().map {bytes ⇒ gm.parseFrom(bytes.toArray)
//		}.getOrElse(throw new ParseProtoException("no parse proto")))
//	}

	def parseProto[A <:GeneratedMessage with Message[A]]
	(gm: GeneratedMessageCompanion[A])(implicit controller:BaseControllerHelpers, executor:ExecutionContext):BodyParser[A] = controller.parse.using{ request ⇒
		controller.parse.raw.map(x ⇒ x.asBytes().map {bytes ⇒ gm.parseFrom(bytes.toArray)
		}.getOrElse(throw new ParseProtoException("no parse proto")))
	}
	//	def raw[A<:GeneratedMessage](memoryThreshold: Int = 10000000, maxLength: Long = 10000000): BodyParser[A] =
//
//
//		BodyParser("raw, memoryThreshold=" + memoryThreshold) { request =>
//			import play.core.Execution.Implicits.trampoline
//			enforceMaxLength(request, maxLength, Accumulator.strict[ByteString, RawBuffer]({ maybeStrictBytes =>
//				Future.successful(RawBuffer(memoryThreshold, temporaryFileCreator, maybeStrictBytes.getOrElse(ByteString.empty)))
//			}, {
//				val buffer = RawBuffer(memoryThreshold, temporaryFileCreator)
//				val sink = Sink.fold[RawBuffer, ByteString](buffer) { (bf, bs) => bf.push(bs); bf }
//				sink.mapMaterializedValue { future =>
//					future andThen { case _ => buffer.close() }
//				}
//			}).map(buffer => Right(buffer)))
//		}
//
//	def parseProto[A<:GeneratedMessage](): BodyParser[A] ={
//		BodyParsers.parse.raw().
//	}

//	def apply[P <: Message, A](bodyParser: BodyParser[A], proto: Class[P])(block: P => Request[A] => Result) =
//		new DecodeProtobuf[P, A] {
//
//		def parser = bodyParser
//		def apply(req: Request[A]) = Future({
//			req.body.asInstanceOf[AnyContent].asRaw.flatMap { raw =>
//				raw.asBytes().map { bytes =>
//					try {
//						val parseFrom = proto.getMethod("parseFrom", classOf[Array[Byte]])
//						val message: P = parseFrom.invoke(proto, bytes).asInstanceOf[P]
//						block(message)(req)
//					}catch{
//						case e:NoSuchMethodException => Results.BadRequest
//					}
//				}
//			}
//		} getOrElse { Results.InternalServerError })
//	}
//	def apply[P <: Message](proto: Class[P])(block: P => Request[AnyContent] => Result): Action[AnyContent] = {
//		DecodeProtobuf(BodyParsers.parse.anyContent, proto)(block)
//	}
}