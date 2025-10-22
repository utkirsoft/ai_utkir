package uz.department.uai.integration.notarial.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.department.uai.integration.notarial.dto.NotarialRequestDTOs.*;
import uz.department.uai.integration.notarial.dto.NotarialResponseDTOs.*;
import uz.department.uai.integration.notarial.service.NotarialApiClient;

/**
 * Notarius API'lari bilan ishlash uchun Controller.
 */
@RestController
@RequestMapping("/api/integration/notarial")
@RequiredArgsConstructor
public class NotarialController {

    private final NotarialApiClient notarialApiClient;

    /**
     * Notarial taqiqlarni qidirish endpoint'i.
     * @param type "member" yoki "subject" bo'lishi mumkin.
     * @param requestBody Qidiruv parametrlarini o'z ichiga olgan JSON (Member yoki Subject DTO).
     * @return Qidiruv natijasi.
     */
    @PostMapping("/ban/search")
    public ResponseEntity<SearchBanResponse> searchBan(
            @RequestParam String type, // "member" or "subject"
            @RequestBody Object requestBody // Bu yerda Member yoki Subject DTO keladi
    ) {
        Integer typeMember = type.equalsIgnoreCase("member") ?
                ((Member) requestBody).getType() : null; // Bu yerda tipni aniqlash logikasi murakkablashishi mumkin
        Integer typeSubject = type.equalsIgnoreCase("subject") ?
                ((Subject) requestBody).getType() : null; // Yaxshiroq yechim - alohida endpointlar qilish

        // Bu yerdagi Object cast xavfli, yaxshiroq yechim alohida endpointlar
        // Yoki requestBody'ni tekshirib, Member/Subject'ga o'girish
        SearchBanResponse response = notarialApiClient.searchNotarialBan(requestBody, typeMember, typeSubject);
        return ResponseEntity.ok(response);
    }

    /**
     * Yangi notarial taqiq qo'shish.
     * @param request Taqiq ma'lumotlari.
     * @return Natija.
     */
    @PostMapping("/ban/add")
    public ResponseEntity<AddBanResponse> addBan(@RequestBody AddBanRequest request) {
        // Faylni Base64'ga o'girish logikasi kerak bo'lsa, shu yerda qo'shiladi
        // request.getStatement().setBaseDocument(notarialApiClient.encodeFileToBase64(...));
        AddBanResponse response = notarialApiClient.addNotarialBan(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Notarial taqiqni bekor qilish.
     * @param request Bekor qilish ma'lumotlari.
     * @return Natija.
     */
    @PostMapping("/ban/cancel")
    public ResponseEntity<CancelBanResponse> cancelBan(@RequestBody CancelBanRequest request) {
        // Faylni Base64'ga o'girish logikasi kerak bo'lsa, shu yerda qo'shiladi
        CancelBanResponse response = notarialApiClient.cancelNotarialBan(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Notarial harakatlarni qidirish.
     * @param request Qidiruv parametrlari.
     * @return Natija.
     */
    @PostMapping("/actions/search")
    public ResponseEntity<SearchActionsResponse> searchActions(@RequestBody SearchActionsRequest request) {
        SearchActionsResponse response = notarialApiClient.searchNotarialActions(request);
        return ResponseEntity.ok(response);
    }
}
