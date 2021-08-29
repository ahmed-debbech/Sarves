package com.debbech.sarves;

import android.util.Log;

import androidx.annotation.Nullable;

public class Contact {
    private String name;
    private String number;

    public Contact(String name, String number){
        this.setName(name);
        this.setNumber(number);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        boolean gg = false;
        if(obj instanceof Contact) {
            Contact c = (Contact) obj;
            String hh = c.getNumber().replaceAll("[^0-9]", "");
            String kk = this.number.replaceAll("[^0-9]", "");
            if(kk.compareTo(hh) == 0){
                gg = true;
            }else{
                gg = false;
            }
        }
        return gg;
    }

    public void setNumber(String number) {
        this.number = number;
    }

}
