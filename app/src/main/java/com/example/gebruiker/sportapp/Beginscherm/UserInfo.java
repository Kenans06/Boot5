package com.example.gebruiker.sportapp.Beginscherm;

/**
 * Created by Gebruiker on 16-5-2017.
 */

public class UserInfo {

    public String username, email, soortAccount;

    public UserInfo(String username, String email, String soortAccount) {
        this.username = username;
        this.email = email;
        this.soortAccount = soortAccount;
    }

    public UserInfo(String email, String soortAccount){
        this.email= email;
        this.soortAccount = soortAccount;
    }

    public UserInfo(String soortAccount){
        this.soortAccount = soortAccount;
    }


}


