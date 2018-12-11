package com.example.joohj.animaltime;

public class Calendar {

    public String calendar_no;
    public String user_id;
    public String calendar_date;
    public String calendar_time;
    public String calendar_content;
    public String alarm_check;

    public Calendar(String calendar_no, String user_id, String calendar_date, String calendar_time, String calendar_content, String alarm_check){
        this.calendar_no = calendar_no;
        this.user_id = user_id;
        this.calendar_date = calendar_date;
        this.calendar_time = calendar_time;
        this.calendar_content = calendar_content;
        this.alarm_check = alarm_check;
    }
}
