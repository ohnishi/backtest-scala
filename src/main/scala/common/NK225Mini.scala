package common


import org.joda.time._
import scalikejdbc._

case class NK225Mini(trdDate: LocalDate, trdTime: LocalTime, mkDate: LocalDate, price: Int, priceType: PriceType.Value, vol: Int, no: Int) {

    def mkDateTime : LocalDateTime = {
      new LocalDateTime(
        mkDate.getYear,
        mkDate.getMonthOfYear,
        mkDate.getDayOfMonth,
        trdTime.getHourOfDay,
        trdTime.getMinuteOfHour,
        trdTime.getSecondOfMinute,
        trdTime.getMillisOfSecond)
    }
}

object NK225Mini extends SQLSyntaxSupport[NK225Mini] {
  override val tableName = "NK225MINI"
  def apply(rs: WrappedResultSet): NK225Mini = new NK225Mini(
    rs.jodaLocalDate("trd_date"),
    rs.jodaLocalTime("trd_time"),
    rs.jodaLocalDate("mk_date"),
    rs.int("price"),
    PriceType.withName(rs.string("price_type")),
    rs.int("vol"),
    rs.int("no"))

}

object OrderTyep extends Enumeration {
  val BUY = Value("買い") //始値
  val SELL = Value("売り") //終値
}

case class KesaiInfo(trdDate: LocalDate, inTIme: LocalTime, outTime: LocalTime, orderPrice: Int, kesaiPrice: Int, saeki: Int, orderType: OrderTyep.Value)

case class OrderInfo(index: Int, rieki: Int = 0, son: Int = 0)

object PriceType extends Enumeration {
  val O = Value("O") //始値
  val E = Value("E") //終値
  val P = Value("P") //始値 = 終値
  val N = Value("N") //中値（ザラ場中の約定）
}
