package com.example.easyteeth.model

data class AvailableSlotResponse(
    val date: String,
    val dayOfWeek: String,
    val timeSlots: List<TimeSlotResponse>
)

data class TimeSlotResponse(
    val period: String,
    val startTime: String,
    val endTime: String,
    val appointmentSlots: List<AppointmentSlotResponse> = emptyList()
)

data class AppointmentSlotResponse(
    val slotStart: String,
    val slotEnd: String,
    val available: Boolean
)
