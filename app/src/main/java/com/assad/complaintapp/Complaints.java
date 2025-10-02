package com.assad.complaintapp;



import java.sql.Date;

public class Complaints {
    private int id;
    private Date creationdate;
    private String factoryname;
    private String groupname;
    private String typename;
    private String subtypename;
    private String detail;
    private String remarks;
    private String cardno;
    private String name;
    private String designation;
    private String status;


    public Complaints(int id, Date creationdate,  String factoryname,  String groupname, String typename,
                      String subtypename, String detail, String remarks, String cardno, String name, String designation,
                      String status
                      ) {
        this.id = id;
        this.creationdate = creationdate;
        this.factoryname = factoryname;
        this.groupname = groupname;
        this.typename = typename;
        this.subtypename = subtypename;
        this.detail = detail;
        this.remarks = remarks;
        this.cardno = cardno;
        this.name = name;
        this.designation = designation;
        this.status = status;



    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public Date getCreationdate() {
        return creationdate;
    }
    public void setCreationdate(Date creationdate) {
        this.creationdate = creationdate;
    }

    public String getFactoryname() {
        return factoryname;
    }
    public void setFactoryname(String factoryname) {
        this.factoryname = factoryname;
    }

    public String getGroupname() {
        return groupname;
    }
    public void setGroupname(String typename) {
        this.typename = typename;
    }

    public String getTypename() {
        return typename;
    }
    public void setType(String typename) {
        this.typename = typename;
    }

    public String getSubtypename() {
        return subtypename;
    }
    public void setSubtypename(String subtypename) {
        this.subtypename = subtypename;
    }

    public String getDetail() {
        return detail;
    }
    public void setDetail(String remarks) {
        this.detail = detail;
    }

    public String getRemarks() {
        return remarks;
    }
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getCardno(){ return cardno;  }
    public void setCardno(String cardno) {this.cardno= cardno;}

    public String getName() {   return name;   }
    public void setName(String name) {      this.name = name;    }

    public String getDesignation() {       return designation;    }
    public void setDesignation(String designation) {        this.designation = designation;    }

    public String getStatus() {  return status;    }
    public void setStatus(String status) {        this.status = status;    }


}
