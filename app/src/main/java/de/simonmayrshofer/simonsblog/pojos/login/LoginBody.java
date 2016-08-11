package de.simonmayrshofer.simonsblog.pojos.login;

public class LoginBody {

    public LoginBody(String email, String password){
        user = new User();
        user.email = email;
        user.password = password;
    }

    public User user;

}