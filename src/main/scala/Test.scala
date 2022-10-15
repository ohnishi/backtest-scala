import java.io.PrintWriter

import org.joda.time.{DateTime, LocalDateTime}

/**
 * Created by ohnishi on 2015/12/13.
 */
object Test {

  def main(args: Array[String]) {


    val cpuCoreCnt = collection.parallel.availableProcessors
    println(s"CPUコア数 = $cpuCoreCnt")


    //CPU コア数表示
    //VMオプション  -Dscala.concurrent.context.maxThreads=2
    val cpuCoreSize = List.tabulate(100000)(identity).par.map(_ => Thread.currentThread.getId).distinct.size
    println(s"論理CPUコア数 = $cpuCoreSize")


    //(1 to 100).par foreach(x => print(x + " "))

//    for (rieki <- (10 to 50).par if rieki % 10 == 0) {
//      for (songiri <- (10 to 10) if songiri % 10 == 0) {
//        print(rieki + "," + songiri + " ")
//      }
//    }

//    val reportFile = new PrintWriter("./report/" + DateTime.now.toString("yyyy-MM-dd-HH-mm-ss") + ".txt")
//    reportFile.println("aaa")
//    reportFile.println("bb")
//    reportFile.close


    val a = DateTime.now
    val b = a.plusMonths(12)
    println(a)
    println(b)


    val highTime = new LocalDateTime(2016,5,1,10,10,0,0)
    val lowTime = new LocalDateTime(2016,5,1,10,30,0,0)

    println(highTime.compareTo(lowTime))
  }

}
