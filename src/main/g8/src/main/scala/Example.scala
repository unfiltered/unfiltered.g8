package com.example

import unfiltered.request._
import unfiltered.response._

import unfiltered.directives._, Directives._

//** unfiltered plan */
class App extends unfiltered.filter.Plan {

  def intent = Directive.Intent {
    case GET(r) =>
      success(Ok ~> view(Map.empty)(<p> What say you? </p>))

    case POST(Params(params)) =>
      case class BadParam(msg: String) extends ResponseJoiner(msg)( messages =>
          view(params)(<ul>{
            for (message <- messages)
            yield <li>{message}</li>
          }</ul>)
      )
      implicit def required[T] = data.Requiring[T].fail(name =>
        BadParam(name + " is missing")
      )
      val inputString = data.as.String ~>
        data.as.String.trimmed ~>
        data.as.String.nonEmpty.fail(
          (key, _) => BadParam(s"\$key is empty")
        )
      implicit val asInt = inputString ~> data.as.Int.fail(
        (_, i) => BadParam(s"'\$i' is not an int")
      )

      for {
        int & word <-
          (data.as.Required[Int] named "int") &
          (inputString ~> data.Conditional(palindrome).fail(
            (_, value) => BadParam(s"'\$value' is not a palindrome")
          ) ~> required named "palindrome")
      } yield {
        view(params)(<p>Yup. { int } is an integer and { word } is a palindrome. </p>)
      }
  }
  def palindrome(s: String) = s.toLowerCase.reverse == s.toLowerCase
  def view(params: collection.Map[String, Seq[String]])(body: scala.xml.NodeSeq) = {
    def p(k: String) = params.get(k).flatMap { _.headOption } getOrElse("")
    Html5(
     <html>
      <head>
        <title>uf example</title>
        <link rel="stylesheet" type="text/css" href="/assets/css/app.css"/>
      </head>
      <body>
       <div id="container">
       { body }
       <form method="POST">
         <div>Integer <input type="text" name="int" value={ p("int") } /></div>
         <div>Palindrome <input type="text" name="palindrome" value={ p("palindrome") } /></div>
         <input type="submit" />
       </form>
       </div>
     </body>
    </html>
   )
  }
}

/** embedded server */
object Server {
  def main(args: Array[String]): Unit = {
    unfiltered.jetty.Server.anylocal.context("/assets") {
      _.resources(new java.net.URL(getClass().getResource("/www/css"), "."))
    }.plan(new App).run({ svr =>
      unfiltered.util.Browser.open(svr.portBindings.head.url)
    })
  }
}
