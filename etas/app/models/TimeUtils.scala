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
  val Log = LoggerFactory getLogger getClass

  def isInvalidTripTime(doj: Long, timeZone: String) = {
    val dayName = getTitleForDate(timeZone, doj, "EN")
    val time = getTimeString(timeZone, doj)
    Log info dayName + ", " + time
    isWeekedDays(dayName) || time > "22" || time < "09"
  }

  def isWeekedDays(dayName: String) = dayName match {
    case "Saturday" | "Sunday" => true
    case _                     => false
  }

  def getTitleForDate(timeZone: String, time: Long, displayLangcode: String) = {
    val date = getCalendar(timeZone)
    date.setTimeInMillis(time)
    val locale = new Locale.Builder().setLanguage(displayLangcode).build()
    val dayName = date.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, locale).capitalize
    s"$dayName"
  }

  def getTimeString(timeZone: String, time: Long) = {
    val cal = Calendar.getInstance(TimeZone.getTimeZone(timeZone))
    cal.setTimeInMillis(time)
    val hourOfDay = cal.get(Calendar.HOUR_OF_DAY) //.toString
    val minute = cal.get(Calendar.MINUTE)
    val hour = if (minute > 0) (hourOfDay + 1).toString else hourOfDay.toString
    (if (hour.length == 1) "0" else "") + hour
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