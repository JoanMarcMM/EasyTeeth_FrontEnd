package navigation

import com.example.easyteeth.model.Appointment

object Routes {
    const val HOME = "home"
    const val LOGIN = "login"

    const val SHOW_PATIENTS = "show_patients"

    const val APPOINTMENT_SEARCHER = "appointment_searcher"

    const val CALENDAR = "calendar"
    const val PATIENTS_APPOINTMENT = "patients_appointment"

    const val NEW_PATIENT_SCREEN = "newPatientScreen"
    const val NEW_BACKGROUND_SCREEN = "newPatientBackground/{patientId}"

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

    const val RADIOGRAPHYS = "radiographys"
    const val FIRST_APPOINTMENT = "first_appointment"
}