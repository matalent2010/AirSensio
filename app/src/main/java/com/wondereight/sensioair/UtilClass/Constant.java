package com.wondereight.sensioair.UtilClass;

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

    public static final String DEFAULT_CITYNAME = "London";
    public static final String DEFAULT_CITYID = "1";

    public static final String DEFAULT_ALLERGY_INDEX = "0";
    public static final String DEFAULT_ALLERGY_INTENSITY = "";
    public static final String DEFAULT_POLLUTION_INDEX = "0";
    public static final String DEFAULT_POLLUTION_INTENSITY = "";

    public static final String DEFAULT_ADVICE= "";
    public static final String DEFAULT_PASSWORD = "SensioAirApp";

    //Login Mode
    public static final String LM_LOCAL = "0";
    public static final String LM_FACEBOOK = "1";
    public static final String LM_LINKEDIN = "2";
    public static final String LM_TWITTER = "3";

// ApiRequestParams Constant
    //login_secret
    public static final String LOGIN_SECTRET= "3sfs0a65905075158146c9710e41qdf2";

    //Login Page
    public static final String STR_LOGIN_MODE = "login_mode";  // "0" : local, "1" : facebook, "2" : twitter, "3" : linkedin
    public static final String STR_EMAIL = "email";
    public static final String STR_PASSWORD = "password";
    public static final String STR_DEVICEID = "device_id";
    public static final String STR_HASH = "hash";
    public static final String STR_NOTIFICATION = "push_notifications";
    public static final String STR_NBR = "session_nbr";
    public static final String STR_THRESHOLD_ALLERGENS = "notification_threshold_allergens";
    public static final String STR_THRESHOLD_POLLUTION = "notification_threshold_pollution";
    public static final String STR_SOCIAL_ID = "social_id";
    public static final String STR_SOCIAL_TOKEN = "token";
    public static final String STR_SECRET_TOKEN = "token_secret";

    //Signup Page
    public static final String STR_FIRSTNAME = "firstname";
    public static final String STR_LASTNAME = "lastname";
    public static final String STR_PHONE = "phone_number";
    public static final String STR_GENDER = "gender";
    public static final String STR_BIRTHDAY = "date_of_birth";
    public static final String STR_GEOLOCATION = "geolocation";
    public static final String STR_NEWSLETTER = "newsletter";
    public static final String STR_CITYFLAG = "city_not_exists";

    //statistics page
    public static final String STR_USERID = "user_id";
    public static final String STR_CITYID = "city_id";
    public static final String STR_DURATION = "statistics_duration";

    public static final String STR_PARAMETER = "parameter_name";
    public static final String STR_VALUE = "log_value";
    public static final String STR_DATE = "log_date";
    public static final String STR_TIME = "log_time";

    //Save Info Page (Tell use your health)
    public static final String STR_CONSCIOUS = "health_conscious";
    public static final String STR_HAVEALLERGIES = "have_allergies";
    public static final String STR_EYES = "eyes";
    public static final String STR_NOSE = "nose";
    public static final String STR_LUNGS = "lungs";
    public static final String STR_ALLERGIESOTHER = "allergies_other";
    public static final String STR_RES_DISTRESS = "respiratory_distress";
    public static final String STR_RES_OTHER = "respiratory_other";
    public static final String STR_PET = "pet";

    //Saving Settings page
    public static final String STR_NOTI_ALLERGENS = "notification_allergens";
    public static final String STR_NOTI_POLLUTION = "notification_pollution";

    public static final String STR_ENVID = "env_parameter_id";
    public static final String STR_ENVVALUE = "env_value";
    public static final String STR_LOGDATETIME = "log_datetime";

    //Symptom page
    public static final String STR_LOCATION = "user_current_location";
    public static final String STR_DATETIME = "current_datetime";
    public static final String STR_SYMPTOM_1 = "symptom_1";
    public static final String STR_SYMPTOM_2 = "symptom_2";
    public static final String STR_SYMPTOM_3 = "symptom_3";
    public static final String STR_SYMPTOM_4 = "symptom_4";
    public static final String STR_SYMPTOM_5 = "symptom_5";
    public static final String STR_SYMPTOM_6 = "symptom_6";

    //LogOutbreak page
    public static final String STR_ADVICE = "advice_text";
    public static final String STR_ALLERGYINDEX = "allergy_index";
    public static final String STR_POLLUTIONINDEX = "pollution_index";
    public static final String STR_LOGINTENSITY = "log_intensity";

    public static final String STR_NUM_WEEK = "week";
    public static final String STR_LABEL = "label";

    public static final String STR_CITYNAME = "name";
    public static final String STR_CITY = "id";


    // Send Feedback page
    public static final String STR_FEEDBACK = "feedback";

    public static final String STR_SENDPUSHID = "push_notifications_id";

    // Sensio page
    public static final String URL_Wlab = "http://www.wlab.io/";

    //Push notification Message
    public static final String NOTI_MESSAGE = "message";
}
