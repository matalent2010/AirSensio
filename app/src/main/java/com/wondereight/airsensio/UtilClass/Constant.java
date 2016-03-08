package com.wondereight.airsensio.UtilClass;

/**
 * Created by Miguel on 02/22/2016.
 */
public class Constant {

    /*
     * Post Parameter Type Constants
	 */

     // Request Key (Connection/Socket timeout keyword)
    public static final int CONNECTION_TIMEOUT_EXCEPTION = 5000;
    public static final int SOCKET_TIMEOUT_EXCEPTION = 5001;
    public static final int NO_INTERNET = 5002;
    public static final int GENERAL_ERROR = 4000;
    public static final int AGREEMENT_UPDATED = 600;

    public static final int SELECT_FILE = 1;
    public static final int REQUEST_CAMERA = 2;
    public static final int IMAGE_CROPPED = 3;

    public static String DOMAIN = "https://www.oigente.com/app/";

    public static enum URL {

        LOGIN("login.php"),
        SIGNUP("signup.php"),

        VIEW_PROFILE("viewprofile.php"),
        POST_ADS("postads.php"),
        GET_ADS("getads.php"),
        GET_ADS_BY_CATEGORY("getads_by_category.php"),
        FORGOT_PASSWORD("forgotpassword.php"),
        GET_COUNTRY("getcountry.php"),
        UPDATE_ACCOUNT("updateaccount.php"),
        POST_CONTACT("contact.php");

        private final String name;

    // Constants using in App
    private URL(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        return (otherName == null) ? false : name.equals(otherName);
    }

    public String toString() {
        return name;
    }

}

public static final String DEFAULT_CITYNAME = "London";

// ApiRequestParams Constant
    //login_secret
    public static String LOGIN_SECTRET= "3sfs0a65905075158146c9710e41qdf2";

    //Login Page
    public static String STR_EMAIL = "email";
    public static String STR_PASSWORD = "password";
    public static String STR_DEVICEID = "device_id";
    public static String STR_HASH = "hash";
    public static String STR_NOTIFICATION = "push_notifications";
    public static String STR_NBR = "session_nbr";

    //Signup Page
    public static String STR_FIRSTNAME = "firstname";
    public static String STR_LASTNAME = "lastname";
    public static String STR_PHONE = "phone_number";
    public static String STR_GENDER = "gender";
    public static String STR_BIRTHDAY = "date_of_birth";
    public static String STR_GEOLOCATION = "geolocation";
    public static String STR_NEWSLETTER = "newsletter";

    //statistics page
    public static String STR_USERID = "user_id";    //must confirm "id" | "user_id" ?
    public static String STR_CITYID = "city_id";
    public static String STR_DURATION = "statistics_duration";

    public static String STR_PARAMETER = "parameter_name";
    public static String STR_VALUE = "log_value";
    public static String STR_DATE = "log_date";
    public static String STR_TIME = "log_time";

    //public static String STR_HASH = "hash";
    //public static String STR_DEVICEID = "device_id";

    //Save Info Page (Tell use your health)
    public static String STR_CONSCIOUS = "health_conscious";
    public static String STR_HAVEALLERGIES = "have_allergies";
    public static String STR_EYES = "eyes";
    public static String STR_NOSE = "nose";
    public static String STR_LUNGS = "lungs";
    public static String STR_ALLERGIESOTHER = "allergies_other";
    public static String STR_RES_DISTRESS = "respiratory_distress";
    public static String STR_RES_OTHER = "respiratory_other";
    public static String STR_PET = "pet";

    //Saving Settings page
    public static String STR_NOTI_ALLERGENS = "notification_allergens";
    public static String STR_NOTI_POLLUTION = "notification_pollution";

    public static String STR_ENVID = "env_parameter_id";
    public static String STR_ENVVALUE = "env_value";
    public static String STR_LOGDATETIME = "log_datetime";

    //Symptom page
    public static String STR_LOCATION = "user_current_location ";
    public static String STR_SYMPTOM_1 = "symptom_1";
    public static String STR_SYMPTOM_2 = "symptom_2";
    public static String STR_SYMPTOM_3 = "symptom_3";
    public static String STR_SYMPTOM_4 = "symptom_4";
    public static String STR_SYMPTOM_5 = "symptom_5";
    public static String STR_SYMPTOM_6 = "symptom_6";
}
