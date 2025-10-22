package uz.department.uai.integration.mip.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

// Barcha DTO'larni bitta faylda saqlashimiz mumkin
public final class MipRequestDTOs {
    public record NarcologicalRequest(String pinpp) {}
    public record PsychologicalRequest(String pinpp) {}
    public record SelfEmploymentRequest(String pinfl) {}
    public record TaxPersonRequest(String pinfl) {}
    public record TaxPersonByPassportRequest(String pasSer, String pasNum) {}
    public record PersonSearchRequest(long transaction_id, String is_consent, String sender_pinfl, int langId, String document, String pinpp, String birth_date, String is_photo, String Sender) {}
    public record PhysicalSalaryRequest(String pinfl) {}
    public record PhysicalDebtRequest(String pinfl) {}
    public record LegalEntityDebtRequest(String tin) {}
    public record StaffCountRequest(String tin) {}
    public record MipAddressRequest(String pinpp) {}
    public record MipWorkHistoryRequest(String pin, String lang, String type, String dateBegin, String dateEnd) {}
    public record MipWorkCurrentRequest(String pin) {}
    public record MipStudentRequest(String pinfl) {}
    public record MipDiplomaRequest(String pinfl) {}
    public record MipLegalEntityRequest(String tin) {}
    public record MipStaffPeopleRequest(String tin, int limit, int page) {}
    public record MibDebtRequest(String pin) {}
    public record MibBanDebtRequest(String pin) {}
    public record MibAlimonyRequest(String pin, int type) {}
    public record DisabilityRequest(String transaction_id, String pin, String purpose, String consent, String pinfl, String passport) {}
    public record WomanNotebookRequest(String pinfl, @JsonProperty("passport_sn") String passportSn) {}
    public record CertificateSearchRequest(String nBlank, String inn, String organName, String registeredGov, String nameProduct, String fromOf, String deadlineCertificate) {}
    public record FinancialReportRequest(@JsonProperty("oferta_base64") String ofertaBase64, int quarter, @JsonProperty("request_date") String requestDate, @JsonProperty("request_id") long requestId, String tin, int year) {}
    public record RentalRequest(@JsonProperty("objcetCode") String objectCode, String pinfl, String tin, @JsonProperty("req_date") String requestDate, @JsonProperty("req_id") long requestId) {}
    public record CustomsMailRequest(@JsonProperty("transaction_id") String transactionId, @JsonProperty("sender_pinfl") String senderPinfl, String purpose, String consent, String pinfl) {}
    public record HotWaterRequest(String cadastre) {}
    public record AuctionLotInfoRequest(String language, String lot) {}
    public record AuctionLotsRequest(String language, int page, @JsonProperty("per_page") int perPage, @JsonProperty("region_soato") String regionSoato, @JsonProperty("area_soato") String areaSoato, @JsonProperty("property_group") String propertyGroup, @JsonProperty("property_category") String propertyCategory, @JsonProperty("auction_date") String auctionDate) {}
    public record WasteRequest(@JsonProperty("cad_num") String cadNum) {}
}