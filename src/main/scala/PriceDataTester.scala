import common.NK225Mini
import scalikejdbc._

object PriceDataTester {

  //時間ごとの平均値幅を算出
  def main(args: Array[String]) {

    Class.forName("org.postgresql.Driver")
    ConnectionPool.singleton("jdbc:postgresql://localhost:5432/backtest", "ohnishi", "")

    val trdDates = DB readOnly { implicit session =>
      sql"select trd_date from TRADE_DAYS order by trd_date".map { rs => rs.jodaLocalDate("trd_date") }.list.apply()
    }

    var priceRange = Map.empty[Int, Int]
    for (trdDate <- trdDates) {
      val priceList: List[NK225Mini] = DB readOnly { implicit session =>
        sql"select * from NK225MINI where trd_date = ${trdDate} order by no".map(rs => NK225Mini(rs)).list.apply()
      }

      var highPrice = Map.empty[Int, Int]
      var lowPrice = Map.empty[Int, Int]

      for (price <- priceList) {
        val hour = price.trdTime.getHourOfDay
        if (highPrice.isDefinedAt(hour)) highPrice += hour -> Math.max(highPrice(hour), price.price) else highPrice += hour -> price.price
        if (lowPrice.isDefinedAt(hour)) lowPrice += hour -> Math.min(lowPrice(hour), price.price) else lowPrice += hour -> price.price
      }

      for (i <- 0 to 23) {
        if (highPrice.isDefinedAt(i)) {
          val range = highPrice(i) - lowPrice(i)
          if (priceRange.isDefinedAt(i)) priceRange += i -> (priceRange(i) + range) else priceRange += i -> range
        }
      }
    }

    //valueでソート
    println(priceRange.toSeq.sortBy(_._2))
    for (i <- 0 to 23) {
      if (priceRange.isDefinedAt(i)) {
        val avgRange = priceRange(i) / trdDates.size
        println(s"$i 時  平均値幅 = $avgRange")
      }
    }
  }
}



