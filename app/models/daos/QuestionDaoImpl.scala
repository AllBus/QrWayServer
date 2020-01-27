package models.daos

import java.time.ZonedDateTime

import javax.inject.Inject
import models.{HasSchemaDescription, User}
import play.api.db.slick.DatabaseConfigProvider
import qrway2.victorine
import qrway2.victorine.{FullQuestionGroup, GroupHeader, LoadQuestionResponse, QuestionData, QuestionInfoWithId}

import scala.concurrent.{ExecutionContext, Future}

class QuestionDaoImpl @Inject()(
	                               protected val dbConfigProvider: DatabaseConfigProvider,
                               )(implicit ec: ExecutionContext)
	extends QuestionDao with DAOSlick {

	import profile.api._

	override def writeQuestions(questions: Seq[victorine.Question], infos: Seq[(Long, victorine.QuestionInfo)], group: Int): Future[Unit] = {

		val q = questions.map { x ⇒
			Question(
				x.id,
				group,
				x.question,
				x.rightAnswerFlag,
				x.answers.mkString(";"))
		}

		val info = infos.map { case (id, x) ⇒
			QuestionInfo(
				0,
				id,
				x.title,
				x.text,
				x.image
			)
		}

		val qrs = questions.map { x ⇒
			QrQuestion(0, s"qrcode: ${x.question}", x.id)
		}

		val actions = DBIO.seq(
			slickQuestions ++= q,
			slickQuestionInfos ++= info,
			slickQrQuestions ++= qrs
		).transactionally

		db.run(actions)
	}

	override def writeFullQuestion(fullQuestion: FullQuestionGroup): Future[Unit] = {
		val header = fullQuestion.getHeader
		val h = QuestionGroup(
			header.id,
			header.getHeader.title,
			header.getHeader.description,
			header.getHeader.color,
			header.questionNamePrefix)


		val q = fullQuestion.questions.map { x ⇒
			Question(
				x.id,
				header.id,
				x.question,
				x.rightAnswerFlag,
				x.answers.mkString(";"))
		}

		val info = fullQuestion.infos.map { case QuestionInfoWithId(id, Some(x)) ⇒
			QuestionInfo(
				0,
				id,
				x.title,
				x.text,
				x.image
			)
		}

		val qrs = fullQuestion.infos.map { case QuestionInfoWithId(id, Some(x)) ⇒
			QrQuestion(0, x.qr, id)
		}


		val qs = slickQuestions.filter(_.group === header.id)
		val dins = slickQuestionInfos.filter(_.questionId in qs.map(_.id))
		val dqrs = slickQrQuestions.filter(_.questionId in qs.map(_.id))

		val actions = DBIO.seq(
			slickQuestionGroups.filter(_.id === header.id).delete,
			dins.delete,
			dqrs.delete,
			qs.delete,
			slickQuestionGroups.forceInsert(h),
			slickQuestions.forceInsertAll(q),
			slickQuestionInfos ++= info,
			slickQrQuestions ++= qrs
		).transactionally


		db.run(actions)

	}

	override def writeHeader(header: victorine.QuestionGroup): Future[Int] = {
		val action = slickQuestionGroups += QuestionGroup(
			header.id,
			header.getHeader.title,
			header.getHeader.description,
			header.getHeader.color,
			header.questionNamePrefix)

		db.run(action)
	}


	override def writeHeaders(headers: Seq[victorine.QuestionGroup]): Future[Int] = {

		val items = headers.map { h ⇒
			QuestionGroup(
				h.id,
				h.getHeader.title,
				h.getHeader.description,
				h.getHeader.color,
				h.questionNamePrefix)
		}
		val actions: DBIO[Option[Int]] = slickQuestionGroups ++= items
		db.run(actions).map(_.getOrElse(-1))
	}

	override def readTable(qrCode: String): Future[LoadQuestionResponse] = {

		val questionAction = slickQuestions.filter(_.id in slickQrQuestions.filter(_.qr === qrCode).take(1).map(_.questionId)).result.headOption

		db.run(questionAction).flatMap {
			case None ⇒ 	Future(LoadQuestionResponse(None,None))
			case Some(q) ⇒

			val gid = q.group

			val group = slickQuestionGroups.filter(_.id === gid).result.headOption
			val questionsIds = slickQuestions.filter(_.group === gid).map(_.id).result
			val questionInfo = slickQuestionInfos.filter(_.questionId === q.id).result.headOption

			val r = for {
				groupf ← db.run(group)
				idsf ← db.run(questionsIds)
				questionInfof ← db.run(questionInfo)
			} yield {
				val qg = groupf.map { g ⇒
					victorine.QuestionGroup(
						g.id,
						Option(GroupHeader(g.title, g.color, g.description)),
						g.questionNamePrefix,
						idsf
					)
				}
				val inf = questionInfof.map(q ⇒ victorine.QuestionInfo(
					q.title,
					q.text,
					q.image,
					qrCode
				))

				val qe = victorine.Question(q.id, q.question, q.answers.split(';'), q.rightAnswer)

				LoadQuestionResponse(Option(QuestionData(Option(qe), inf)), qg)
			}//end for
			r
		}
	}

}
