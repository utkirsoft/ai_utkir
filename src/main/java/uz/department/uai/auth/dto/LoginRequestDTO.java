package uz.department.uai.auth.dto;

import lombok.Data;

@Data
public class LoginRequestDTO {
    private String login;
    private String password;
}