package com.example.studygroups;

import java.util.ArrayList;

public class UserProfile {

    String username;
    String display_name;
    String major;
    ArrayList<String> classes;
    ArrayList<String> groups;


    public UserProfile(){}
    public UserProfile(String username, String display_name, String major){
        this.username = username;
        this.display_name = display_name;
        this.major = major;
        classes = new ArrayList<String>();
        groups = new ArrayList<String>();
    }
    public UserProfile(String username, String display_name, String major,ArrayList<String> classes){
        this.username = username;
        this.display_name = display_name;
        this.major = major;
        this.classes = new ArrayList<String>();
        for(int i = 0; i < classes.size(); i++){
            this.classes.add(i, classes.get(i));
        }
        groups = new ArrayList<String>();
    }
}
