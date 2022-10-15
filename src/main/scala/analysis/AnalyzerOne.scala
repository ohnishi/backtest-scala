package analysis

import common.NK225Mini
import org.joda.time.LocalTime
import org.joda.time.format.DateTimeFormat
import scalikejdbc._

object AnalyzerOne {

  def main(args: Array[String]) {

    Class.forName("com.mysql.cj.jdbc.Driver")
    ConnectionPool.singleton("jdbc:mysql://192.168.100.11:3306/backtest?serverTimezone=JST&useSSL=false", "hons", "hons")

    val toDate = DateTimeFormat.forPattern("yyyy-MM-dd").parseLocalDate("2017-02-01")
    val fromDate = toDate.minusMonths(25)

    val fromTime = new LocalTime(8, 45, 0, 0)
    val toTime = new LocalTime(15, 15, 0, 0)

    val priceList: List[NK225Mini] = DB readOnly { implicit session =>
      sql"""select * from NK225MINI
           where trd_date >= ${fromDate} and trd_date < ${toDate}
           and trd_time >= ${fromTime} and trd_time <= ${toTime}
           order by trd_date, no""".map(rs => NK225Mini(rs)).list.apply()
    }

    var strategyList = Seq.empty[StrategyOne]

    //strategyList +:= new StrategyOne(9,0,12,30,110,80,0)


    strategyList +:= new StrategyOne(10, 0, 12, 45, 14, 30, 14, 45, 190, 90, -20, 280)
    strategyList +:= new StrategyOne(10, 0, 12, 45, 14, 30, 15, 0, 190, 90, -20, 280)
    strategyList +:= new StrategyOne(10, 0, 12, 45, 14, 30, 15, 15, 190, 90, -20, 280)

//
//    1.79,423,527334,10,0,12,45,14,30,14,30,190,90,-20,280
//    1.72,423,609334,10,0,12,45,14,30,15,0,190,90,-20,280
//    1.64,422,820376,10,0,12,45,14,30,15,15,190,90,-20,280

//    1.76,407,619906,10,0,12,45,14,30,15,0,190,90,-20,280
//    1.78,407,520406,10,0,12,45,14,30,14,30,190,90,-20,280
//    1.69,407,843406,10,0,12,45,14,30,15,15,190,90,-20,280



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
