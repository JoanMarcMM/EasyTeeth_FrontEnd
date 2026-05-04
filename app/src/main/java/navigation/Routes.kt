package navigation

import com.example.easyteeth.model.Appointment

object  Routes {
    const val HOME = "home"
    const val LOGIN = "login"
    const val PROFILE = "profile"

    const val SHOW_PATIENTS = "show_patients"

    const val APPOINTMENT_SEARCHER = "appointment_searcher"
    const val DELETE_APPOINTMENT = "delete_appointment"

    const val UPDATE_APPOINTMENT = "update_appointment"

    const val CALENDAR = "calendar"
    const val PATIENTS_APPOINTMENT = "patients_appointment"

    const val NEW_PATIENT_SCREEN = "newPatientScreen"
    const val NEW_BACKGROUND_SCREEN = "newPatientBackground/{patientId}?patientData={patientData}"

    const val PATIENT_LIST_TO_PROFILE = "patientListToProfile"

    const val TREATMENTS = "treatments"
    const val PATIENTSLIST = "patientsList"
    const val PATIENT_MENU_SCREEN = "patientMenuScreen"
    const val APPOINTMENT_MENU_SCREEN = "appointmentMenuScreen"
    const val AGENDA = "agenda"
    const val SELECT_PATIENT = "select_patient"
    const val PATIENT_PROFILE_SCREEN = "patient_profile/{patientId}"
    const val UPDATE_PATIENT_SCREEN = "updatePatient/{patientId}"
    const val UPDATE_BACKGROUND_SCREEN = "updateBackground/{patientId}"

    const val ODONTOGRAM_SCREEN = "odontogram/{patientId}"

    const val PATIENT_IMAGES = "patientImages"
    const val PATIENT_DOCUMENTS = "patientDocuments"
    const val TOOTH_DETAIL_SCREEN = "toothDetail/{patientId}/{toothId}"
    const val ADD_APPOINTMENT_DETAILS = "add_appointment_details/{patientId}"
    fun addAppointmentDetails(id: Long) = "add_appointment_details/$id"

    const val SELECT_APPOINTMENT_SHIFT = "select_appointment_shift/{patientId}/{treatmentId}/{odontologistId}/{motive}"
    fun selectAppointmentShift(patientId: Long, treatmentId: Long, odontologistId: Long, motive: String) =
        "select_appointment_shift/$patientId/$treatmentId/$odontologistId/${motive.replace("/", "_").replace(" ", "+")}"

    const val SELECT_BOXES = "select_boxes/{patientId}/{treatmentId}/{odontologistId}/{motive}/{shift}"
    fun selectBoxes(patientId: Long, treatmentId: Long, odontologistId: Long, motive: String, shift: String) =
        "select_boxes/$patientId/$treatmentId/$odontologistId/${motive.replace("/", "_").replace(" ", "+")}/$shift"

    const val SELECT_AVAILABLE_SLOTS = "select_available_slots/{patientId}/{treatmentId}/{odontologistId}/{motive}/{shift}/{boxId}"
    fun selectAvailableSlots(patientId: Long, treatmentId: Long, odontologistId: Long, motive: String, shift: String, boxId: Long) =
        "select_available_slots/$patientId/$treatmentId/$odontologistId/${motive.replace("/", "_").replace(" ", "+")}/$shift/$boxId"

    const val SELECT_APPOINTMENT_DATETIME = "select_appointment_datetime/{patientId}/{treatmentId}/{odontologistId}/{boxId}/{motive}"
    fun selectAppointmentDateTime(patientId: Long, treatmentId: Long, odontologistId: Long, boxId: Long, motive: String) =
        "select_appointment_datetime/$patientId/$treatmentId/$odontologistId/$boxId/${motive.replace("/", "_").replace(" ", "+")}"

    const val RADIOGRAPHYS = "radiographys"
    const val FIRST_APPOINTMENT = "first_appointment"
    const val BOXES = "boxes"
    const val BOX_CALENDAR_SCREEN = "boxCalendar/{boxId}/{numBox}"

    const val STORAGE_AND_ORDERS_MENU = "storage_and_orders_menu"

    const val STORAGE_LIST = "storage_list"
    const val STORAGE_DETAIL = "storage_detail/{storageId}"

    const val UTENSIL_LIST = "utensil_list"
    const val UTENSIL_ORDER_SELECTION = "utensil_order_selection/{storageId}"
    const val ORDER_REVIEW = "order_review/{storageId}"
    const val BOX_ORDER_REVIEW = "box_order_review/{boxId}/{dateMillis}"
    const val ORDERS_LIST = "orders_list"
    const val ORDER_DETAIL = "order_detail/{orderId}"

    const val UTENSILS_AND_SUPPLIERS_MENU = "utensils_and_suppliers_menu"
    const val UTENSIL_LIST_MANAGEMENT = "utensil_list_management"
    const val UTENSIL_EDIT = "utensil_edit/{utensilId}"
    const val SUPPLIER_LIST = "supplier_list"
    const val SUPPLIER_EDIT = "supplier_edit/{supplierId}"
    const val SUPPLIER_CREATE = "supplier_create"
}