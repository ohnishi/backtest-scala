package analysis

import common.NK225Mini
import org.joda.time._
import org.joda.time.format.DateTimeFormat
import scalikejdbc._

object AnalyzerMonthlyOne extends App {

  Class.forName("com.mysql.cj.jdbc.Driver")
  ConnectionPool.singleton("jdbc:mysql://192.168.100.11:3306/backtest?serverTimezone=JST&useSSL=false", "hons", "hons")

  val startTime = DateTime.now

  var toDate = DateTimeFormat.forPattern("yyyy-MM-dd").parseLocalDate("2017-02-01")
  var fromDate = toDate.minusMonths(1)
  val fromTime = new LocalTime(8, 45, 0, 0)
  val toTime = new LocalTime(15, 15, 0, 0)
  val mintueHind = 15

  var strategyList = Seq.empty[StrategyOne]

  //  for (inHour <- Seq(9, 10)) {
  //    for (inMintue <- (0 to 57) if inMintue % mintueHind == 0) {
  //      for (outHour <- Seq(12, 13) if inHour < outHour) {
  //        for (outMintue <- (0 to 57) if outMintue % mintueHind == 0) {
  //          for (endHour <- Seq(14, 15)) {
  //            for (endMintue <- (0 to 57) if endMintue % mintueHind == 0) {
  //              for (closingHour <- Seq(14, 15)) {
  //                for (closingMintue <- (0 to 57) if closingMintue % 5 == 0) {
  //                  for (rieki <- (50 to 300) if rieki % 10 == 0) {
  //                    for (son <- (50 to 300) if son % 10 == 0 && son <= rieki) {
  //                      for (hosei <- -30 to 30 if hosei % 10 == 0) {
  //                        for (max <- 200 to 400 if max % 10 == 0) {
  //                          if (endHour != 15 || endMintue <= 15) {
  //                            if (closingHour != 15 || closingMintue <= 15) {
  //                              if (endHour < closingHour || (endHour == closingHour && endMintue <= closingMintue)) {
  //                                strategyList +:= new StrategyOne(inHour, inMintue, outHour, outMintue, endHour, endMintue, closingHour, closingMintue, rieki, son, hosei, max)
  //                              }
  //                            }
  //                          }
  //                        }
  //                      }
  //                    }
  //                  }
  //                }
  //              }
  //            }
  //          }
  //        }
  //      }
  //    }
  //  }

//  for (endMintue <- (30 to 57) if endMintue % 5 == 0) {
//    for (closingHour <- Seq(14, 15)) {
//      for (closingMintue <- (0 to 57) if closingMintue % 5 == 0) {
//        if ((closingHour == 14 && closingMintue >= 30 && endMintue <= closingMintue) || (closingHour == 15 && closingMintue <= 15)) {
//          strategyList +:= new StrategyOne(10, 0, 12, 45, 14, endMintue, closingHour, closingMintue, 190, 90, -20, 280)
//        }
//      }
//    }
//  }
  strategyList +:= new StrategyOne(10, 0, 12, 45, 14, 30, 14, 45, 190, 90, -20, 280)

  for (plusPoint <- 1 to 25) {
    val priceList: List[NK225Mini] = DB readOnly {
      implicit session =>
        sql"""select * from NK225MINI
           where trd_date >= ${
          fromDate
        } and trd_date < ${
          toDate
        }
           and trd_time >= ${
          fromTime
        } and trd_time <= ${
          toTime
        }
           order by trd_date, no""".map(rs => NK225Mini(rs)).list.apply()
    }

    for (strategy <- strategyList.par) {
      if (strategy.isInvalid == false) {

        val (totalRieki, totalSon, tradeCount) = strategy.exec(priceList)
        val cost = tradeCount * 42
        val allRieki = ((totalRieki - totalSon) * 100) - cost
        val monthPf = (totalRieki * 100 / (if (totalSon == 0) 1 else totalSon))
        strategy.totalRieki += totalRieki
        strategy.totalSon += totalSon
        val totalPF = (strategy.totalRieki * 100 / (if (strategy.totalSon == 0) 1 else strategy.totalSon))

        if ( totalPF < 75) {
          strategy.isInvalid = true
        } else {
          val params = strategy.params
          val monthPF = monthPf / 100d
          val viewTotalPF = totalPF / 100d
          val month = fromDate.toString("yyyy-MM")
          val outLine = s"$monthPF,$tradeCount,$allRieki,$params,$viewTotalPF,$month"

          println(outLine)
        }
      }

    }

    fromDate = fromDate.minusMonths(1)
    toDate = toDate.minusMonths(1)
  }

  val endTime = DateTime.now
  val procDay = Days.daysBetween(startTime, endTime).getDays
  val procHour = Hours.hoursBetween(startTime, endTime).getHours % 24
  val procMinute = Minutes.minutesBetween(startTime, endTime).getMinutes % 60
  val procSecond = Seconds.secondsBetween(startTime, endTime).getSeconds % 60
  println("開始時間 = " + startTime.toString("yyyy-MM-dd HH:mm:ss"))
  println("終了時間 = " + endTime.toString("yyyy-MM-dd HH:mm:ss"))
  println(s"処理時間 = $procDay 日 $procHour 時 $procMinute 分 $procSecond 秒")
}
