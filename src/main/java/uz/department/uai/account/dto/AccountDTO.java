package uz.department.uai.account.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Data
@Builder // Obyektni qulay yaratish uchun
public class AccountDTO {

    // Foydalanuvchi asosiy ma'lumotlari
    private UUID id;
    private String login;
    private String firstName;
    private String lastName;
    private String fullName; // To'liq F.I.Sh.
    private String userPhotoUrl; // Kelajakda rasm uchun

    // Lavozim va tashkilot ma'lumotlari
    private String positionName;
    private String departmentName;
    private String branchName;

    // Eng muhim qismi: Foydalanuvchi huquqlari
    // Map<"Subject", Set<"Actions">> formatida
    // Misol: {"documents": ["READ", "CREATE"], "reports": ["READ"]}
    private Map<String, Set<String>> permissions;
}