package analysis

import common.NK225Mini
import org.joda.time.format.DateTimeFormat
import scalikejdbc._

object AnalyzerTest {

  def main(args: Array[String]) {

    Class.forName("com.mysql.cj.jdbc.Driver")
    ConnectionPool.singleton("jdbc:mysql://localhost:3306/backtest?serverTimezone=JST&useSSL=false", "hons", "hons")

    val toDate = DateTimeFormat.forPattern("yyyy-MM-dd").parseLocalDate("2016-07-01")
    val fromDate = toDate.minusMonths(12)

    val priceList: List[NK225Mini] = DB readOnly { implicit session =>
      sql"select * from NK225MINI where trd_date >= ${fromDate} and trd_date < ${toDate} order by trd_date, no".map(rs => NK225Mini(rs)).list.apply()
    }

    var strategyList = Seq.empty[StrategyOne]



    //strategyList +:= new StrategyOne(17,0,19,0,90,50,-20)




    for (strategy <- strategyList.par) {

      val (totalRieki, totalSon, tradeCount) = strategy.exec(priceList)

      val allPf = (totalRieki * 100 / (if (totalSon == 0) 1 else totalSon)) / 100d
      val cost = tradeCount * 42
      val allRieki = ((totalRieki - totalSon) * 100) - cost
      val params = strategy.params
      val outLine = s"$allPf,$tradeCount,$allRieki,$params"
      println(outLine)
    }
  }
}
