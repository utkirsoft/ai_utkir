package uz.department.uai.integration.notarial.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

// So'rovlar uchun DTO'lar
public final class NotarialRequestDTOs {

    // --- Search Ban Request DTOs ---
    @Data @Builder
    public static class SearchBanRequest {
        @JsonProperty("request_id") private long requestId;
        @JsonProperty("request_date") private String requestDate; // "YYYY-MM-DD"
        private Declarant declarant;
        private Member member; // Agar member bo'yicha qidirilsa
        private Subject subject; // Agar subject bo'yicha qidirilsa
    }

    @Data @Builder
    public static class Declarant {
        @JsonProperty("company_inn") private String companyInn;
        private String mfo;
        @JsonProperty("company_name") private String companyName;
        @JsonProperty("representative_inn") private String representativeInn;
        @JsonProperty("representative_fio") private String representativeFio;
    }

    @Data @Builder
    public static class Member {
        private int type; // 1: Jismoniy, 2: Yuridik
        private String inn;
        private String pin;
        @JsonProperty("pass_serial") private String passSerial;
        @JsonProperty("pass_num") private String passNum;
    }

    @Data @Builder
    public static class Subject {
        private int type; // Taqiq qo'yilayotgan obyekt turi
        @JsonProperty("cadastre_num") private String cadastreNum;
        @JsonProperty("state_num") private String stateNum; // Avto raqami
        @JsonProperty("engine_num") private String engineNum;
        @JsonProperty("body_num") private String bodyNum;
        @JsonProperty("chassis_num") private String chassisNum;
    }

    // --- Add Ban Request DTOs ---
    @Data @Builder
    public static class AddBanRequest {
        private Statement statement;
        private List<AddBanMember> members;
        private AddBanSubject subject;
    }

    @Data @Builder
    public static class Statement {
        @JsonProperty("outer_id") private Long outerId;
        @JsonProperty("doc_type") private Integer docType;
        @JsonProperty("doc_num") private String docNum;
        @JsonProperty("doc_date") private String docDate; // "YYYY-MM-DD"
        @JsonProperty("org_type") private Integer orgType;
        @JsonProperty("org_name") private String orgName;
        @JsonProperty("org_post") private Integer orgPost;
        @JsonProperty("org_fio") private String orgFio;
        @JsonProperty("base_document") private String baseDocument; // Base64 encoded file
        @JsonProperty("ban_edate") private String banEdate; // Taqiq tugash sanasi (ixtiyoriy)
    }

    @Data @Builder
    public static class AddBanMember {
        private int type; // 1: Jismoniy, 2: Yuridik
        private String inn;
        private String district;
        private String address;
        private String country; // "860"
        private String name; // Yuridik shaxs nomi
        @JsonProperty("first_name") private String firstName; // Jismoniy shaxs ismi
        @JsonProperty("sur_name") private String surName; // Jismoniy shaxs familiyasi
        @JsonProperty("last_name") private String lastName; // Jismoniy shaxs otasining ismi
        @JsonProperty("birth_date") private String birthDate; // "YYYY-MM-DD"
        private String pin;
        @JsonProperty("pass_type") private String passType;
        @JsonProperty("pass_serial") private String passSerial;
        @JsonProperty("pass_num") private String passNum;
        @JsonProperty("issue_org") private String issueOrg;
        @JsonProperty("issue_date") private String issueDate; // "YYYY-MM-DD"
    }

    @Data @Builder
    public static class AddBanSubject {
        private int type; // Mol-mulk turi
        private String description;
        @JsonProperty("cadastre_num") private String cadastreNum;
        private String district;
        private String street;
        private String home;
        private String flat;
        private String block;
        @JsonProperty("obj_name") private String objName;
        @JsonProperty("state_num") private String stateNum;
        @JsonProperty("engine_num") private String engineNum;
        @JsonProperty("body_num") private String bodyNum;
        @JsonProperty("chassis_num") private String chassisNum;
        private String mark;
        @JsonProperty("year_create") private String yearCreate;
        private String color;
        @JsonProperty("tech_serial") private String techSerial;
        @JsonProperty("tech_num") private String techNum;
        @JsonProperty("tech_date") private String techDate; // "YYYY-MM-DD"
        @JsonProperty("tech_issue") private String techIssue;
        @JsonProperty("vehicle_id") private String vehicleId;
    }

    // --- Cancel Ban Request DTOs ---
    @Data @Builder
    public static class CancelBanRequest {
        @JsonProperty("reg_num") private String regNum; // Bekor qilinayotgan taqiq raqami
        private Statement statement; // AddBanRequest'dagi Statement'ga o'xshash
        private Declarant declarant; // SearchBanRequest'dagi Declarant'ga o'xshash
    }

    // --- Search Actions Request DTO ---
    @Data @Builder
    public static class SearchActionsRequest {
        private long requestid;
        private String inn;
        private String pin;
        private String cadastrenum;
        private String autoregnum;
        private String technum;
        private String techserial;
    }
}