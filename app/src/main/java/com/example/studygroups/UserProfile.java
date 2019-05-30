package com.example.studygroups;

import android.net.Uri;

import java.util.ArrayList;

public class UserProfile {

    String username;
    String display_name;
    String major;
    String profilePicture;
    ArrayList<String> classes;
    ArrayList<String> groups;


    public UserProfile() {
    }

    public UserProfile(String username, String display_name, String major) {
        this.username = username;
        this.display_name = display_name;
        this.major = major;
        this.profilePicture = null;
        classes = new ArrayList<String>();
        groups = new ArrayList<String>();
    }

    public UserProfile(String username, String display_name, String major, ArrayList<String> classes) {
        this.username = username;
        this.display_name = display_name;
        this.major = major;
        this.profilePicture = null;
        this.classes = new ArrayList<String>();
        for (int i = 0; i < classes.size(); i++) {
            this.classes.add(i, classes.get(i));
        }
        groups = new ArrayList<String>();
    }
    public UserProfile(String username, String display_name, String major,String profilePicture,ArrayList<String> classes){
        this.username = username;
        this.display_name = display_name;
        this.major = major;
        this.profilePicture = profilePicture;
        this.classes = new ArrayList<String>();
        for(int i = 0; i < classes.size(); i++){
            this.classes.add(i, classes.get(i));
        }
        groups = new ArrayList<String>();
    }
}
