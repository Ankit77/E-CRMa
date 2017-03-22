package com.symphony_ecrm.model;

/**
 * Created by Ankit on 6/19/2016.
 */
public class CRMModel {
    private int crmId;
    private String cusId;
    private String companyname;
    private String ConttactPerson;
    private String location;
    private String discussion;
    private String purpose;
    private String purposeId;

    private String nextaction;
    private String nextactionId;
    private String actiondate;
    private String checkInLat;
    private String checkInLong;
    private String checkInTimeStemp;
    private String checkInImagePath;
    private String checkOutLat;
    private String checkOutLong;
    private String checkOutTimeStemp;
    private String checkOutImagePath;
    private int checkStatus;
    private int checkFlag;
    private int isCompleteVisit;
    private int isSendtoServer;
    private String referenceVisitId;

    public int getIsSendtoServer() {
        return isSendtoServer;
    }

    public void setIsSendtoServer(int isSendtoServer) {
        this.isSendtoServer = isSendtoServer;
    }

    public String getPurposeId() {
        return purposeId;
    }

    public void setPurposeId(String purposeId) {
        this.purposeId = purposeId;
    }

    public String getNextactionId() {
        return nextactionId;
    }

    public void setNextactionId(String nextactionId) {
        this.nextactionId = nextactionId;
    }

    public int getCrmId() {
        return crmId;
    }

    public void setCrmId(int crmId) {
        this.crmId = crmId;
    }

    public String getCusId() {
        return cusId;
    }

    public void setCusId(String cusId) {
        this.cusId = cusId;
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

    public String getDiscussion() {
        return discussion;
    }

    public void setDiscussion(String discussion) {
        this.discussion = discussion;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getNextaction() {
        return nextaction;
    }

    public void setNextaction(String nextaction) {
        this.nextaction = nextaction;
    }

    public String getActiondate() {
        return actiondate;
    }

    public void setActiondate(String actiondate) {
        this.actiondate = actiondate;
    }

    public String getCheckInLat() {
        return checkInLat;
    }

    public void setCheckInLat(String checkInLat) {
        this.checkInLat = checkInLat;
    }

    public String getCheckInLong() {
        return checkInLong;
    }

    public void setCheckInLong(String checkInLong) {
        this.checkInLong = checkInLong;
    }

    public String getCheckInTimeStemp() {
        return checkInTimeStemp;
    }

    public void setCheckInTimeStemp(String checkInTimeStemp) {
        this.checkInTimeStemp = checkInTimeStemp;
    }

    public String getCheckInImagePath() {
        return checkInImagePath;
    }

    public void setCheckInImagePath(String checkInImagePath) {
        this.checkInImagePath = checkInImagePath;
    }

    public String getCheckOutLat() {
        return checkOutLat;
    }

    public void setCheckOutLat(String checkOutLat) {
        this.checkOutLat = checkOutLat;
    }

    public String getCheckOutLong() {
        return checkOutLong;
    }

    public void setCheckOutLong(String checkOutLong) {
        this.checkOutLong = checkOutLong;
    }

    public String getCheckOutTimeStemp() {
        return checkOutTimeStemp;
    }

    public void setCheckOutTimeStemp(String checkOutTimeStemp) {
        this.checkOutTimeStemp = checkOutTimeStemp;
    }

    public String getCheckOutImagePath() {
        return checkOutImagePath;
    }

    public void setCheckOutImagePath(String checkOutImagePath) {
        this.checkOutImagePath = checkOutImagePath;
    }

    public int getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(int checkStatus) {
        this.checkStatus = checkStatus;
    }

    public int getCheckFlag() {
        return checkFlag;
    }

    public void setCheckFlag(int checkFlag) {
        this.checkFlag = checkFlag;
    }

    public String getConttactPerson() {
        return ConttactPerson;
    }

    public void setConttactPerson(String conttactPerson) {
        ConttactPerson = conttactPerson;
    }

    public int getIsCompleteVisit() {
        return isCompleteVisit;
    }

    public void setIsCompleteVisit(int isCompleteVisit) {
        this.isCompleteVisit = isCompleteVisit;
    }

    public String getReferenceVisitId() {
        return referenceVisitId;
    }

    public void setReferenceVisitId(String referenceVisitId) {
        this.referenceVisitId = referenceVisitId;
    }
}
