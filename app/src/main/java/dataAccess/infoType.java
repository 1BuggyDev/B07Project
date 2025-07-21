package dataAccess;

/**
 * Enumeration for the type of user data
 * ANSWER: an answer for the questionnaire
 *      Data: Pair<String>
 * Document: A document the user uploaded
 *      Data: Document
 * Contact: An emergency contact the user uploaded
 *      Data: EmergencyContact
 * Location: A safe location the user uploaded
 *      Data: SafeLocation
 * Medication: Medication info the user uploaded
 *      Data: Medication
 */
public enum infoType {
    ANSWER,
    DOCUMENT,
    CONTACT,
    LOCATION,
    MEDICATION
}
