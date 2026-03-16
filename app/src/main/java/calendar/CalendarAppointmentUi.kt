package calendar

import java.time.LocalDate
import java.time.LocalDateTime

data class CalendarAppointmentUi(
    val id: Long,
    val motive: String,
    val start: LocalDateTime,
    val end: LocalDateTime,
    val durationMinutes: Int,
    val day: LocalDate,
    val patientFullName: String,
    val treatmentName: String,
    val odontologistFullName: String,
    val boxNum: Int
)