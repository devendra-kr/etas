package models

import java.sql.Timestamp
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util._

import org.joda.time._
import org.joda.time.format._

import org.slf4j.LoggerFactory
import scala.util.Try
import java.util.concurrent.TimeUnit
import java.time
import java.time.ZonedDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale.Category

object TimeUtils {
  
  def isValidTripTime(doj: Long) = {
    val oneDay = 24 * 60 * 60L
    val days = (doj - System.currentTimeMillis) / oneDay
    val dayName = getTitleForDate("IST", days.toInt, "EN")
    val time = getTimeString("IST", doj)
    isWeekDays(dayName) && (time >= "22" || time <= "01")
  }
  
  def isWeekDays(dayName: String) = dayName match {
    case "Saturday" |"Sunday" => false
    case _ => true
  }
   
  def getTitleForDate(timeZone: String, days: Int, displayLangcode: String) = {
    val date = getCalendar(timeZone)
    date.add(Calendar.DATE, days)
    val locale = new Locale.Builder().setLanguage(displayLangcode).build()
    val dayName = date.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, locale).capitalize
    s"$dayName"
  }
  
  def getTimeString(timeZone: String, time: Long) = {
    val cal = Calendar.getInstance(TimeZone.getTimeZone(timeZone))
    cal.setTimeInMillis(time)
    val hourOfDay = cal.get(Calendar.HOUR_OF_DAY).toString
    (if (hourOfDay.length == 1) "0" else "") + hourOfDay
  }
  
  def getCalendar(timeZone: String, date: Date = new Date()) = {
    val calendar = Calendar.getInstance(TimeZone.getTimeZone(timeZone))
    calendar.setTime(date)
    calendar.set(Calendar.MILLISECOND, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar
  }
}