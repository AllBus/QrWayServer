package controllers

import com.mohiva.play.silhouette.api.Silhouette
import javax.inject.Inject
import models.daos.UserManagementDAO
import models.services.{MailerService, UserService}
import play.api.mvc.{AbstractController,  ControllerComponents}
import utils.auth.DefaultEnv

import scala.concurrent.{ExecutionContext, Future}

class MailController @Inject()(components: ControllerComponents,
                              userService: UserService,
                              userManagementDAO: UserManagementDAO,
                              mailer: MailerService,
                              silhouette: Silhouette[DefaultEnv])(implicit ec: ExecutionContext) extends AbstractController(components) {

	def sendMail(subject:String,email:String) = Action.async {
		{
			Future(mailer.sendEmail(subject,s"Kr TO <$email>")).map(x ⇒Ok(s"$x"))
		}
	}
}
