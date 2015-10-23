package de.mixas.streams

import java.io.File
import java.time.{ZoneOffset, LocalDateTime, Instant}
import java.time.format.DateTimeFormatter

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

import scala.collection.SortedSet

/**
 *
 * @param location place of measurment
 * @param date point of time when measured
 * @param levelInCm water depth of measurement
 */
case class DataSet(location: String, date: Instant, levelInCm: Double)

/**
 * Handling data source for jsoup
 */
sealed trait DocumentSource
case class Local(location : String) extends DocumentSource
case class Remote(location : String) extends DocumentSource


class RetrieveWaterLevel(document: () => Document) extends Function1[String, Option[DataSet]] {
  private implicit val ordering = new Ordering[DataSet]{
    override def compare(x: DataSet, y: DataSet): Int = x.date.compareTo(y.date)
  }
  private val dateformatter = DateTimeFormatter.ofPattern("dd.MM.yyyy H:mm")
  private var dataSet = SortedSet.empty[DataSet]
  private var lastLevelDate = Instant.MIN

  override def apply(v1: String): Option[DataSet] = {
    dataSet match {
      case d if d.isEmpty => refresh()
        None
      case d => val res = dataSet.head
        dataSet = dataSet.tail
        lastLevelDate = res.date
        Some(res)
    }
  }

  private def refresh()={
    val doc = document()
    val location = doc.getElementsByClass("smallheader").first().text()
    val head = doc.select("table").last().select("tbody").select("tr")
    val levels = extract(head,location)
    dataSet = dataSet ++ levels
  }

  private def extract(fields:Elements,location : String)={
    import scala.collection.JavaConversions._
    val header = fields.head.select("td")
    val dataRows = fields.tail
    val ds = (for {dataRow <- dataRows
                   lineElements = dataRow.select("td")
                   time = lineElements.head.text
                   level <- lineElements.tail.zip(header.tail)
    } yield {
        val timeString = level._2.text + " " + time
        val temporal = LocalDateTime.parse(timeString, dateformatter)
        val instant = temporal.toInstant(ZoneOffset.UTC)
        val l = asLevel(level._1.text)
        (instant, l)
      }).toList

    val cleanedDs = ds.filterNot{ d =>  d._2 == None}.map{ d => DataSet(location,d._1,d._2.get)}.filter(_.date.isAfter(lastLevelDate))
    val res = cleanedDs.to[SortedSet]
    res
  }


  private def asLevel(data: String): Option[Double] = {
    val level = data.replace(",", ".").trim
    try {
      Some(level.toDouble)
    } catch {
      case x: NumberFormatException => None
    }
  }

}

object RetrieveWaterLevel{

  def apply(location : DocumentSource) = {
    val doc = location match {
      case Local(l) => () => Jsoup.parse(new File(l),"utf-8")
      case Remote(l) => () => Jsoup.connect(l).get()
    }
    val res = new RetrieveWaterLevel(doc)
    res
  }
}