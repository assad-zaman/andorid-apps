package com.assad.complaintapp;

public class ComplaintType {
    int complaintid;
    String  complaintname;

    public ComplaintType(){
    }

    public ComplaintType(int complaintid, String complaintname) {
        this.complaintid = complaintid;
        this.complaintname = complaintname;
    }

    public int getComplaintid(){
        return complaintid;
    }

    public String getComplaintname(){
        return complaintname;
    }

    public void setComplaintid(int complaintid){
        this.complaintid = complaintid;
    }

    public void setComplaintname(String complaintname){
        this.complaintname = complaintname;
    }

}
