package uz.department.uai.integration.notarial.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uz.department.uai.integration.notarial.config.NotarialApiProperties;
import uz.department.uai.integration.notarial.dto.NotarialRequestDTOs.*;
import uz.department.uai.integration.notarial.dto.NotarialResponseDTOs.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class NotarialApiClient {

    private final RestTemplate restTemplate;
    private final NotarialApiProperties properties;

    // --- Private Helper Methods ---

    private <T> T sendBanServiceRequest(String path, Object body, Class<T> responseType) {
        String url = properties.getBan().getBaseUrl() + "/" + path;
        String basicAuth = properties.getBan().getBasicAuth();
        return sendRequest(url, body, basicAuth, responseType);
    }

    private <T> T sendIntegrationServiceRequest(String path, Object body, Class<T> responseType) {
        String url = properties.getNotarialActs().getBaseUrl() + "/" + path;
        String basicAuth = properties.getNotarialActs().getBasicAuth();
        return sendRequest(url, body, basicAuth, responseType);
    }

    private <T> T sendRequest(String url, Object body, String basicAuthCredentials, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Basic " + basicAuthCredentials);

        HttpEntity<Object> entity = new HttpEntity<>(body, headers);

        return restTemplate.exchange(url, HttpMethod.POST, entity, responseType).getBody();
    }

    // --- Public API Methods ---

    /**
     * Notarial taqiqlarni qidirish.
     * @param searchData Qidiruv parametri (Member yoki Subject bo'yicha).
     * @param typeMember Agar ishtirokchi (member) bo'yicha qidirilayotgan bo'lsa, uning tipi (1 yoki 2).
     * @param typeSubject Agar obyekt (subject) bo'yicha qidirilayotgan bo'lsa, uning tipi.
     * @return Qidiruv natijasi (API dokumentatsiyasiga mos DTO).
     */
    public SearchBanResponse searchNotarialBan(Object searchData, Integer typeMember, Integer typeSubject) {
        // PHP kodidagi mantiqni DTO qurish uchun moslash kerak
        SearchBanRequest.SearchBanRequestBuilder requestBuilder = SearchBanRequest.builder()
                .requestId(System.currentTimeMillis()) // Yoki boshqa unikal ID generator
                .requestDate(LocalDate.now().toString())
                .declarant(buildDefaultDeclarant()); // Standart ma'lumotlarni alohida metodga chiqarish mumkin

        if (typeMember != null && searchData instanceof Member) {
            requestBuilder.member((Member) searchData);
        } else if (typeSubject != null && searchData instanceof Subject) {
            requestBuilder.subject((Subject) searchData);
        } else {
            throw new IllegalArgumentException("Either member or subject must be provided for search");
        }

        String path = properties.getBan().getPaths().getSearch();
        return sendBanServiceRequest(path, requestBuilder.build(), SearchBanResponse.class);
    }

    /**
     * Yangi notarial taqiq qo'shish.
     * @param request Taqiq ma'lumotlarini o'z ichiga olgan DTO.
     * @return Taqiq qo'shish natijasi.
     */
    public AddBanResponse addNotarialBan(AddBanRequest request) {
        String path = properties.getBan().getPaths().getAddBan();
        return sendBanServiceRequest(path, request, AddBanResponse.class);
    }

    /**
     * Mavjud notarial taqiqni bekor qilish.
     * @param request Taqiqni bekor qilish ma'lumotlarini o'z ichiga olgan DTO.
     * @return Taqiqni bekor qilish natijasi.
     */
    public CancelBanResponse cancelNotarialBan(CancelBanRequest request) {
        String path = properties.getBan().getPaths().getCancelBan();
        return sendBanServiceRequest(path, request, CancelBanResponse.class);
    }

    /**
     * Shaxs yoki obyekt bo'yicha notarial harakatlarni qidirish.
     * @param request Qidiruv parametrlarini o'z ichiga olgan DTO.
     * @return Notarial harakatlar ro'yxati.
     */
    public SearchActionsResponse searchNotarialActions(SearchActionsRequest request) {
        String path = properties.getNotarialActs().getPaths().getSearchActions();
        return sendIntegrationServiceRequest(path, request, SearchActionsResponse.class);
    }

    // --- Helper Methods ---

    private Declarant buildDefaultDeclarant() {
        // Bu ma'lumotlarni ham konfiguratsiyaga chiqarish mumkin
        return Declarant.builder()
                .companyInn("203661531")
                .mfo("00423")
                .companyName("O‘ZBEKISTON RESPUBLIKASI BOSH PROKURATURASI HUZURIDAGI IQTISODIY JINOYATLARGA QARSHI KURASHISH DEPARTAMENTI")
                .representativeInn("201589828")
                .representativeFio("Иванов Иван Иванович") // Yoki joriy foydalanuvchi FIOsi
                .build();
    }

    // PHP kodidagi base64 bilan ishlash logikasi kerak bo'lsa, shu yerga qo'shiladi
    public String encodeFileToBase64(String filePath) {
        // Faylni o'qib, Base64'ga o'girish logikasi
        // Files.readAllBytes(Paths.get(filePath));
        // Base64.getEncoder().encodeToString(...);
        return "base64_encoded_file_content"; // Placeholder
    }
}