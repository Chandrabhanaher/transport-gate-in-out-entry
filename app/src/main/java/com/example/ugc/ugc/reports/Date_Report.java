package com.example.ugc.ugc.reports;

class Date_Report {

    private String VEHICLENO;
    private String SUPPLIERNAME;
    private String INTIME;
    private String OUTTIME;
    private String custname;

    public void setVEHICLENO(String VEHICLENO) {
        this.VEHICLENO = VEHICLENO;
    }

    public void setSUPPLIERNAME(String SUPPLIERNAME) {
        this.SUPPLIERNAME = SUPPLIERNAME;
    }

    public void setINTIME(String INTIME) {
        this.INTIME = INTIME;
    }

    public void setOUTTIME(String OUTTIME) {
        this.OUTTIME = OUTTIME;
    }

    public String getSUPPLIERNAME() {
        return SUPPLIERNAME;
    }

    public String getINTIME() {
        return INTIME;
    }

    public String getOUTTIME() {
        return OUTTIME;
    }

    public String getVEHICLENO() {
        return VEHICLENO;
    }

    public void setCustname(String custname) {
        this.custname = custname;
    }

    public String getCustname() {
        return custname;
    }
}
