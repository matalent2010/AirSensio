package com.wondereight.sensioair.Modal;

/**
 * Created by Miguel on 02/23/2016.
 */
public class UserModal {

    private String id = "";
    private String firstname = "";
    private String lastname = "";
    private String email = "";
    private String gender = "";
    private String phone_number = "";
    private String push_notifications = "";
    private String notification_threshold_allergens = "5";
    private String notification_threshold_pollution = "5";
    private String birthday = "";
    private String newsletter = "";
    private String session_nbr = "";
    private String password = "";
    private Boolean isLogouted = false;

//    private String geolocation = "";
//    private String cityname = "";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone_number;
    }

    public void setPhone(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getNotification() {
        return push_notifications;
    }

    public void setNotification(String notification) {
        this.push_notifications = push_notifications;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getNewsletter() {
        return newsletter;
    }

    public void setNewsletter(String newsletter) {
        this.newsletter = newsletter;
    }

    public String getSession_nbr() {
        return session_nbr;
    }

    public void setSession_nbr(String session_nbr) {
        this.session_nbr = session_nbr;
    }

    public String getThresholdAllergens() {
        return notification_threshold_allergens;
    }

    public void setThresholdAllergens(String notification_threshold_allergens) {
        this.notification_threshold_allergens = notification_threshold_allergens;
    }

    public String getThresholdPollution() {
        return notification_threshold_pollution;
    }

    public void setThresholdPollution(String notification_threshold_pollution) {
        this.notification_threshold_pollution = notification_threshold_pollution;
    }

    public Boolean isLogoutedUser() {
        return isLogouted;
    }
    public void setLogouted( Boolean logouted ){
        this.isLogouted = logouted;
    }
//    public String getGeolocation() {
//        return geolocation;
//    }
//
//    public void setGeolocation(String geolocation) {
//        this.geolocation = geolocation;
//    }
//
//    public String getCityname() {
//        return cityname;
//    }
//
//    public void setCityname(String cityname) {
//        this.cityname = cityname;
//    }

}

