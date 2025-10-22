package uz.department.uai.integration.mip.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uz.department.uai.integration.mip.config.MipApiProperties;
import uz.department.uai.integration.mip.dto.MipRequestDTOs.*;
import uz.department.uai.shared.exception.ApiClientErrorException;
import uz.department.uai.shared.exception.ApiServerException;
import uz.department.uai.shared.exception.IntegrationException;
import uz.department.uai.shared.exception.ServiceUnavailableException;


import java.io.IOException;
import java.nio.channels.UnresolvedAddressException;
import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MipApiClient {

    private final RestTemplate restTemplate;
    private final MipApiProperties mipApiProperties;
    private final MipTokenManager mipTokenManager;

    private <T> T sendPostRequest(String path, Object body, Class<T> responseType) {
        return sendRequest(path, HttpMethod.POST, body, responseType);
    }

    private <T> T sendGetRequest(String path, Class<T> responseType) {
        return sendRequest(path, HttpMethod.GET, null, responseType);
    }

    private <T> T sendRequest(String path, HttpMethod method, Object body, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // Token olishda xatolik bo'lsa, bu yerda ham exception chiqishi mumkin
        try {
            headers.setBearerAuth(mipTokenManager.getAccessToken());
        } catch (Exception e) {
            log.error("Token olishda xatolik yuz berdi!", e);
            // Agar token olishning o'zi tarmoq xatosi bo'lsa ham ServiceUnavailableException beramiz
            Throwable rootCause = findRootCause(e);
            if (rootCause instanceof IOException || rootCause instanceof ResourceAccessException) {
                throw new ServiceUnavailableException("MIP (Token olishda)", e);
            }
            // Boshqa xatoliklar uchun umumiy IntegrationException
            throw new IntegrationException("MIP token olishda kutilmagan xatolik: " + e.getMessage(), e);
        }

        HttpEntity<Object> entity = new HttpEntity<>(body, headers);
        String url = mipApiProperties.getBaseUrl() + "/" + path;

        try {
            log.debug("Tashqi API'ga so'rov yuborilmoqda: {} {}", method, url);
            ResponseEntity<T> responseEntity = restTemplate.exchange(url, method, entity, responseType);
            log.debug("Tashqi API'dan javob olindi: {}", responseEntity.getStatusCode());
            return responseEntity.getBody();

        } catch (HttpClientErrorException e) { // 4xx xatoliklar
            log.warn("ApiClient 4xx xatolikni ushladi: {} {}, Status: {}, Body: {}", method, url, e.getStatusCode(), e.getResponseBodyAsString());
            throw new ApiClientErrorException("MIP", e.getStatusCode(), e.getResponseBodyAsString(), e);

        } catch (HttpServerErrorException e) { // 5xx xatoliklar
            log.error("ApiClient 5xx xatolikni ushladi: {} {}, Status: {}, Body: {}", method, url, e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new ApiServerException("MIP", e.getStatusCode(), e.getResponseBodyAsString(), e);

        } catch (ResourceAccessException e) { // Tarmoq xatoliklari (Timeout, Connection Refused, DNS va hk.)
            log.error("ApiClient tarmoq xatoligini (ResourceAccessException) ushladi: {} {}", method, url, e);
            // Bu yerda asl sababni (cause) tekshirishimiz shart EMAS,
            // chunki GlobalExceptionHandler buni o'zi qiladi.
            throw new ServiceUnavailableException("MIP", e); // <-- MUHIM: ServiceUnavailableException'ga o'girish

        } catch (Exception e) { // Boshqa barcha kutilmagan xatoliklar
            log.error("ApiClient kutilmagan umumiy Exception ushladi: {} {}", method, url, e);
            throw new IntegrationException("MIP servisida kutilmagan xatolik: " + e.getMessage(), e);
        }
    }
    // findRootCause metodini shu klassga yoki Util klassiga qo'shing
    private Throwable findRootCause(Throwable throwable) {
        Throwable rootCause = throwable;
        while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
            rootCause = rootCause.getCause();
        }
        return rootCause;
    }

    /**
     * Jismoniy shaxsning daromadlari haqida ma'lumot oladi.
     */
    public Object sendPhysicalSalary(String pin) {
        String path = mipApiProperties.getPaths().getPhysicalSalary();
        return sendPostRequest(path, new PhysicalSalaryRequest(pin), Object.class);
    }

    /**
     * Fuqaroning nogironligi haqida ma'lumot oladi.
     */
    public Object sendDisability(String pin) {
        String path = mipApiProperties.getPaths().getDisability();
        // PHP kodidagi statik qiymatlarni DTO'ga joylashtiramiz
        var body = new DisabilityRequest("1234", "12345678901234", "Tezkor qidiruv faoliyati uchun", "true", pin, "");
        return sendPostRequest(path, body, Object.class);
    }

    /**
     * Jismoniy shaxsning notarial harakatlar bo'yicha qarzdorligini tekshiradi.
     */
    public Object sendPhysicalDebt(String pin) {
        String path = mipApiProperties.getPaths().getPhysicalDebt();
        return sendPostRequest(path, new PhysicalDebtRequest(pin), Object.class);
    }

    /**
     * Yuridik shaxsning soliq bo'yicha qarzdorligini tekshiradi.
     */
    public Object sendLegalEntityDebt(String tin) {
        String path = mipApiProperties.getPaths().getLegalEntityDebt();
        return sendPostRequest(path, new LegalEntityDebtRequest(tin), Object.class);
    }

    /**
     * Tashkilotdagi xodimlar soni haqida ma'lumot oladi.
     */
    public Object sendStaffCount(String tin) {
        String path = mipApiProperties.getPaths().getStaffCount();
        return sendPostRequest(path, new StaffCountRequest(tin), Object.class);
    }

    /**
     * "Ayollar daftari" bo'yicha ma'lumotlarni oladi.
     */
    public Object sendWomanNotebook(String pin, String passport) {
        String path = mipApiProperties.getPaths().getWomanNotebook();
        return sendPostRequest(path, new WomanNotebookRequest(pin, passport), Object.class);
    }


    public Object sendNarcological(String pin) {
        String path = mipApiProperties.getPaths().getNarcological();
        return sendPostRequest(path, new NarcologicalRequest(pin), Object.class);
    }

    public Object sendPsychological(String pin) {
        String path = mipApiProperties.getPaths().getPsychological();
        return sendPostRequest(path, new PsychologicalRequest(pin), Object.class);
    }

    public Object sendSelfEmployment(String pin) {
        String path = mipApiProperties.getPaths().getSelfEmployment();
        return sendPostRequest(path, new SelfEmploymentRequest(pin), Object.class);
    }

    public Object sendTaxPerson(String pin) {
        String path = mipApiProperties.getPaths().getTaxPerson();
        return sendPostRequest(path, new TaxPersonRequest(pin), Object.class);
    }

    public Object sendTaxPersonByPassport(String serial, String number) {
        String path = mipApiProperties.getPaths().getTaxPerson();
        return sendPostRequest(path, new TaxPersonByPassportRequest(serial, number), Object.class);
    }



    public Object sendPersonSearch(String pin, String passport, String birthDate) {
        var body = new PersonSearchRequest(System.currentTimeMillis(), "Y", "35245636974164", 3, passport, pin, birthDate, "Y", "P");
        String path = mipApiProperties.getPaths().getPersonSearch();
        return sendPostRequest(path, body, Object.class);
    }

    public Object sendMipAddress(String pin) {
        String path = mipApiProperties.getPaths().getAddress();
        return sendPostRequest(path, new MipAddressRequest(pin), Object.class);
    }

    public Object sendMipWorkHistory(String pin) {
        var body = new MipWorkHistoryRequest(pin, "1", "1", "dd.mm.yyyy", "dd.mm.yyyy");
        String path = mipApiProperties.getPaths().getWorkHistory();
        return sendPostRequest(path, body, Object.class);
    }

    public Object sendMipWorkCurrent(String pin) {
        String path = mipApiProperties.getPaths().getWorkCurrent();
        return sendPostRequest(path, new MipWorkCurrentRequest(pin), Object.class);
    }

    public Object sendMipStudent(String pin) {
        String path = mipApiProperties.getPaths().getStudent();
        return sendPostRequest(path, new MipStudentRequest(pin), Object.class);
    }

    public Object sendMipDiploma(String pin) {
        String path = mipApiProperties.getPaths().getDiploma();
        return sendPostRequest(path, new MipDiplomaRequest(pin), Object.class);
    }

    public Object sendMipLegalEntity(String tin) {
        String path = mipApiProperties.getPaths().getLegalEntity();
        return sendPostRequest(path, new MipLegalEntityRequest(tin), Object.class);
    }

    public Object sendMipStaffPeople(String tin, int page) {
        String path = mipApiProperties.getPaths().getStaffPeople();
        return sendPostRequest(path, new MipStaffPeopleRequest(tin, 20, page), Object.class);
    }

    public Object sendMibDebt(String pin) {
        String path = mipApiProperties.getPaths().getMibDebt();
        return sendPostRequest(path, new MibDebtRequest(pin), Object.class);
    }

    public Object sendMibBanDebt(String pin) {
        String path = mipApiProperties.getPaths().getMibBanDebt();
        return sendPostRequest(path, new MibBanDebtRequest(pin), Object.class);
    }

    public Object sendMibAlimony(String pin) {
        String path = mipApiProperties.getPaths().getMibAlimony();
        return sendPostRequest(path, new MibAlimonyRequest(pin, 1), Object.class);
    }
    public Object sendCertificateSearch(CertificateSearchRequest body) {
        String path = mipApiProperties.getPaths().getCertificateSearch();
        return sendPostRequest(path, body, Object.class);
    }

    public Object sendFinancialReport(String tin) {
        // In a real scenario, these values should be dynamic
        var body = new FinancialReportRequest("TEST", 4, "2024-08-13 14:17:15", 1542, tin, 2023);
        String path = mipApiProperties.getPaths().getFinancialReport();
        return sendPostRequest(path, body, Object.class);
    }

    public Object sendFactureListByCatalogCode(String tin, String code, LocalDate fromDate, LocalDate toDate) {
        String baseUrl = mipApiProperties.getBaseUrl() + "/" + mipApiProperties.getPaths().getFactureListByCatalog();
        String url = UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("catalogCode", code)
                .queryParam("fromDate", fromDate.toString())
                .queryParam("tin", tin)
                .queryParam("toDate", toDate.toString())
                .toUriString();

        // Note: This uses a custom GET request method to handle URLs with query params
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(mipTokenManager.getAccessToken());
        HttpEntity<?> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(url, HttpMethod.GET, entity, Object.class).getBody();
    }

    public Object sendRentalRequest(String objectCode, String pinfl, String tin) {
        var body = new RentalRequest(objectCode, pinfl, tin, LocalDate.now().toString(), System.currentTimeMillis());
        String path = mipApiProperties.getPaths().getRental();
        return sendPostRequest(path, body, Object.class);
    }

    public Object sendCustomsMail(String pin) {
        // In a real scenario, these values should be dynamic
        var body = new CustomsMailRequest("some_transaction_id", "91908871110012", "Tezkor qidiruv faoliyati", "true", pin);
        String path = mipApiProperties.getPaths().getCustomsMail();
        return sendPostRequest(path, body, Object.class);
    }

    public Object sendHotWater(String cadastre) {
        String path = mipApiProperties.getPaths().getHotWater();
        return sendPostRequest(path, new HotWaterRequest(cadastre), Object.class);
    }

    public Object sendMipAuctionLotInfo(String lotNumber) {
        var body = new AuctionLotInfoRequest("uk", lotNumber);
        String path = mipApiProperties.getPaths().getAuctionLotInfo();
        return sendPostRequest(path, body, Object.class);
    }

    public Object sendMipAuctionLots(AuctionLotsRequest body) {
        String path = mipApiProperties.getPaths().getAuctionLots();
        return sendPostRequest(path, body, Object.class);
    }

    public Object sendWaste(String cadNum) {
        String path = mipApiProperties.getPaths().getWaste();
        return sendPostRequest(path, new WasteRequest(cadNum), Object.class);
    }

    public Object sendOnlineLanguageCertificate(String pinfl) {
        String baseUrl = mipApiProperties.getBaseUrl() + "/" + mipApiProperties.getPaths().getLanguageCertificate();
        String url = UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("PNFL", pinfl)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(mipTokenManager.getAccessToken());
        HttpEntity<?> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(url, HttpMethod.GET, entity, Object.class).getBody();
    }

    public Object sendCollege(String pinfl) {
        String baseUrl = mipApiProperties.getBaseUrl() + "/" + mipApiProperties.getPaths().getCollege();
        String url = UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("pinfl", pinfl)
                .toUriString();
        return sendGetRequest(url, Object.class); // Assuming sendGetRequest handles full URLs
    }
    /**
     * Maktab shahodatnomasi ma'lumotlarini JShShIR (PINFL) bo'yicha oladi.
     * @param pin Jismoniy shaxsning JShShIR raqami.
     * @return Tashqi API'dan kelgan javob.
     */
    public Object sendSchool(String pin) {
        // 1. Asosiy URL manzilini olamiz
        String baseUrl = mipApiProperties.getBaseUrl();
        // 2. Endpoint yo'lining shablonini olamiz
        String pathTemplate = mipApiProperties.getPaths().getSchoolCertificate();

        // 3. UriComponentsBuilder yordamida to'liq URL'ni xavfsiz yig'amiz
        String url = UriComponentsBuilder.fromUriString(baseUrl)
                .pathSegment(pathTemplate) // Shablonni qo'shamiz
                .queryParam("transaction_id", UUID.randomUUID().toString()) // Dinamik transaction_id
                .queryParam("sender_pinfl", mipApiProperties.getPaths().getSchoolCertificateSenderPin()) // Konfiguratsiyadan olamiz
                .queryParam("purpose", "xizmat")
                .queryParam("consent", "yes")
                .buildAndExpand(pin) // Shablon ichidagi {pin} o'rniga qiymatni qo'yadi
                .toUriString();

        // 4. Tayyor bo'lgan URL bilan so'rovni yuboramiz
        return sendGetRequest(url, Object.class);
    }
}