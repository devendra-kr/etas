package models

import scala.concurrent.Future
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import org.slf4j.LoggerFactory

object Utilities {
  
  val Log = LoggerFactory getLogger getClass
  
  def executeSynchronous[T](f: Future[T], failureLog: String) = {
    Await.result(f, Duration.Inf)
    val res = f.value.flatMap(x => x.toOption)

    if (res.isEmpty) {
      Log error "Error in getting result for future f: " + failureLog
    }
    res
  }

  def toSimpleOption[T](opt: Option[Option[T]]) = {
    opt.flatMap(x => x)
  }

  def toSimpleList[T](opt: List[Vector[T]]) = {
    opt.flatMap(x => x)
  }
  
  def toSimpleListForSeq[T](opt: List[Seq[T]]) = {
    opt.flatMap(x => x)
  }
}