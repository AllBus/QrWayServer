package models.daos

import slick.jdbc.JdbcProfile

trait DBTableQuestionDefinitions {
	protected val profile: JdbcProfile
	import profile.api._

	case class Question(
		                   id:Long,
		                   group:Int,
		                   question:String,
		                   rightAnswer:Int,
		                   answers:String,
	                   )

	case class QuestionInfo(
		                       id:Long,
		                       questionId:Long,
		                       title:String,
		                       text:String,
		                       image:String,
	                       )

	case class QrQuestion(
		                     id:Int,
		                     qr:String,
		                     questionId:Long
	                     )

	case class QuestionGroup(
		                        id:Int,
		                        title:String,
		                        description:String,
		                        color:Int,
		                        questionNamePrefix:String
	                        )

	class Questions(tag: Tag) extends Table[Question](tag, Some("victorine"), "questions") {
		def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
		def group = column[Int]("group")
		def question = column[String]("question")
		def rightAnswer = column[Int]("rightAnswer")
		def answers = column[String]("answers")
		def * = (id, group, question, rightAnswer,answers) <> (Question.tupled, Question.unapply)
	}

	class QuestionInfos(tag: Tag) extends Table[QuestionInfo](tag, Some("victorine"), "question_infos") {
		def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
		def questionId = column[Long]("questionId")
		def title = column[String]("title")
		def text = column[String]("text")
		def image = column[String]("image")
		def * = (id, questionId, title, text,image) <> (QuestionInfo.tupled, QuestionInfo.unapply)
	}

	class QrQuestions(tag: Tag) extends Table[QrQuestion](tag, Some("victorine"), "question_infos") {
		def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
		def qr = column[String]("qr")
		def questionId = column[Long]("questionId")
		def * = (id, qr, questionId) <> (QrQuestion.tupled, QrQuestion.unapply)
	}

	class QuestionGroups(tag: Tag) extends Table[QuestionGroup](tag, Some("victorine"), "question_groups") {
		def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
		def title = column[String]("title")
		def description = column[String]("description")
		def color = column[Int]("color")
		def questionNamePrefix = column[String]("questionNamePrefix")
		def * = (id,  title, description,color, questionNamePrefix) <> (QuestionGroup.tupled, QuestionGroup.unapply)
	}

	// table query definitions
	val slickQuestions = TableQuery[Questions]
	val slickQuestionInfos = TableQuery[QuestionInfos]
	val slickQrQuestions = TableQuery[QrQuestions]
	val slickQuestionGroups = TableQuery[QuestionGroups]

}
