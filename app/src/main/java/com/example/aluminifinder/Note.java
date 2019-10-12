package com.example.aluminifinder;

public class Note {
    private String name;
    private String DocumentId;
    private String state_txt;
    private String district_txt;
    private String college_txt;
    private String name1;
    private String email;
    private String number;
    private String date;
    private String radio;
    private Double lat;
    private Double lng;
    private String date_txt;
    private String email_txt;
    private String time_txt;
    private String name_txt;
    private String loc_txt;
    private String remarks_txt;
    private String token_id;

    public Note() {

    }


    public Note(String college_txt, String date_txt, String email_txt, String time_txt, String name_txt, String loc_txt, String remarks_txt) {
        this.college_txt = college_txt;
        this.date_txt = date_txt;
        this.email_txt = email_txt;
        this.time_txt = time_txt;
        this.name_txt = name_txt;
        this.loc_txt = loc_txt;
        this.remarks_txt = remarks_txt;
    }

    public Note(String state_txt, String district_txt, String college_txt, String name1, String email, String number, String date, String radio) {
        this.state_txt = state_txt;
        this.district_txt = district_txt;
        this.college_txt = college_txt;
        this.name1 = name1;
        this.email = email;
        this.number = number;
        this.date = date;
        this.radio = radio;
    }

    public Note(String state_txt, String district_txt, String college_txt, String name1, String email, String number, String date, String radio, double lat, double lng) {
        this.state_txt = state_txt;
        this.district_txt = district_txt;
        this.college_txt = college_txt;
        this.name1 = name1;
        this.email = email;
        this.number = number;
        this.date = date;
        this.radio = radio;
        this.lat = lat;
        this.lng = lng;
    }

    public Note(String name_txt,String college_txt,String date_txt,String DocumentId) {
        this.college_txt = college_txt;
        this.name_txt = name_txt;
        this.date_txt = date_txt;
        this.DocumentId = DocumentId;
    }

    public Note(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDocumentId() {
        return DocumentId;
    }

    public void setDocumentId(String documentId) {
        DocumentId = documentId;
    }

    public String getState_txt() {
        return state_txt;
    }

    public void setState_txt(String state_txt) {
        this.state_txt = state_txt;
    }

    public String getDistrict_txt() {
        return district_txt;
    }

    public void setDistrict_txt(String district_txt) {
        this.district_txt = district_txt;
    }

    public String getCollege_txt() {
        return college_txt;
    }

    public void setCollege_txt(String college_txt) {
        this.college_txt = college_txt;
    }

    public String getName1() {
        return name1;
    }

    public void setName1(String name1) {
        this.name1 = name1;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRadio() {
        return radio;
    }

    public void setRadio(String radio) {
        this.radio = radio;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public String getDate_txt() {
        return date_txt;
    }

    public void setDate_txt(String date_txt) {
        this.date_txt = date_txt;
    }

    public String getEmail_txt() {
        return email_txt;
    }

    public void setEmail_txt(String email_txt) {
        this.email_txt = email_txt;
    }

    public String getTime_txt() {
        return time_txt;
    }

    public void setTime_txt(String time_txt) {
        this.time_txt = time_txt;
    }

    public String getName_txt() {
        return name_txt;
    }

    public void setName_txt(String name_txt) {
        this.name_txt = name_txt;
    }

    public String getLoc_txt() {
        return loc_txt;
    }

    public void setLoc_txt(String loc_txt) {
        this.loc_txt = loc_txt;
    }

    public String getRemarks_txt() {
        return remarks_txt;
    }

    public void setRemarks_txt(String remarks_txt) {
        this.remarks_txt = remarks_txt;
    }
}
