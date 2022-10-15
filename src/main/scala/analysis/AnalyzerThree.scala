package analysis

import common.NK225Mini
import org.joda.time.LocalTime
import org.joda.time.format.DateTimeFormat
import scalikejdbc._

object AnalyzerThree {

  def main(args: Array[String]) {

    Class.forName("com.mysql.cj.jdbc.Driver")
    ConnectionPool.singleton("jdbc:mysql://localhost:3306/backtest?serverTimezone=JST&useSSL=false", "hons", "hons")

    val toDate = DateTimeFormat.forPattern("yyyy-MM-dd").parseLocalDate("2017-01-01")
    val fromDate = toDate.minusMonths(24)

    val fromTime = new LocalTime(8, 45, 0, 0)
    val toTime = new LocalTime(15, 15, 0, 0)

    val priceList: List[NK225Mini] = DB readOnly { implicit session =>
      sql"""select * from NK225MINI
           where trd_date >= ${fromDate} and trd_date < ${toDate}
           and trd_time >= ${fromTime} and trd_time <= ${toTime}
           order by trd_date, no""".map(rs => NK225Mini(rs)).list.apply()
    }

    var strategyList = Seq.empty[StrategyThree]


    //    strategyList +:= new StrategyThree(9,0,10,0,190,190,-20)
    //    strategyList +:= new StrategyThree(9,0,10,0,190,110,-20)

    strategyList +:= new StrategyThree(10,30,13,15,14,0,250,70,-20,270)
    strategyList +:= new StrategyThree(10,0,12,30,14,45,240,50,-20,240)
    strategyList +:= new StrategyThree(10,30,13,15,14,15,250,70,-20,260)
    strategyList +:= new StrategyThree(10,30,13,15,14,0,260,70,-20,270)
    strategyList +:= new StrategyThree(10,0,12,30,14,45,240,50,-20,250)
    strategyList +:= new StrategyThree(10,30,13,15,14,15,250,70,-20,270)
    strategyList +:= new StrategyThree(10,30,13,15,14,0,270,70,-20,270)
    strategyList +:= new StrategyThree(10,0,12,30,14,45,240,50,-20,260)
    strategyList +:= new StrategyThree(10,30,13,15,14,15,260,70,-20,260)
    strategyList +:= new StrategyThree(10,0,12,30,14,30,240,50,-20,240)
    strategyList +:= new StrategyThree(10,0,12,30,14,45,240,50,-20,270)
    strategyList +:= new StrategyThree(10,30,13,15,14,15,260,70,-20,270)
    strategyList +:= new StrategyThree(10,0,12,30,14,30,240,50,-20,250)
    strategyList +:= new StrategyThree(10,0,12,30,14,45,260,50,-20,240)
    strategyList +:= new StrategyThree(10,15,12,45,14,15,120,60,-20,290)
    strategyList +:= new StrategyThree(10,0,12,30,14,30,240,50,-20,260)
    strategyList +:= new StrategyThree(10,0,12,30,14,45,260,50,-20,250)
    strategyList +:= new StrategyThree(10,15,12,45,14,15,120,60,-20,300)
    strategyList +:= new StrategyThree(10,0,12,30,14,30,240,50,-20,270)
    strategyList +:= new StrategyThree(10,0,12,30,14,45,260,50,-20,260)
    strategyList +:= new StrategyThree(10,30,13,15,14,15,270,70,-20,260)
    strategyList +:= new StrategyThree(10,0,12,30,14,30,260,50,-20,240)
    strategyList +:= new StrategyThree(10,0,12,30,14,45,260,50,-20,270)
    strategyList +:= new StrategyThree(10,15,12,45,14,15,120,70,-20,290)
    strategyList +:= new StrategyThree(10,0,12,30,14,30,260,50,-20,250)
    strategyList +:= new StrategyThree(10,0,12,30,14,45,270,50,-20,240)
    strategyList +:= new StrategyThree(10,15,12,45,14,15,120,70,-20,300)
    strategyList +:= new StrategyThree(10,0,12,30,14,30,260,50,-20,260)
    strategyList +:= new StrategyThree(10,0,12,30,14,45,270,50,-20,250)
    strategyList +:= new StrategyThree(10,30,13,15,14,15,270,70,-20,270)
    strategyList +:= new StrategyThree(10,0,12,30,14,30,260,50,-20,270)
    strategyList +:= new StrategyThree(10,0,12,30,14,45,270,50,-20,260)
    strategyList +:= new StrategyThree(10,0,12,45,14,30,190,90,-20,270)
    strategyList +:= new StrategyThree(10,0,12,30,14,30,270,50,-20,240)
    strategyList +:= new StrategyThree(10,0,12,30,14,45,270,50,-20,270)
    strategyList +:= new StrategyThree(10,0,12,45,14,30,190,90,-20,280)
    strategyList +:= new StrategyThree(10,0,12,30,14,30,270,50,-20,250)
    strategyList +:= new StrategyThree(10,0,12,45,14,45,140,50,-20,290)
    strategyList +:= new StrategyThree(10,0,12,45,14,30,190,90,-20,290)
    strategyList +:= new StrategyThree(10,0,12,30,14,30,270,50,-20,260)
    strategyList +:= new StrategyThree(10,0,12,45,14,45,140,50,-20,300)
    strategyList +:= new StrategyThree(10,0,12,45,14,30,190,90,-20,300)
    strategyList +:= new StrategyThree(10,0,12,30,14,30,270,50,-20,270)
    strategyList +:= new StrategyThree(10,0,12,45,14,45,150,50,-20,290)
    strategyList +:= new StrategyThree(10,30,13,15,14,30,250,70,-20,260)
    strategyList +:= new StrategyThree(10,30,13,15,14,30,260,70,-20,260)
    strategyList +:= new StrategyThree(10,0,12,45,14,45,150,50,-20,300)
    strategyList +:= new StrategyThree(10,30,13,15,14,30,250,70,-20,270)
    strategyList +:= new StrategyThree(10,30,13,15,14,30,260,70,-20,270)
    strategyList +:= new StrategyThree(10,0,12,45,14,45,150,70,-20,290)
    strategyList +:= new StrategyThree(10,15,12,45,14,30,120,60,-20,290)
    strategyList +:= new StrategyThree(10,0,12,45,14,45,150,70,-20,300)
    strategyList +:= new StrategyThree(10,30,13,15,14,30,270,50,-20,260)
    strategyList +:= new StrategyThree(10,0,12,45,14,45,190,90,-20,270)
    strategyList +:= new StrategyThree(10,15,12,45,14,30,120,60,-20,300)
    strategyList +:= new StrategyThree(10,0,12,45,14,45,190,90,-20,280)
    strategyList +:= new StrategyThree(10,30,13,15,14,30,270,70,-20,260)
    strategyList +:= new StrategyThree(10,0,12,45,14,45,190,90,-20,290)
    strategyList +:= new StrategyThree(10,15,12,45,14,30,120,70,-20,290)
    strategyList +:= new StrategyThree(10,0,12,45,14,45,190,90,-20,300)
    strategyList +:= new StrategyThree(10,15,12,45,14,30,120,70,-20,300)
    strategyList +:= new StrategyThree(10,30,13,15,14,45,250,70,-20,260)
    strategyList +:= new StrategyThree(10,30,13,15,14,30,270,70,-20,270)
    strategyList +:= new StrategyThree(10,30,13,15,14,45,250,70,-20,270)
    strategyList +:= new StrategyThree(10,30,13,15,14,45,260,70,-20,260)
    strategyList +:= new StrategyThree(10,30,13,15,14,45,260,70,-20,270)
    strategyList +:= new StrategyThree(10,15,12,45,14,45,120,60,-20,290)
    strategyList +:= new StrategyThree(10,30,13,15,14,45,270,50,-20,260)
    strategyList +:= new StrategyThree(10,15,12,45,14,45,120,60,-20,300)
    strategyList +:= new StrategyThree(10,30,13,15,14,45,270,70,-20,260)
    strategyList +:= new StrategyThree(10,15,12,45,14,45,120,70,-20,290)
    strategyList +:= new StrategyThree(10,15,12,45,14,45,120,70,-20,300)
    strategyList +:= new StrategyThree(10,30,13,15,14,45,270,70,-20,270)





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
