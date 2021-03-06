package com.example.studygroups;

import android.net.Uri;

import java.io.Serializable;
import java.util.ArrayList;

public class UserProfile implements Serializable {

    String username;
    String display_name;
    String major;
    String profilePicture;
    ArrayList<String> classes;
    ArrayList<String> groups;


    public UserProfile() {
        this.username = "";
    }

    public UserProfile(String username, String display_name) {
        this.username = username;
        this.display_name = display_name;
        this.profilePicture = null;
        this.classes = new ArrayList<String>();
        this.groups = new ArrayList<String>();
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

    public String getUsername(){
        return this.username;
    }

    public void addGroup(Group g){
        this.groups.add(g.getName());
    }
}
