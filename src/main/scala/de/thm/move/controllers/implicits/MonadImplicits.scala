package de.thm.move.controllers.implicits

import java.util.Optional
import scala.language.implicitConversions

object MonadImplicits {
  implicit def asOption[T](opt : Optional[T]): Option[T] = {
    if(opt.isPresent) Some(opt.get)
    else None
  }
}
