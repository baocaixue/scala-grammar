package com.isaac.ch9

object FileMather {
  private def filesHere = (new java.io.File(".")).listFiles()

  def filesEnding(query: String) =
//    for (file <- filesHere; if file.getName.endsWith(query))
//      yield file
//      filesMatching(query, (fileName, condition) => fileName.endsWith(condition))
        filesMatching(_.endsWith(query))

  def filesContaining(query: String) =
//    for (file <- filesHere; if file.getName.contains(query))
//      yield file
      filesMatching(_.contains(query))

  def filesRegex(query: String) =
//    for (file <- filesHere; if file.getName.matches(query))
//      yield file
      filesMatching(_.matches(query))

  def filesMatching(matcher: String  => Boolean) =
    for (file <- filesHere; if matcher(file.getName))
      yield file
}
