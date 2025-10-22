package uz.department.uai.integration.notarial.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

// Javoblar uchun DTO'lar
public final class NotarialResponseDTOs {

    // --- Search Actions Response DTO (notarius.json asosida) ---
    @Data @NoArgsConstructor
    public static class SearchActionsResponse {
        private long responseid;
        private int resultcode;
        private String resultnote;
        private List<NotarialAction> data;
    }

    @Data @NoArgsConstructor
    public static class NotarialAction {
        private String regdate;
        private String regnum;
        private String office;
        private String notarycode;
        private String notaryfio;
        private Integer typeid;
        private String type;
        private Integer stateid;
        private String state;
        private Integer relative;
        private Double sum;
        private Double totalsum;
        private List<NotarialMember> members;
        private List<NotarialSubject> subjects;
        private String validdate;
        private String datebegin;
        // ... json'dagi boshqa maydonlar kerak bo'lsa qo'shiladi
    }

    @Data @NoArgsConstructor
    public static class NotarialMember {
        private Integer classid; // 1: Jismoniy, 2: Yuridik
        private Integer typeid;
        private String type;
        private String surname;
        private String name;
        private String lastname;
        private Integer gender;
        private String birthdate;
        private String inn;
        private String pin;
        private String passserial;
        private String passnum;
        private String country;
        private String region;
        private String district;
        private String address;
        // ... json'dagi boshqa maydonlar kerak bo'lsa qo'shiladi
    }

    @Data @NoArgsConstructor
    public static class NotarialSubject {
        private Integer typeid;
        private String type;
        private String cadastrenum;
        private String region;
        private String district;
        private String address;
        private Double quadrature;
        private String mark; // Avto markasi
        private String regnum; // Avto raqami
        private Integer yearcreate; // Avto yili
        private String color;
        private String techserial;
        private String technum;
        private String techissue;
        private String techdate;
        private Double price;
        // ... json'dagi boshqa maydonlar kerak bo'lsa qo'shiladi
    }

    // --- Ban Service uchun javob DTO'lari (API dokumentatsiyasiga qarab yaratiladi) ---
    // Misol uchun SearchBanResponse
    @Data @NoArgsConstructor
    public static class SearchBanResponse {
        // ... API dokumentatsiyasiga mos maydonlar
    }

    @Data @NoArgsConstructor
    public static class AddBanResponse {
        // ... API dokumentatsiyasiga mos maydonlar
    }

    @Data @NoArgsConstructor
    public static class CancelBanResponse {
        // ... API dokumentatsiyasiga mos maydonlar
    }
}
