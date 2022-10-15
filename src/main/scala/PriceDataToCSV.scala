import java.io.PrintWriter

import common.{PriceType, NK225Mini}
import org.joda.time.DateTime
import scalikejdbc._

object PriceDataToCSV {

  //時間ごとの平均値幅を算出
  def main(args: Array[String]) {

    Class.forName("org.postgresql.Driver")
    ConnectionPool.singleton("jdbc:postgresql://localhost:5432/backtest", "ohnishi", "")

    val priceList: List[NK225Mini] = DB readOnly { implicit session =>
      sql"select * from NK225MINI order by trd_date, no".map(rs => NK225Mini(rs)).list.apply()
    }

    var open = 0
    var high = Integer.MIN_VALUE
    var low = Integer.MAX_VALUE

    val reportFile = new PrintWriter("./report/candle.csv")
    for (price <- priceList) {

      if (price.priceType == PriceType.O) {
        open = price.price
        high = price.price
        low = price.price
      }
      high = Math.max(high, price.price)
      low = Math.min(low, price.price)

      if (price.priceType == PriceType.E) {


        reportFile.println(price.trdDate.toString("yyyy-MM-dd-") + price.trdTime.toString("HH") + "," + open + "," + low + "," + high + "," + price.price)
        reportFile.flush
      }
    }

    reportFile.close
  }
}



