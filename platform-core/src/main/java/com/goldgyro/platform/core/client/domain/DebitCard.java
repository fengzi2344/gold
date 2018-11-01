package com.goldgyro.platform.core.client.domain;

public class DebitCard {
    private String id;
    private String bankAccountNo;
    private String bankName;
    private String bankUnitNo;
    private String openCardId;
    private String Phoneno;
    private String cvn2;
    private String expired;

    public DebitCard(String id, String bankAccountNo, String bankName, String bankUnitNo, String openCardId, String phoneno) {
        this.id = id;
        this.bankAccountNo = bankAccountNo;
        this.bankName = bankName;
        this.bankUnitNo = bankUnitNo;
        this.openCardId = openCardId;
        Phoneno = phoneno;
    }
    public DebitCard(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBankAccountNo() {
        return bankAccountNo;
    }

    public void setBankAccountNo(String bankAccountNo) {
        this.bankAccountNo = bankAccountNo;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankUnitNo() {
        return bankUnitNo;
    }

    public void setBankUnitNo(String bankUnitNo) {
        this.bankUnitNo = bankUnitNo;
    }

    public String getOpenCardId() {
        return openCardId;
    }

    public void setOpenCardId(String openCardId) {
        this.openCardId = openCardId;
    }

    public String getPhoneno() {
        return Phoneno;
    }

    public void setPhoneno(String phoneno) {
        Phoneno = phoneno;
    }
}
