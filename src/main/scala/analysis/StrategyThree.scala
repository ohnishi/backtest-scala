package analysis

import common._
import org.joda.time.{DateTimeConstants, LocalTime, Minutes}

import scala.util.control.Breaks


class StrategyThree(inHour: Int, inMintue: Int, outHour: Int, outMintue: Int, endHour: Int, endMintue: Int, rieki: Int, son: Int, hosei: Int, max: Int) {

  val IN_HOUR = inHour
  val IN_MINTUE = inMintue
  val OUT_HOUR = outHour
  val OUT_MINTUE = outMintue
  val END_HOUR = endHour
  val END_MINTUE = endMintue
  val RIEKI = rieki
  val SON = son
  val HOSEI = hosei
  val MAX = max

  var isInvalid = false
  var totalRieki = 0L
  var totalSon = 0L

  def params: String = {
    s"$IN_HOUR,$IN_MINTUE,$OUT_HOUR,$OUT_MINTUE,$END_HOUR,$END_MINTUE,$RIEKI,$SON,$HOSEI,$MAX"
  }

  def getTradeHourMinute(tradeTime: LocalTime): (Int, Int) = {

    val hour = tradeTime.getHourOfDay
    (if (hour <= 5) hour + 24 else hour, tradeTime.getMinuteOfHour)
  }

  def isTradeTime(hour: Int, mintue: Int): Boolean = {

    if ((hour > IN_HOUR && hour < OUT_HOUR) ||
      (hour == IN_HOUR && mintue >= IN_MINTUE) ||
      (hour == OUT_HOUR && mintue < OUT_MINTUE)) {
      true
    } else {
      false
    }
  }

  def getRiekiSon(high: Int, low: Int): (Int, Int) = {
    var rieki = (high - low) * RIEKI / 100
    var son = (high - low) * SON / 100
    rieki -= rieki % 5
    son -= son % 5
    (rieki, son)
  }

  def exec(priceList: List[NK225Mini] ): (Int, Int, Int) = {

    var buyOrderIndexs = Seq.empty[OrderInfo]
    var sellOrderIndexs = Seq.empty[OrderInfo]

    var isHighTouch, isLowTouch = false
    var pointHigh, pointLow = 0

    for ((price: NK225Mini, i: Int) <- priceList.zipWithIndex) {

      if (price.priceType == PriceType.O) {
        isHighTouch = false
        isLowTouch = false
        pointHigh = Integer.MIN_VALUE
        pointLow = Integer.MAX_VALUE
      }

      if (isHighTouch == false || isLowTouch == false) {

        val (hour, minute) = getTradeHourMinute(price.trdTime)

        if (isTradeTime(hour, minute)) {
          if (pointHigh < price.price) {
            pointHigh = price.price
          }else if (pointLow > price.price) {
            pointLow = price.price
          }
        } else if (hour > OUT_HOUR || (hour == OUT_HOUR && minute >= OUT_MINTUE)) {

          if(hour < END_HOUR || (hour == END_HOUR && minute < END_MINTUE)) {

            if (pointHigh - pointLow <= MAX){
              if (isHighTouch == false && pointHigh + HOSEI < price.price) {
                val (rieki, son) = getRiekiSon(pointHigh, pointLow)
                //              if(isLowTouch) sellOrderIndexs +:= new OrderInfo(i, rieki, son)
                if(!isLowTouch) buyOrderIndexs +:= new OrderInfo(i, rieki, son)
                isHighTouch = true
              } else if (isLowTouch == false && pointLow - HOSEI > price.price) {
                val (rieki, son) = getRiekiSon(pointHigh, pointLow)
                //              if(isHighTouch) buyOrderIndexs +:= new OrderInfo(i, rieki, son)
                if(!isHighTouch) sellOrderIndexs +:= new OrderInfo(i, rieki, son)
                isLowTouch = true
              }
            } else {
              isHighTouch = true
              isLowTouch = true
            }
          }
        }
      }
    }

    val b = new Breaks

    //検証
    var kesaiList = Seq.empty[Int]
    for (buyOrder <- buyOrderIndexs) {
      val order = priceList(buyOrder.index)
      b.breakable {
        for (price <- priceList.drop(buyOrder.index + 1)) {
          if (order.price + buyOrder.rieki < price.price) {
            kesaiList +:= buyOrder.rieki
            b.break
          } else if (order.price - buyOrder.son > price.price) {
            kesaiList +:= price.price - order.price
            b.break
          } else if (price.priceType == PriceType.E) {
            kesaiList +:= price.price - order.price
            b.break
          }
        }
      }
    }
    for (sellOrder <- sellOrderIndexs) {
      val order = priceList(sellOrder.index)
      b.breakable {
        for (price <- priceList.drop(sellOrder.index + 1)) {
          if (order.price - sellOrder.rieki > price.price) {
            kesaiList +:= sellOrder.rieki
            b.break
          } else if (order.price + sellOrder.son < price.price) {
            kesaiList +:= order.price - price.price
            b.break
          } else if (price.priceType == PriceType.E) {
            kesaiList +:= order.price - price.price
            b.break
          }
        }
      }
    }

    var rieki, son = 0
    for (saeki <- kesaiList) {
      if (saeki > 0) {
        rieki += saeki
      } else {
        son -= saeki
      }
    }
    (rieki, son, kesaiList.size)
  }
}
