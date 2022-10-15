package setup

import java.io.File

import com.github.tototoshi.csv.CSVReader
import common.PriceType
import org.joda.time.format.DateTimeFormat
import org.joda.time.{LocalDate, LocalTime}
import scalikejdbc._

/**
  * Created by ohnishi on 15/10/27.
  */


object NK225Setup {

  GlobalSettings.loggingSQLAndTime = new LoggingSQLAndTimeSettings(
    enabled = true,
    singleLineMode = true,
    logLevel = 'DEBUG
  )

  def main(args: Array[String]) {

    Class.forName("com.mysql.cj.jdbc.Driver")
    ConnectionPool.singleton("jdbc:mysql://192.168.100.11:3306/backtest?serverTimezone=JST&useSSL=false", "hons", "hons")

    DB autoCommit { implicit session =>
      sql"TRUNCATE TABLE NK225MINI".update.apply
    }

    val dir = new File("./db/225mini")

    readFolder(dir)
  }

  def readFolder(dir: File) {
    val files = dir.listFiles

    for (file <- files) {
      if (file.isDirectory) readFolder(file)
      else if (file.isFile && file.getName.toLowerCase.endsWith(".csv")) readFile(file)
    }
  }

  def isMajorSecCode(code: String): Boolean = {

    val mounth = code.substring(3, 5)
    if (mounth == "03" || mounth == "06" || mounth == "09" || mounth == "12") return true
    else return false
  }


  def readFile(file: File): Unit = {


    val reader = CSVReader.open(file)
    val csvList = reader.allWithHeaders


    DB localTx { implicit session =>

      val code = csvList.find(m => isMajorSecCode(m("SEC_CODE"))).getOrElse(throw new RuntimeException)("SEC_CODE")
      val trdDate = DateTimeFormat.forPattern("yyyyMMdd").parseLocalDate(csvList.head("TRADING_DATE"))

      for (csvMap <- csvList.filter(m => m("SEC_CODE") == code)) {
        val nk225 = NK225(csvMap)
        sql"""INSERT INTO NK225MINI (trd_date, trd_time, mk_date, price, price_type, vol, no)
            VALUES (${nk225.trdDate}, ${nk225.trdTime}, ${nk225.mkDate}, ${nk225.price}, ${nk225.priceType.toString}, ${nk225.vol}, ${nk225.no})""".update.apply
      }
    }
    println(file.getName)
    reader.close
  }
}

case class NK225(trdDate: LocalDate, trdTime: LocalTime, mkDate: LocalDate, price: Int, priceType: PriceType.Value, vol: Int, no: Int)

object NK225 {

  def apply(csvMap: Map[String, String]) = {
    new NK225(
      DateTimeFormat.forPattern("yyyyMMdd").parseLocalDate(csvMap("TRADING_DATE")),
      DateTimeFormat.forPattern("HHmmss").parseLocalTime(csvMap("BUSINESS_TIME").take(6)),
      DateTimeFormat.forPattern("yyyyMMdd").parseLocalDate(csvMap("MAKING_DATE")),
      csvMap("TRD_PRICE").toInt,
      PriceType.withName(csvMap("PRICE_TYP")),
      csvMap("TRD_VOL").toInt,
      csvMap("NO").toInt) //取引通番
  }
}

//"TRADING_DATE","MAKING_DATE","SEC_CODE","BUSINESS_TIME","TRD_PRICE","PRICE_TYP","TRD_VOL","NO"
//"20150803","20150731","160080019   ","163029",20605,"N",2,17
