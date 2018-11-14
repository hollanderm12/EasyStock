package com.mtlcollege.quickstocks.model;

public class StockInfo {

    private String symbol;
    private String securityName;
    private String securityType;
    private String currencyType;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getSecurityName() { return securityName; }

    public void setSecurityName(String securityName) { this.securityName = securityName; }

    public String getSecurityType() {
        return securityType;
    }

    public void setSecurityType(String securityType) {
        this.securityType = securityType;
    }

    public String getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(String currencyType) {
        this.currencyType = currencyType;
    }

    public StockInfo() {
    }
}
