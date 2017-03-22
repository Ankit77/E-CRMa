package com.symphony_ecrm.model;

/**
 * Created by Admin on 18-06-2016.
 */
public class CustomerListModel {

//    private String name;
//    private String address;
//    private String city;
//    private String id;


    private String id;
    private String customername;
    private String address;
    private String contact;
    private String town;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomername() {
        return customername;
    }

    public void setCustomername(String customername) {
        this.customername = customername;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }
}
