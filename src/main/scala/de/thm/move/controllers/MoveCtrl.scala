package de.thm.move.controllers

import java.net.URL
import java.util.ResourceBundle
import javafx.collections.FXCollections
import javafx.event.ActionEvent
import javafx.fxml.{Initializable, FXML}
import javafx.scene.Cursor
import javafx.scene.canvas.{GraphicsContext, Canvas}
import javafx.scene.control._
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import collection.JavaConversions._

import de.thm.move.models.SelectedShape
import de.thm.move.models.SelectedShape.SelectedShape
import implicits.FxHandlerImplicits._

class MoveCtrl extends Initializable {

  type Point = (Double,Double)

  @FXML
  var mainCanvas: Canvas = _
  @FXML
  var btnGroup: ToggleGroup = _
  @FXML
  var fillColorPicker: ColorPicker = _
  @FXML
  var strokeColorPicker: ColorPicker = _

  @FXML
  var borderThicknessChooser: ChoiceBox[Int] = _

  private val shapeBtnsToSelectedShapes = Map(
      "rectangle_btn" -> SelectedShape.Rectangle,
      "circle_btn" -> SelectedShape.Circle,
      "line_btn" -> SelectedShape.Line,
      "polygon_btn" -> SelectedShape.Polygon
    )

  override def initialize(location: URL, resources: ResourceBundle): Unit = {
    fillColorPicker.setValue(Color.BLACK)
    strokeColorPicker.setValue(Color.BLACK)

    val sizesList:java.util.List[Int] = (1 until 20).toList
    borderThicknessChooser.setItems(FXCollections.observableArrayList(sizesList))
    borderThicknessChooser.getSelectionModel.selectFirst()

     var startX = -1.0
     var startY = -1.0

      var points = List[Point]()

    val drawHandler = { mouseEvent:MouseEvent =>
      selectedShape match {
        case s@Some(SelectedShape.Polygon) =>
          if (mouseEvent.getEventType() == MouseEvent.MOUSE_CLICKED) {
            val newX = mouseEvent.getX()
            val newY = mouseEvent.getY()

            points.find {
              case (x,y) => Math.abs(x-newX)<=10 && Math.abs(y-newY)<=10
            } match {
              case Some(_) =>
                drawColored { context =>
                  val size = points.length
                  val xs = points.map(_._1).toArray
                  val ys = points.map(_._2).toArray
                  context.fillPolygon(xs, ys, size)
                }
                points = List()
              case None =>
                points = (newX,newY) :: points

                drawColored { canvas =>
                  canvas.fillOval(newX, newY, 4,4)
                }
            }
          }
        case Some(_) =>
          if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) {
            points = (mouseEvent.getX(), mouseEvent.getY()) :: points
          } else if (mouseEvent.getEventType() == MouseEvent.MOUSE_RELEASED) {
            points = (mouseEvent.getX(), mouseEvent.getY()) :: points

            points match {
              case end::start::_ => drawCustomShape(start, end)
            }
          }
        case _ =>
        //ignore
      }
    }

    mainCanvas.setOnMousePressed(drawHandler)
    mainCanvas.setOnMouseClicked(drawHandler)
    mainCanvas.setOnMouseReleased(drawHandler)

  }


  private def drawPoint(x:Point):Unit = mainCanvas.getGraphicsContext2D.strokeOval(x._1, x._2, 5,5)

  @FXML
  def onPointerClicked(e:ActionEvent): Unit = changeDrawingCursor(Cursor.DEFAULT)
  @FXML
  def onCircleClicked(e:ActionEvent): Unit = changeDrawingCursor(Cursor.CROSSHAIR)
  @FXML
  def onRectangleClicked(e:ActionEvent): Unit = changeDrawingCursor(Cursor.CROSSHAIR)
  @FXML
  def onLineClicked(e:ActionEvent): Unit = changeDrawingCursor(Cursor.CROSSHAIR)
  @FXML
  def onPolygonClicked(e:ActionEvent): Unit = changeDrawingCursor(Cursor.CROSSHAIR)

  private def getStrokeColor: Color = strokeColorPicker.getValue
  private def getFillColor: Color = fillColorPicker.getValue
  private def selectedThickness: Int = borderThicknessChooser.getSelectionModel.getSelectedItem
  private def changeDrawingCursor(c:Cursor): Unit = mainCanvas.setCursor(c)

  private def drawColored[A](fn: GraphicsContext => A): A = {
    val context = mainCanvas.getGraphicsContext2D
    context.setFill(getFillColor)
    context.setStroke(getStrokeColor)
    fn(context)
  }

  private def selectedShape: Option[SelectedShape] = {
    val btn = btnGroup.getSelectedToggle.asInstanceOf[ToggleButton]
    Option(btn.getId).flatMap(shapeBtnsToSelectedShapes.get(_))
  }

  private def drawCustomShape(start:Point, end:Point) = {
    drawColored { canvas =>
      val (startX, startY) = start
      val (endX, endY) = end
      println(selectedShape)
      selectedShape.foreach {
        case SelectedShape.Rectangle =>
          val width = endX - startX
          val height = endY - startY
          canvas.fillRect(startX, startY, width, height)
        case SelectedShape.Polygon =>
        case SelectedShape.Circle =>
          val width = endX - startX
          val height = endY - startY
          canvas.fillOval(startX, startY, width, height)
        case SelectedShape.Line =>
          val thickness = borderThicknessChooser.getSelectionModel.getSelectedItem
          canvas.setLineWidth(thickness)
          canvas.strokeLine(startX, startY, endX, endY)
      }

    }
  }
}