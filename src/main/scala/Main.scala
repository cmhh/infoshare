package org.cmhh

import org.openqa.selenium.{WebDriver, WebElement}
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import java.util.concurrent.TimeUnit
import java.io.File
import java.nio.file._

object Main extends App {
  val sch = new File(args(0))
  val out = new File(args(1))
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

  Files.copy(
    Paths.get(tmp.getAbsolutePath()), 
    Paths.get(out.getAbsolutePath()), 
    StandardCopyOption.REPLACE_EXISTING
  )

  tmp.delete()

  if (out.exists()) {
    println("\n\nFile created successfully.\n\n")
  } else {
    println("\n\nFuckity.\n\n")
  }
}
