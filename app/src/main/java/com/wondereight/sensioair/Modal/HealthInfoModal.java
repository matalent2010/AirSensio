package com.wondereight.sensioair.Modal;


/**
 * Created by Miguel on 3/21/2016.
 */
public class HealthInfoModal {

    boolean mConscious;
    boolean mAllergies;
//    boolean mEyes;
//    boolean mNose;
//    boolean mLungs;
    String strSpecify;          //String
    boolean mRespiratory;
    String strAnotherspecify;          //String
    boolean mPet;

    public HealthInfoModal(){
        mConscious = false;
        mAllergies = false;
//        mEyes = false;
//        mNose = false;
//        mLungs = false;
        strSpecify = "";
        mRespiratory = false;
        strAnotherspecify = "";
        mPet = false;
    }
    public void firstInit()
    {
        mConscious = false;
        mRespiratory = false;
        mPet = false;
    }

    public boolean getConscious()
    {
        return mConscious;
    }

    public void setConscious(boolean conscious)
    {
        mConscious = conscious;
    }
    public void setConscious(String conscious)
    {
        mConscious = conscious.equalsIgnoreCase("1") ? true : false;
    }

    public boolean getAllergies()
    {
        return mAllergies;
    }

    public void setAllergies(boolean allergies)
    {
        mAllergies = allergies;
    }
    public void setAllergies(String allergies)
    {
        mAllergies = allergies.equalsIgnoreCase("1") ? true : false;
    }

//    public boolean getEyes()
//    {
//        return mEyes;
//    }
//
//    public void setEyes(boolean eyes)
//    {
//        mEyes = eyes;
//    }
//    public void setEyes(String eyes)
//    {
//        mEyes = eyes.equalsIgnoreCase("1") ? true : false;
//    }
//
//    public boolean getNose()
//    {
//        return mNose;
//    }
//
//    public void setNose(boolean Nose)
//    {
//        mNose = Nose;
//    }
//    public void setNose(String Nose)
//    {
//        mNose = Nose.equalsIgnoreCase("1") ? true : false;
//    }
//
//    public boolean getLungs()
//    {
//        return mLungs;
//    }
//
//    public void setLungs(boolean Lungs)
//    {
//        mLungs = Lungs;
//    }
//    public void setLungs(String Lungs)
//    {
//        mLungs = Lungs.equalsIgnoreCase("1") ? true : false;
//    }

    public String getSpecify()
    {
        return strSpecify;
    }

    public void setSpecify(String Specify)
    {
        strSpecify = Specify;
    }

    public boolean getRespiratory()
    {
        return mRespiratory;
    }

    public void setRespiratory(boolean Respiratory)
    {
        mRespiratory = Respiratory;
    }
    public void setRespiratory(String Respiratory)
    {
        mRespiratory = Respiratory.equalsIgnoreCase("1") ? true : false;
    }

    public String getAnotherspecify()
    {
        return strAnotherspecify;
    }

    public void setAnotherspecify(String Anotherspecify)
    {
        strAnotherspecify = Anotherspecify;
    }

    public boolean getPet()
    {
        return mPet;
    }

    public void setPet(boolean Pet)
    {
        mPet = Pet;
    }
    public void setPet(String Pet)
    {
        mPet = Pet.equalsIgnoreCase("1") ? true : false;
    }


}
