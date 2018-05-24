package com.example.johnnyseo.swpj;

public class Listviewitem {
    private String rowNum;
    private String trainLineNm;
    private String subwayId;
    private String arvlMsg2;
    private String btrainNo;

    public String getRowNum() {
        return rowNum;
    }

    public void setRowNum(String rowNum) {
        this.rowNum = rowNum;
    }

    public String getTrainLineNm() {
        return trainLineNm;
    }

    public void setTrainLineNm(String trainLineNm) {
        this.trainLineNm = trainLineNm;
    }

    public String getSubwayId() {
        return subwayId;
    }

    public void setSubwayId(String subwayId) {
        this.subwayId = subwayId;
    }

    public String getArvlMsg2() {
        return arvlMsg2;
    }

    public void setArvlMsg2(String arvlMsg2) {
        this.arvlMsg2 = arvlMsg2;
    }

    public String getBtrainNo() {
        return btrainNo;
    }

    public void setBtrainNo(String btrainNo) {
        this.btrainNo = btrainNo;
    }





    public Listviewitem(String rowNum, String trainLineNm, String subwayId, String arvlMsg2, String btrainNo){
        this.rowNum = rowNum;
        this.trainLineNm = trainLineNm;
        this.subwayId=subwayId;
        this.arvlMsg2 = arvlMsg2;
        this.btrainNo = btrainNo;
    }
}