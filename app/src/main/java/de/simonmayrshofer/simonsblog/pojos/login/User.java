package de.simonmayrshofer.simonsblog.pojos.login;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("email")
    public String email;
    @SerializedName("password")
    public String password;

}