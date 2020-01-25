package controllers

import com.google.inject.Inject
import models.HasSchemaDescription
import models.HasSchemaDescription.SqlSchemaDescription
import models.daos.{UserDAO, UserDAOImpl}
import play.api.Environment
import play.api.Mode
import play.api.mvc.{AbstractController, ControllerComponents}



class SchemaEvolutionsController @Inject() (components: ControllerComponents,
                                            environment: Environment,
                                            catDao : UserDAOImpl) extends AbstractController(components) {

	def allSchemas : Seq[HasSchemaDescription] = List(catDao) // List all schemas here

	def descriptionsForAllSchemas : Seq[SqlSchemaDescription] = allSchemas.flatMap(_.schemaDescription)

	def evolutions = Action {
		environment.mode match {
			case Mode.Prod => NotFound
			case _ => Ok(views.txt.evolutions(descriptionsForAllSchemas)).withHeaders(CONTENT_TYPE -> "text/plain")
		}
	}
}