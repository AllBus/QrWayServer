package controllers

import java.nio.file.Paths

import csv.QuestionReader
import javax.inject.Inject
import models.services.QuestionService
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent.{ExecutionContext, Future}

class QuestionController @Inject()(components: ControllerComponents,
                                   questionService:QuestionService
                                  )
                                  (implicit ec: ExecutionContext) extends AbstractController(components)  {

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

}
