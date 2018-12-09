package com.example.joohj.animaltime;

public class petListViewItem {
    private String petIDStr;
    private String nameStr ;
    private String sexStr ;
    private String ageStr ;
    private String weightStr ;

    public void setPetID(String petID) {
        petIDStr = petID ;
    }
    public void setName(String name) {
        nameStr = name ;
    }
    public void setSex(String sex) {
        sexStr = sex ;
    }
    public void setAge(String age) {
        ageStr = age ;
    }
    public void setWeight(String weight) {
        weightStr = weight ;
    }


    public String getpetID() {
        return this.petIDStr ;
    }
    public String getName() {
        return this.nameStr ;
    }
    public String getSex() {
        return this.sexStr ;
    }
    public String getAge() {
        return this.ageStr ;
    }
    public String getWeight() {
        return this.weightStr ;
    }



}
