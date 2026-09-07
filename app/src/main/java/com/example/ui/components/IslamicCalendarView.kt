package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.IslamicEvent
import com.example.prayer.CalendarDayItem
import com.example.prayer.HijriCalendarHelper
import com.example.ui.theme.*
import java.text.DateFormatSymbols
import java.util.*

@Composable
fun IslamicCalendarView(
  year: Int,
  month: Int,
  onMonthChanged: (Int, Int) -> Unit,
  modifier: Modifier = Modifier
) {
  val monthNames = remember { DateFormatSymbols(Locale("ms", "MY")).months }
  val daysOfWeek = listOf("Ahd", "Isn", "Sel", "Rab", "Kha", "Jum", "Sab")

  val calendarDays = remember(year, month) {
    HijriCalendarHelper.getMonthCalendarDays(year, month)
  }

  var selectedFilterMajorOnly by remember { mutableStateOf(false) }

  val filteredEvents = remember(selectedFilterMajorOnly) {
    if (selectedFilterMajorOnly) {
      HijriCalendarHelper.ISLAMIC_EVENTS.filter { it.isMajorHoliday }
    } else {
      HijriCalendarHelper.ISLAMIC_EVENTS
    }
  }

  // Find Hijri months spanned by current view
  val hijriSpan = remember(calendarDays) {
    val currentMonthDays = calendarDays.filter { it.isSelectedMonth }
    val firstHijri = currentMonthDays.firstOrNull()?.hijriMonthName ?: ""
    val lastHijri = currentMonthDays.lastOrNull()?.hijriMonthName ?: ""
    if (firstHijri == lastHijri || lastHijri.isEmpty()) firstHijri else "$firstHijri / $lastHijri"
  }

  LazyColumn(
    modifier = modifier
      .fillMaxWidth()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Top Month Navigation Card
    item {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth().testTag("calendar_month_nav")
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            IconButton(
              onClick = {
                val newMonth = if (month == 0) 11 else month - 1
                val newYear = if (month == 0) year - 1 else year
                onMonthChanged(newYear, newMonth)
              },
              modifier = Modifier.size(36.dp).clip(CircleShape).background(Emerald50)
            ) {
              Icon(Icons.Default.ChevronLeft, contentDescription = "Bulan Lepas", tint = Emerald800)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "${monthNames[month]} $year",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = Gold500.copy(alpha = 0.2f),
                modifier = Modifier.padding(top = 2.dp)
              ) {
                Text(
                  text = "🌙 $hijriSpan",
                  color = Gold700,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.SemiBold,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
              }
            }

            IconButton(
              onClick = {
                val newMonth = if (month == 11) 0 else month + 1
                val newYear = if (month == 11) year + 1 else year
                onMonthChanged(newYear, newMonth)
              },
              modifier = Modifier.size(36.dp).clip(CircleShape).background(Emerald50)
            ) {
              Icon(Icons.Default.ChevronRight, contentDescription = "Bulan Depan", tint = Emerald800)
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Day headers: Ahd Isn Sel Rab Kha Jum Sab
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            daysOfWeek.forEach { dayName ->
              Text(
                text = dayName,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (dayName == "Jum") Emerald700 else MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Calendar Grid: rows of 7 days
          val rows = calendarDays.chunked(7)
          rows.forEach { rowDays ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              rowDays.forEach { dayItem ->
                CalendarDayCell(
                  day = dayItem,
                  modifier = Modifier.weight(1f)
                )
              }
            }
          }
        }
      }
    }

    // Important Events Header and Filter
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Peristiwa Penting Sepanjang Tahun",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "Kalendar Hijrah & Takwim Islam",
            fontSize = 12.sp,
            color = Emerald700
          )
        }

        FilterChip(
          selected = selectedFilterMajorOnly,
          onClick = { selectedFilterMajorOnly = !selectedFilterMajorOnly },
          label = { Text("Hari Cuti Utama", fontSize = 11.sp) },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = Gold500.copy(alpha = 0.2f),
            selectedLabelColor = Gold800
          ),
          shape = RoundedCornerShape(12.dp)
        )
      }
    }

    // List of Islamic Events
    items(filteredEvents) { event ->
      IslamicEventCard(event = event)
    }

    item {
      Spacer(modifier = Modifier.height(20.dp))
    }
  }
}

@Composable
fun CalendarDayCell(
  day: CalendarDayItem,
  modifier: Modifier = Modifier
) {
  val isFriday = false // Highlight special prayer day

  val backgroundColor = when {
    day.isToday -> Emerald700
    day.eventTitle != null && day.isSelectedMonth -> Gold400.copy(alpha = 0.2f)
    else -> Color.Transparent
  }

  val textColor = when {
    day.isToday -> Color.White
    !day.isSelectedMonth -> Color(0xFF94A3B8).copy(alpha = 0.4f)
    else -> MaterialTheme.colorScheme.onSurface
  }

  val hijriColor = when {
    day.isToday -> Gold100
    !day.isSelectedMonth -> Color(0xFF94A3B8).copy(alpha = 0.4f)
    else -> Emerald800
  }

  Column(
    modifier = modifier
      .padding(2.dp)
      .clip(RoundedCornerShape(8.dp))
      .background(backgroundColor)
      .padding(vertical = 4.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text(
      text = "${day.gregorianDay}",
      fontSize = 13.sp,
      fontWeight = if (day.isToday) FontWeight.Bold else FontWeight.Medium,
      color = textColor
    )
    Text(
      text = "${day.hijriDay}",
      fontSize = 10.sp,
      fontWeight = FontWeight.SemiBold,
      color = hijriColor
    )
    if (day.eventTitle != null && day.isSelectedMonth) {
      Box(
        modifier = Modifier
          .size(4.dp)
          .clip(CircleShape)
          .background(if (day.isToday) Color.White else Gold600)
      )
    }
  }
}

@Composable
fun IslamicEventCard(event: IslamicEvent) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    border = if (event.isMajorHoliday) androidx.compose.foundation.BorderStroke(1.dp, Gold400.copy(alpha = 0.6f)) else null,
    modifier = Modifier.fillMaxWidth().testTag("islamic_event_${event.id}")
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.Top
    ) {
      // Date badge
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (event.isMajorHoliday) Gold500.copy(alpha = 0.2f) else Emerald100,
        modifier = Modifier.size(width = 65.dp, height = 65.dp)
      ) {
        Column(
          modifier = Modifier.fillMaxSize().padding(4.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          Text(
            text = "${event.hijriDay}",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = if (event.isMajorHoliday) Gold800 else Emerald900
          )
          Text(
            text = event.hijriDateStr.substringAfter(" "),
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (event.isMajorHoliday) Gold800 else Emerald800,
            textAlign = TextAlign.Center
          )
        }
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = event.title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
          )

          if (event.isMajorHoliday) {
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = Gold600
            ) {
              Text(
                text = "Hari Kebesaran",
                color = Color.White,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = "Anggaran Masihi: ${event.gregorianDateStr}",
          fontSize = 11.sp,
          fontWeight = FontWeight.Medium,
          color = Emerald700
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = event.description,
          fontSize = 12.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          lineHeight = 17.sp
        )
      }
    }
  }
}
