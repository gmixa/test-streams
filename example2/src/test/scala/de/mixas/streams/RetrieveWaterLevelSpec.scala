package de.mixas.streams

import java.time.{ZoneOffset, LocalDateTime}
import java.time.format.DateTimeFormatter

import org.scalatest.FlatSpec
import org.scalatest.Matchers


class RetrieveWaterLevelSpec extends FlatSpec with Matchers{

  val publisher = RetrieveWaterLevel(Local("src/test/resources/WasserstandPassau.html"))
  val zeroResult = publisher("Test")
  val firstResult = publisher("Test")
  val secondResult = publisher("Test")
  "A RetrieveWaterLevelSpec for Passau" should "publish levels" in {


    val dateformatter = DateTimeFormatter.ofPattern("dd.MM.yyyy H:mm")
    val temporal = LocalDateTime.parse("14.09.2015 0:00", dateformatter)
    val instant = temporal.toInstant(ZoneOffset.UTC)

    val res = Some(DataSet("Passau / Donau", instant, 411.0))

    firstResult should be (res)
  }

  it should "publish another level " in {
    val dateformatter = DateTimeFormatter.ofPattern("dd.MM.yyyy H:mm")
    val temporal = LocalDateTime.parse("14.09.2015 0:15", dateformatter)
    val instant = temporal.toInstant(ZoneOffset.UTC)

    val res = Some(DataSet("Passau / Donau", instant, 411.0))

    secondResult should be (res)
  }

  it should "publish 624 consecutive levels " in {
    var results : List[Option[DataSet]] = Nil

    var data : Option[DataSet] = None
    do{
      data = publisher("Tick")
      results = data :: results

    }while(data != None)


    results.size should be(624)
    results.head should be(None)
    results.count(_ == None) should be (1)
    results.count(_ != None) should be (623)


  }


}
