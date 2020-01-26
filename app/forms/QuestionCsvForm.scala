package forms

import play.api.data.Forms._
import play.api.data._

object QuestionCsvForm {
	/**
	 * A play framework form.
	 */
	val form = Form(
		"file" -> text
	)
}
