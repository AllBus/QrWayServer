package csv

import java.io.{BufferedReader, FileReader, IOException, InputStreamReader}

import com.opencsv.{CSVReader, CSVReaderBuilder}
import play.api.libs.Files
import qrway2.victorine.{FullQuestionGroup, GroupHeader, Question, QuestionData, QuestionGroup, QuestionInfo, QuestionInfoWithId}


object QuestionReader {

	val questionAppendId = 1000000L

	def createHeader(nextLine: Array[String]) = {
		val id = nextLine(0).toInt
		id → QuestionGroup(id, questionNamePrefix = nextLine(4)).
			withHeader(GroupHeader(
				nextLine(1),
				java.lang.Long.parseLong(nextLine(5), 16).toInt,
				nextLine(2)
			))
			.withQuestionIds((1L to nextLine(3).toLong).map(_ + id * questionAppendId).toSeq)

	}

	def createQuestion(data: Array[String], groupId: Long) = {
		val id = data(0).toInt + groupId
		id → Question(
			id,
			data(1),
			data.drop(3),
			data(2).toInt,
		)
	}

	def createDescription(data: Array[String], groupId: Long) = {
		QuestionInfoWithId(data(0).toInt + groupId,
			Option(QuestionInfo(
				title = data(1),
				text = data(3),
				image = data(2),
				qr = data(4)
			))
		)
	}


	def readCsv(file: Files.TemporaryFile) = {

		//file.copyTo()
		val result = Seq.newBuilder[FullQuestionGroup] //Map.newBuilder[Int, VictorineQuestion]
		val questions = Seq.newBuilder[Question]
		val questionInfos = Seq.newBuilder[QuestionInfoWithId]
		var tekHeader = QuestionGroup()
		val headers = Seq.newBuilder[QuestionGroup]

		try {
			val reader = new CSVReader(
				new BufferedReader(new FileReader(file.path.toFile)),
				';', '\"'
			)

			var nextLine: Array[String] = null
			var groupId = 0L


			while ( {
				nextLine = reader.readNext
				nextLine != null
			}) {

				if (nextLine.nonEmpty) {
					nextLine.head match {
						case "h" ⇒
							val (id, header) = createHeader(nextLine.tail)
							if (tekHeader.id>0) {
								result += FullQuestionGroup(Option(tekHeader),questions.result(),questionInfos.result())
							}
							questions.clear()
							questionInfos.clear()
							tekHeader = header
							groupId = id * questionAppendId
						case "q" ⇒
							val (id, question) = createQuestion(nextLine.tail, groupId)
							questions += question
						case "d" ⇒
							val infos = createDescription(nextLine.tail, groupId)
							questionInfos += infos
						case _ ⇒
					}
				}
				//		val question = VictorineQuestion(nextLine)
				//	result += question.id → question
				//result += nextLine.mkString(" - ")
			}
		} catch {
			case e: IOException ⇒

		}

		result += FullQuestionGroup(Option(tekHeader),questions.result(),questionInfos.result())
		result.result()
	}

}
