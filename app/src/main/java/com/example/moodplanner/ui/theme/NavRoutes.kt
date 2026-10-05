
package com.example.moodplanner.ui.theme

object NavRoutes {
    const val CALENDAR = "calendar"
    const val DAY_MENU = "day_menu/{date}" // θα περνάμε την ημερομηνία σαν παραμετρο

    const val CREATE_EDIT_ENTRY = "create_edit_entry/{date}/{entryId}"
    /*
    Γιατί χρειαζόμαστε νέα διαδρομή:
Για να μπορούμε να πλοηγηθούμε από το DayMenuScreen στην οθόνη δημιουργίας/επεξεργασίας,
χρειαζόμαστε μια νέα διαδρομή που να δέχεται την ημερομηνία και το ID του entry (ή "new" για νέο)
     */

    const val SETTINGS = "settings"

}