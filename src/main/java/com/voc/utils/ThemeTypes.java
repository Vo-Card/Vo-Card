package com.voc.utils;

public enum ThemeTypes {
    Card("Card"),
    Deck("Deck");

    private String valueString;

    private ThemeTypes(String value){
        this.valueString = value;
    }

    public String getValue(){
        return this.valueString;
    }
}
