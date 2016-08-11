package de.simonmayrshofer.simonsblog.pojos;

import com.google.gson.annotations.SerializedName;

public class User {

//    Need Expose annotations?

    @SerializedName("id")
    public Integer id;
    @SerializedName("email")
    public String email;
    @SerializedName("created_at")
    public String createdAt;
    @SerializedName("updated_at")
    public String updatedAt;
    @SerializedName("provider")
    public Object provider;
    @SerializedName("uid")
    public Object uid;
    @SerializedName("authentication_token")
    public String authenticationToken;

}