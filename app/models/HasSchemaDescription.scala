package models

import models.HasSchemaDescription.SqlSchemaDescription

trait HasSchemaDescription {
	def schemaDescription: Seq[SqlSchemaDescription]
}

object HasSchemaDescription {
	type SqlSchemaDescription = slick.sql.SqlProfile#SchemaDescription
}