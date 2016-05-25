package com.wondereight.sensioair.Modal;

import org.json.JSONObject;

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
    private String threshold_allergens = "5";
    private String threshold_pollution = "5";
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
        return threshold_allergens;
    }

    public void setThresholdAllergens(String threshold_allergens) {
        this.threshold_allergens = threshold_allergens;
    }

    public String getThresholdPollution() {
        return threshold_pollution;
    }

    public void setThresholdPollution(String threshold_pollution) {
        this.threshold_pollution = threshold_pollution;
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

    public UserModal fromJson(JSONObject json){

        try{ id             = json.getString("id"); }           catch (Exception e) { id=""; }
        try{ firstname      = json.getString("firstname"); }    catch (Exception e) { firstname=""; }
        try{ lastname       = json.getString("lastname"); }     catch (Exception e) { lastname=""; }
        try{ email          = json.getString("email"); }        catch (Exception e) { email=""; }
        try{ gender         = json.getString("gender"); }       catch (Exception e) { gender=""; }
        try{ phone_number   = json.getString("phone_number"); } catch (Exception e) { phone_number=""; }
        try{ push_notifications     = json.getString("push_notifications"); }   catch (Exception e) { push_notifications=""; }
        try{ threshold_allergens    = json.getString("threshold_allergens"); }  catch (Exception e) { threshold_allergens="5"; }
        try{ threshold_pollution    = json.getString("threshold_pollution"); }  catch (Exception e) { threshold_pollution="5"; }
        try{ birthday       = json.getString("birthday"); }     catch (Exception e) { birthday=""; }
        try{ newsletter     = json.getString("newsletter"); }   catch (Exception e) { newsletter=""; }
        try{ session_nbr    = json.getString("session_nbr"); }  catch (Exception e) { session_nbr=""; }
        try{ password       = json.getString("password"); }     catch (Exception e) { password=""; }
        try{ isLogouted     = json.getBoolean("isLogouted"); }  catch (Exception e) { isLogouted=false; }

        return this;
    }

    public JSONObject toJson(){

        JSONObject result = new JSONObject();
        try{ result.put("id", id); }                catch (Exception ignore) { }
        try{ result.put("firstname", firstname); }  catch (Exception ignore) { }
        try{ result.put("lastname", lastname); }    catch (Exception ignore) { }
        try{ result.put("email", email); }          catch (Exception ignore) { }
        try{ result.put("gender", gender); }        catch (Exception ignore) { }
        try{ result.put("phone_number", phone_number); }                catch (Exception ignore) { }
        try{ result.put("push_notifications", push_notifications); }    catch (Exception ignore) { }
        try{ result.put("threshold_allergens", threshold_allergens); }  catch (Exception ignore) { }
        try{ result.put("threshold_pollution", threshold_pollution); }  catch (Exception ignore) { }
        try{ result.put("birthday", birthday); }        catch (Exception ignore) { }
        try{ result.put("newsletter", newsletter); }    catch (Exception ignore) { }
        try{ result.put("session_nbr", session_nbr); }  catch (Exception ignore) { }
        try{ result.put("password", password); }        catch (Exception ignore) { }
        try{ result.put("isLogouted", isLogouted); }    catch (Exception ignore) { }

        return result;
    }
}

