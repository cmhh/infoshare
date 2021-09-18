package org.cmhh

import org.openqa.selenium.{WebDriver, WebElement}
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import java.util.concurrent.TimeUnit
import java.io.{File, BufferedWriter, FileWriter}
import scala.io.Source
import scala.util.{Try, Success, Failure}

object infoshare {
  def makeSch(ids: String, outputFile: String): Try[String] = 
    makeSch(ids.split(",").toList, outputFile)

  def makeSch(ids: Seq[String], outputFile: String): Try[String] = Try {
    val bw = new BufferedWriter(new FileWriter(new File(outputFile)))
    bw.write(ids.mkString("\n"))
    bw.close()
    outputFile
  }

  def fetch(inputSch: String, outputFile: String): Try[String] = Try {
    val sch = new File(inputSch)
    val out = new File(outputFile)
    val wrk = new File(".")
    val tmp = new File("./ExportDirect.csv")

    if (out.exists()) out.delete()
    if (tmp.exists()) tmp.delete()

    val options: ChromeOptions = new ChromeOptions()
    options.addArguments("--headless")
    options.addArguments("--no-sandbox")
    options.addArguments("--disable-extensions")

    val prefs = new java.util.HashMap[String, Any]()
    prefs.put("download.prompt_for_download", false)
    prefs.put("download.defaut_directory", wrk.getAbsolutePath())

    options
      .setExperimentalOption(
        "prefs", 
        prefs
      )

    val driver: ChromeDriver = new ChromeDriver(options)

    driver.get("http://infoshare.stats.govt.nz/infoshare/ExportDirect.aspx")
    driver.manage().timeouts().implicitlyWait (10, TimeUnit.SECONDS)
    driver.manage().window().maximize()

    // choose file
    driver
      .findElementById("ctl00_MainContent_fuSearchFile")
      .sendKeys(sch.getAbsolutePath())

    // select all time periods
    driver
      .findElementById("ctl00_MainContent_TimeVariableSelector_lblSelectAll")
      .click()

    // download
    driver
      .findElementById("ctl00_MainContent_btnGenerate")
      .click()

    Thread.sleep(5000)

    // add 'date' field to header
    // remove trailing commas on each row
    // remove trailing whitespace and metadata
    val lines = Source
      .fromFile(tmp)
      .getLines()
      .toList
      .filter(line => line != "" && line != "Source: Statistics New Zealand")
      .map(line => {
        val newl = if (line.takeRight(1) == ",") line.dropRight(1) else line
        if (newl.head == ',') s"date${newl}" else newl 
      })

    // write to file
    val bw = new BufferedWriter(new FileWriter(out))
    bw.write(lines.mkString("\n"))
    bw.close()

    tmp.delete()

    outputFile
  }
}

object FetchSch extends App {
  infoshare.fetch(args(0), args(1)) match {
    case Success(f) => println(s"Created $f successfully.")
    case Failure(e) => println(s"Failed to create output.\n${e.getMessage()}")
  }
}

object FetchIds extends App {
  val sch = infoshare.makeSch(
    args(0), 
    File.createTempFile("infoshare", ".sch").getAbsolutePath()
  )

  sch match {
    case Success(f) => infoshare.fetch(f, args(1)) match {
      case Success(g) => println(s"Created $g successfully.")
      case Failure(e) => println(s"Failed to create output.\n${e.getMessage()}")
    }
    case Failure(e) => println(s"Failed to create temp file.")
  }
}
