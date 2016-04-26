/**
 * Copyright (C) 2016 Nicola Justus <nicola.justus@mni.thm.de>
 */

package de.thm.move.util

import java.net.URI
import java.nio.file.Path
import javafx.scene.paint.Color
import de.thm.move.Global._

object ResourceUtils {

  def getFilename(uri:URI):String = {
    val uriStr = uri.toString
    uriStr.substring(uriStr.lastIndexOf("/")+1, uriStr.length)
  }

  def getFilename(p:Path):String = {
    p.getFileName.toString
  }

  def asColor(key:String): Option[Color] =
    config.getString(key).map(Color.web)
}
