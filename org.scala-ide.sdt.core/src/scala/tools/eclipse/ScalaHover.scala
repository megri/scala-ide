/*
 * Copyright 2005-2010 LAMP/EPFL
 */
// $Id$

package scala.tools.eclipse

import org.eclipse.jdt.core.ICodeAssist
import org.eclipse.jface.text.{ ITextViewer, IRegion, ITextHover }

import scala.tools.eclipse.javaelements.ScalaCompilationUnit

class ScalaHover(codeAssist : Option[ICodeAssist]) extends ITextHover {
  
  private val _noHoverInfo = "" // could return null, but prefer to return empty (see API of ITextHover).

  override def getHoverInfo(viewer : ITextViewer, region :  IRegion) = {
    codeAssist match {
      case Some(scu : ScalaCompilationUnit) => {
        val start = region.getOffset
        val end = start + region.getLength
        scu.withSourceFile ({ (src, compiler) =>
          import compiler._
          val resp = new Response[Tree]
          val range = compiler.rangePos(src, start, start, end)
          askTypeAt(range, resp)
          resp.get.left.toOption map {	t =>
            ask { () => t.symbol.defString }
          } getOrElse _noHoverInfo
        }) (_noHoverInfo)
      }
      case _ => _noHoverInfo
    }
  }
  
  override def getHoverRegion(viewer : ITextViewer, offset : Int) = {
    ScalaWordFinder.findWord(viewer.getDocument, offset)
  }
}