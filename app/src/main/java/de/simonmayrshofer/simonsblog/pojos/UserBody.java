package de.simonmayrshofer.simonsblog.pojos;


public class UserBody {

    public UserBody(String email, String password){
        user = new User();
        user.email = email;
        user.password = password;
    }

    public User user;

}