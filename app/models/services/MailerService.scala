package models.services

import play.api.libs.mailer._
import javax.inject.Inject

class MailerService @Inject() (mailerClient: MailerClient) {

	private val from = "Mystery <noreply@qrway.com>"


	def sendResetPasswordEmail(email: String, url: String): Unit = {
		sendHtmlEmail(
			from,
			email,
			"Site password reset",
			"<html><body><p>Please <a href='" + url + "' rel='nofollow'>click here</a> to reset your password.</p><p>If you didn't request password reset, please ignore this mail.</p></body></html>",
			"Site password reset"
		)
	}



	def sendActivateAccountEmail(email: String, url: String): Unit = {
		sendHtmlEmail(
			from,
			email,
			"Account confirmation",
			"<html><body><p>Please <a href='" + url + "' rel='nofollow'>click here</a> to confirm your account.</p><p>If you didn't create an account using this e-mail address, please ignore this message.</p></body></html>",
			"Account confirmation"
		)
	}

	def sendHtmlEmail(from: String, to: String, subject: String, body: String, loggerNote: String): Unit = {

		val email = Email(
			subject,
			from,
			Seq(to),//Seq("Miss TO <to@email.com>"),
			// adds attachment
			attachments = Seq(
				//				AttachmentFile("attachment.pdf", new File("/some/path/attachment.pdf")),
				//				// adds inline attachment from byte array
				//				AttachmentData("data.txt", "data".getBytes, "text/plain", Some("Simple data"), Some(EmailAttachment.INLINE)),
				//				// adds cid attachment
				//				AttachmentFile("image.jpg", new File("/some/path/image.jpg"), contentId = Some(cid))
			),
			// sends text, HTML or both...
		//	bodyText = Some("A text message"),
			bodyHtml = Some(body)
		)
		mailerClient.send(email)
	}

	def sendEmail(subject:String, to:String) = {
		val cid = "1234"
		val email = Email(
			subject,
			"Mister FROM <from@email.com>",
			Seq(to),//Seq("Miss TO <to@email.com>"),
			// adds attachment
			attachments = Seq(
//				AttachmentFile("attachment.pdf", new File("/some/path/attachment.pdf")),
//				// adds inline attachment from byte array
//				AttachmentData("data.txt", "data".getBytes, "text/plain", Some("Simple data"), Some(EmailAttachment.INLINE)),
//				// adds cid attachment
//				AttachmentFile("image.jpg", new File("/some/path/image.jpg"), contentId = Some(cid))
			),
			// sends text, HTML or both...
			bodyText = Some("A text message"),
			bodyHtml = Some(s"""<html><body><p>An <b>html</b> message with cid <img src="cid:$cid"></p></body></html>""")
		)
		mailerClient.send(email)
	}

}