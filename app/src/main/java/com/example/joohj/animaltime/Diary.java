package com.example.joohj.animaltime;

public class Diary {
    public String diary_id;
    public String user_id;
    public String title;
    public String context;
    public String date;
    public String url;

    public Diary(String diary_id, String user_id, String title, String context, String date, String url){
        this.diary_id = diary_id;
        this.user_id = user_id;
        this.title = title;
        this.context = context;
        this.date = date;
        this.url = url;
    }
}
