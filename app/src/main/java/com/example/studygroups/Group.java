package com.example.studygroups;

import android.widget.EditText;

import java.io.Serializable;
import java.util.ArrayList;

public class Group implements Serializable {
    String name;
    String type;
    String department;
    String course_no;
    String prof;
    boolean active;
    String key;
    String creator;
    ArrayList<String> group_member = new ArrayList<>();
    ArrayList<String> pending_invitations = new ArrayList<>();

    public Group(){}

    public Group(String name, String type, String department, String course_no, String prof, String creator){
        this.name = name;
        this.type = type;
        this.department = department;
        this.course_no = course_no;
        this.prof = prof;
        this.key = "";
        this.creator = creator;
        group_member.add(creator);
    }

    public String getType() {
        return type;
    }

    public String getDepartment() {
        return department;
    }

    public String getCourse_no() {
        return course_no;
    }

    public String getProf() {
        return prof;
    }

    public String getName(){
        return name;
    }

    public String getKey(){return key;}

    public void setKey(final String key){this.key = key;}

    public String toString()
    {
        return this.name + "\n" + department + course_no + "-" +prof;
    }

    public void request2join(String user){
        pending_invitations.add(user);
    }

    public void addGroupMember(String user){group_member.add(user);
    pending_invitations.remove(user);}

    public void removePendingInvitation(String user){pending_invitations.remove(user);}


    }
