package com.example.alexcalle.beaconbooking3;

/**
 * Created by Alex on 10/19/2016.
 */

public class RegisterViewModel {

        String userName;
        String email;
        int departmentId;
        String password;
        String confirmPassword;

        public RegisterViewModel(String userName, String email, int departmentId, String password, String confirmPassword)
        {
            this.userName = userName;
            this.email = email;
            this.departmentId = departmentId;
            this.password = password;
            this.confirmPassword = confirmPassword;
        }
    }
