package controllers

import java.nio.file.Paths

import play.api.cache._
import csv.QuestionReader
import dataconversion.DecodeProtobuf._
import javax.inject.Inject
import models.services.QuestionService
import play.api.mvc.{AbstractController, ControllerComponents}
import qrway2.victorine.{LoadQuestionRequest, LoadQuestionResponse}

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

class QuestionController @Inject()(components: ControllerComponents,
                                   cache: AsyncCacheApi,
                                   questionService:QuestionService
                                  )
                                  (implicit ec: ExecutionContext) extends AbstractController(components)  {

	implicit def controller=this

	def sendCsv() = Action.async(parse.multipartFormData) { request =>
		request.body.file("file") .map { picture =>
			// only get the last part of the filename
			// otherwise someone can send a path like ../../home/foo/bar.txt to write to other files on the system
			val filename    = Paths.get(picture.filename).getFileName
			val fileSize    = picture.fileSize
			val contentType = picture.contentType
			val res  = QuestionReader.readCsv(picture.ref)

			questionService.saveCsv(res.head).map {x ⇒
				//	picture.ref.copyTo(Paths.get(s"/tmp/picture/$filename"), replace = true)
				Ok(s"File uploaded $filename, $fileSize $contentType \n ${res.map(_.toProtoString).mkString("\n")}")
			}
		}
			.getOrElse {
				Future(		Ok("File error"))
			}
	}

	def readQuestion(qrCode:String) = Action.async{
		cache.getOrElseUpdate[LoadQuestionResponse]("QuestionController.readQuestion", 5.minutes) {
			questionService.readQuestion(qrCode)
		}.map(x ⇒ Ok(x))
	}



	def readQuestionProto = Action.async(parseProto(LoadQuestionRequest)){ request ⇒
		cache.getOrElseUpdate[LoadQuestionResponse]("QuestionController.readQuestionProto", 5.minutes) {
			questionService.readQuestion(request.body.qrLink)
		}.map(x ⇒ Ok(x))
	}

}
