package com.sneakerstore.backend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterDTO {
    @JsonProperty("fullname")
    private String fullName;

    @JsonProperty("username") // Quan trọng: Phải có trường này
    private String username;

    @JsonProperty("password")
    private String password;

    @JsonProperty("retype_password")
    private String retypePassword;

    @JsonProperty("date_of_birth")
    private Date dateOfBirth;

    @JsonProperty("phone_number") // Vẫn giữ để lưu vào DB (nếu muốn)
    private String phoneNumber;
    
    @JsonProperty("address")
    private String address;

    @JsonProperty("facebook_account_id")
    private int facebookAccountId;

    @JsonProperty("google_account_id")
    private int googleAccountId;
}