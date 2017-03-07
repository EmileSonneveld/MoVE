package de.thm.move.models.pattern

import java.util.function.Predicate
import javafx.scene.Node

import de.thm.move.types.ColorizableNode

sealed trait LinePattern {
  def cssClass:String
  def patternName:String = this.getClass.getSimpleName
  def modelicaRepresentation: String = s"LinePattern.${patternName}"
  def generateModelicaCode: String =
    s"pattern = ${modelicaRepresentation}"

  def applyToShape(shape:ColorizableNode): Unit = {
    LinePattern.removeOldCss(shape)
    shape.getStyleClass.add(cssClass)
  }
}

case object None extends LinePattern {
  override val cssClass: String = "none-stroke"
}

case object Solid extends LinePattern {
  override val cssClass: String = "solid-stroke"
}

case object Dash extends LinePattern {
  override val cssClass: String = "dash-stroke"
}
case object Dot extends LinePattern {
  override val cssClass: String = "dotted-stroke"
}
case object DashDot extends LinePattern {
  override val cssClass: String = "dash-dotted-stroke"
}
case object DashDotDot extends LinePattern {
  override val cssClass: String = "dash-dotted-dotted-stroke"
}

object LinePattern {
  val cssRegex = ".*-stroke"

  //remove old stroke style
  val removeOldCss: (Node) => Unit = { shape =>
    shape.getStyleClass().removeIf(new Predicate[String]() {
      override def test(str:String): Boolean = str.`matches`(cssRegex)
    })
  }
  val patternObjects:List[LinePattern] =
    List(None, Solid, Dash, Dot, DashDot, DashDotDot)
}