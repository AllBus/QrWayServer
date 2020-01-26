package models.services

import javax.inject.Inject
import models.daos.{QuestionDao, QuestionDaoImpl, UserDAO}
import qrway2.victorine.FullQuestionGroup

import scala.concurrent.{ExecutionContext, Future}

class QuestionService  @Inject()(questionDAO: QuestionDaoImpl)(implicit ec: ExecutionContext)  {

	def saveCsv(fullQuestion:FullQuestionGroup): Future[Unit] = {
		questionDAO.writeFullQuestion(fullQuestion)
	}
}
