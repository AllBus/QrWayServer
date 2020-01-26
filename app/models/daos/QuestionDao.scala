package models.daos

import qrway2.victorine
import qrway2.victorine.{FullQuestionGroup, Question, QuestionData, QuestionGroup, QuestionInfo}

import scala.concurrent.Future


trait QuestionDao {

	def writeQuestions(questions: Seq[victorine.Question], infos: Seq[(Long , victorine.QuestionInfo)], group:Int): Future[Unit]

	def writeHeaders(headers:Seq[QuestionGroup]):Future[Int]

	def writeHeader(header: victorine.QuestionGroup):Future[Int]

	def writeFullQuestion(fullQuestion:FullQuestionGroup):Future[Unit]
}
