name := "backtest"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.scalikejdbc"      %% "scalikejdbc"        % "2.3.5",
  "ch.qos.logback"       %  "logback-classic"    % "1.1.3",
  "com.github.tototoshi" %% "scala-csv" % "1.3.0",
  "mysql" % "mysql-connector-java" % "6.0.3"
)

//seq(flywaySettings: _*)

//flywayUrl := "jdbc:h2:file:./db/225mini"
//flywayUser := "user"
//flywayPassword := "pass"
//"org.flywaydb" % "flyway-sbt" % "4.0"


flywayUrl := "jdbc:mysql://localhost:3306/backtest?serverTimezone=JST"
flywayUser := "hons"
flywayPassword := "hons"
