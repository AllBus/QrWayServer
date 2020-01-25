package models.daos

import java.time.ZonedDateTime
import java.util.UUID

import javax.inject.Inject
import models.AuthToken
import play.api.db.slick.DatabaseConfigProvider
import MyPostgresProfile.api._

import scala.concurrent.Future

/**
 * Give access to the [[AuthToken]] object.
 */
class AuthTokenDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider) extends DAOSlick {

  /**
   * Finds a token by its ID.
   *
   * @param id The unique token ID.
   * @return The found token or None if no token for the given ID could be found.
   */
  def find(id: UUID): Future[Option[AuthToken]] = {
    db.run(slickAuthTokens.filter(_.id === id).result.headOption)
  }

  /**
   * Finds expired tokens.
   *
   */
  def findExpired(): Future[Seq[AuthToken]] = {
    db.run(slickAuthTokens.filter(_.expiry < ZonedDateTime.now()).result)
  }


  /**
   * Saves a token.
   *
   * @param token The token to save.
   * @return The saved token.
   */
  def save(token: AuthToken): Future[Int] = {
    db.run(slickAuthTokens.insertOrUpdate(token))
  }

  /**
   * Removes the token for the given ID.
   *
   * @param id The ID for which the token should be removed.
   * @return A future to wait for the process to be completed.
   */
  def remove(id: UUID): Future[Int] = {
    db.run(slickAuthTokens.filter(_.id === id).delete)
  }
}
