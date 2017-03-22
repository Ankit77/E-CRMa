package com.symphony_ecrm.model;

/**
 * Created by Ankit on 6/19/2016.
 */
public class CompanyModel {
    private int companyid;
    private String companyname;
    private String location;

    public CompanyModel(int companyid, String companyname, String location) {
        this.companyid = companyid;
        this.companyname = companyname;
        this.location = location;
    }

    public int getCompanyid() {
        return companyid;
    }

    public void setCompanyid(int companyid) {
        this.companyid = companyid;
    }

    public String getCompanyname() {
        return companyname;
    }

    public void setCompanyname(String companyname) {
        this.companyname = companyname;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
