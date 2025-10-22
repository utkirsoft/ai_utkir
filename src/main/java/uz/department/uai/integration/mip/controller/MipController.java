package uz.department.uai.integration.mip.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.department.uai.integration.mip.dto.MipRequestDTOs;
import uz.department.uai.integration.mip.service.MipApiClient;

import java.time.LocalDate;

/**
 * Tashqi "Yagona milliy mehnat tizimi" (MIP) API'lari bilan ishlash uchun
 * markazlashtirilgan Controller.
 * Bu endpoint'lar faqat autentifikatsiyadan o'tgan foydalanuvchilar uchun ochiq.
 */
@RestController
@RequestMapping("/api/integration/mip")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class MipController {

    private final MipApiClient mipApiClient;

    /**
     * Fuqaroning narkologik dispanser hisobida turishi haqida ma'lumot oladi.
     * @param pin JShShIR (PINFL)
     * @return API javobi
     */
    @PostMapping("/narcological/{pin}")
    public ResponseEntity<Object> getNarcologicalInfo(@PathVariable String pin) {
        Object response = mipApiClient.sendNarcological(pin);
        return ResponseEntity.ok(response);
    }

    /**
     * Fuqaroning psixonevrologik dispanser hisobida turishi haqida ma'lumot oladi.
     * @param pin JShShIR (PINFL)
     * @return API javobi
     */
    @PostMapping("/psychological/{pin}")
    public ResponseEntity<Object> getPsychologicalInfo(@PathVariable String pin) {
        Object response = mipApiClient.sendPsychological(pin);
        return ResponseEntity.ok(response);
    }

    /**
     * Fuqaroning nogironligi haqida ma'lumot oladi.
     * @param pin JShShIR (PINFL)
     * @return API javobi
     */
    @PostMapping("/disability/{pin}")
    public ResponseEntity<Object> getDisabilityInfo(@PathVariable String pin) {
        Object response = mipApiClient.sendDisability(pin);
        return ResponseEntity.ok(response);
    }

    /**
     * Shaxsning o'zini o'zi band qilganligi statusini tekshiradi.
     * @param pin JShShIR (PINFL)
     * @return API javobi
     */
    @PostMapping("/self-employment/{pin}")
    public ResponseEntity<Object> getSelfEmploymentStatus(@PathVariable String pin) {
        Object response = mipApiClient.sendSelfEmployment(pin);
        return ResponseEntity.ok(response);
    }

    /**
     * JShShIR (PINFL) orqali STIR (INN) ma'lumotlarini oladi.
     * @param pin JShShIR (PINFL)
     * @return API javobi
     */
    @PostMapping("/tax-person/by-pinfl/{pin}")
    public ResponseEntity<Object> getTaxInfoByPinfl(@PathVariable String pin) {
        Object response = mipApiClient.sendTaxPerson(pin);
        return ResponseEntity.ok(response);
    }

    /**
     * Pasport seriyasi va raqami orqali STIR (INN) ma'lumotlarini oladi.
     * @param serial Pasport seriyasi
     * @param number Pasport raqami
     * @return API javobi
     */
    @PostMapping("/tax-person/by-passport")
    public ResponseEntity<Object> getTaxInfoByPassport(@RequestParam String serial, @RequestParam String number) {
        Object response = mipApiClient.sendTaxPersonByPassport(serial, number);
        return ResponseEntity.ok(response);
    }

    /**
     * Maktab shahodatnomasi ma'lumotlarini oladi.
     * @param pin JShShIR (PINFL)
     * @return API javobi
     */
    @GetMapping("/school-certificate/{pin}")
    public ResponseEntity<Object> getSchoolCertificate(@PathVariable String pin) {
        Object response = mipApiClient.sendSchool(pin);
        return ResponseEntity.ok(response);
    }

    /**
     * Shaxsni JShShIR, pasport yoki tug'ilgan sana bo'yicha qidirish.
     * @param pin JShShIR (ixtiyoriy)
     * @param passport Pasport seriya va raqami (ixtiyoriy)
     * @param birthDate Tug'ilgan sana (ixtiyoriy)
     * @return API javobi
     */
    @GetMapping("/person/search")
    public ResponseEntity<Object> searchPerson(
            @RequestParam(required = false) String pin,
            @RequestParam(required = false) String passport,
            @RequestParam(required = false) String birthDate
    ) {
        Object response = mipApiClient.sendPersonSearch(pin, passport, birthDate);
        return ResponseEntity.ok(response);
    }

    /**
     * Jismoniy shaxsning daromadlari haqida ma'lumot oladi.
     * @param pin JShShIR (PINFL)
     * @return API javobi
     */
    @PostMapping("/physical-salary/{pin}")
    public ResponseEntity<Object> getPhysicalSalary(@PathVariable String pin) {
        Object response = mipApiClient.sendPhysicalSalary(pin);
        return ResponseEntity.ok(response);
    }

    /**
     * Jismoniy shaxsning notarial harakatlar bo'yicha qarzdorligini tekshiradi.
     * @param pin JShShIR (PINFL)
     * @return API javobi
     */
    @PostMapping("/physical-debt/{pin}")
    public ResponseEntity<Object> getPhysicalDebt(@PathVariable String pin) {
        Object response = mipApiClient.sendPhysicalDebt(pin);
        return ResponseEntity.ok(response);
    }

    /**
     * Yuridik shaxsning soliq bo'yicha qarzdorligini tekshiradi.
     * @param tin STIR (INN)
     * @return API javobi
     */
    @PostMapping("/legal-entity-debt/{tin}")
    public ResponseEntity<Object> getLegalEntityDebt(@PathVariable String tin) {
        Object response = mipApiClient.sendLegalEntityDebt(tin);
        return ResponseEntity.ok(response);
    }

    /**
     * Tashkilotdagi xodimlar soni haqida ma'lumot oladi.
     * @param tin STIR (INN)
     * @return API javobi
     */
    @PostMapping("/staff-count/{tin}")
    public ResponseEntity<Object> getStaffCount(@PathVariable String tin) {
        Object response = mipApiClient.sendStaffCount(tin);
        return ResponseEntity.ok(response);
    }

    /**
     * JShShIR bo'yicha manzil ma'lumotlarini oladi.
     * @param pin JShShIR (PINFL)
     * @return API javobi
     */
    @PostMapping("/address/{pin}")
    public ResponseEntity<Object> getAddressInfo(@PathVariable String pin) {
        Object response = mipApiClient.sendMipAddress(pin);
        return ResponseEntity.ok(response);
    }

    /**
     * Fuqaroning mehnat daftarchasi tarixini oladi.
     * @param pin JShShIR (PINFL)
     * @return API javobi
     */
    @PostMapping("/work-history/{pin}")
    public ResponseEntity<Object> getWorkHistory(@PathVariable String pin) {
        Object response = mipApiClient.sendMipWorkHistory(pin);
        return ResponseEntity.ok(response);
    }

    /**
     * Fuqaroning joriy ish joyi haqida ma'lumot oladi.
     * @param pin JShShIR (PINFL)
     * @return API javobi
     */
    @PostMapping("/work-current/{pin}")
    public ResponseEntity<Object> getCurrentWorkPlace(@PathVariable String pin) {
        Object response = mipApiClient.sendMipWorkCurrent(pin);
        return ResponseEntity.ok(response);
    }

    /**
     * Talaba ma'lumotlarini oladi.
     * @param pin JShShIR (PINFL)
     * @return API javobi
     */
    @PostMapping("/student/{pin}")
    public ResponseEntity<Object> getStudentInfo(@PathVariable String pin) {
        Object response = mipApiClient.sendMipStudent(pin);
        return ResponseEntity.ok(response);
    }

    /**
     * Diplom ma'lumotlarini oladi.
     * @param pin JShShIR (PINFL)
     * @return API javobi
     */
    @PostMapping("/diploma/{pin}")
    public ResponseEntity<Object> getDiplomaInfo(@PathVariable String pin) {
        Object response = mipApiClient.sendMipDiploma(pin);
        return ResponseEntity.ok(response);
    }

    /**
     * Yuridik shaxs ma'lumotlarini oladi.
     * @param tin STIR (INN)
     * @return API javobi
     */
    @PostMapping("/legal-entity/{tin}")
    public ResponseEntity<Object> getLegalEntityInfo(@PathVariable String tin) {
        Object response = mipApiClient.sendMipLegalEntity(tin);
        return ResponseEntity.ok(response);
    }

    /**
     * MIB (Мажбурий ижро бюроси) bo'yicha ijro hujjati bo'yicha taqiq va qarzdorlikni tekshiradi.
     * @param pin JShShIR (PINFL)
     * @return API javobi
     */
    @PostMapping("/mib/debt-ban/{pin}")
    public ResponseEntity<Object> getMibDebtAndBanInfo(@PathVariable String pin) {
        Object response = mipApiClient.sendMibDebt(pin);
        return ResponseEntity.ok(response);
    }

    /**
     * MIB bo'yicha qarzdorlikni tekshiradi.
     * @param pin JShShIR (PINFL)
     * @return API javobi
     */
    @PostMapping("/mib/debt/{pin}")
    public ResponseEntity<Object> getMibDebtInfo(@PathVariable String pin) {
        Object response = mipApiClient.sendMibBanDebt(pin);
        return ResponseEntity.ok(response);
    }

    /**
     * MIB bo'yicha aliment qarzdorligini tekshiradi.
     * @param pin JShShIR (PINFL)
     * @return API javobi
     */
    @PostMapping("/mib/alimony/{pin}")
    public ResponseEntity<Object> getMibAlimonyInfo(@PathVariable String pin) {
        Object response = mipApiClient.sendMibAlimony(pin);
        return ResponseEntity.ok(response);
    }

    /**
     * "Ayollar daftari" bo'yicha ma'lumotlarni oladi.
     * @param pin JShShIR (PINFL)
     * @param passport Pasport seriya va raqami
     * @return API javobi
     */
    @PostMapping("/woman-notebook")
    public ResponseEntity<Object> getWomanNotebookInfo(@RequestParam String pin, @RequestParam String passport) {
        Object response = mipApiClient.sendWomanNotebook(pin, passport);
        return ResponseEntity.ok(response);
    }
    /**
     * Sertifikat ma'lumotlarini so'rov tanasidagi parametrlar asosida qidiradi.
     * @param request Qidiruv parametrlarini (nBlank, inn, organName va hk.) o'z ichiga olgan DTO.
     * @return Tashqi API'dan kelgan javob.
     */
    @PostMapping("/sertificate/search")
    public ResponseEntity<Object> searchSertificate(@RequestBody MipRequestDTOs.CertificateSearchRequest request) {
        return ResponseEntity.ok(mipApiClient.sendCertificateSearch(request));
    }

    /**
     * STIR (INN) bo'yicha tashkilotning moliyaviy hisobotini oladi.
     * @param tin Tashkilotning STIR (INN) raqami.
     * @return Tashqi API'dan kelgan javob.
     */
    @PostMapping("/financial-report/{tin}")
    public ResponseEntity<Object> getFinancialReport(@PathVariable String tin) {
        return ResponseEntity.ok(mipApiClient.sendFinancialReport(tin));
    }

    /**
     * Katalog kodi va belgilangan sana oralig'i bo'yicha hisob-fakturalar ro'yxatini oladi.
     * @param tin Tashkilotning STIR (INN) raqami.
     * @param code Mahsulot yoki xizmatning katalog kodi (MXIK).
     * @param fromDate Qidiruv boshlanish sanasi (ISO formatida, masalan, '2025-10-17').
     * @param toDate Qidiruv tugash sanasi (ISO formatida, masalan, '2025-10-20').
     * @return Tashqi API'dan kelgan javob.
     */
    @GetMapping("/facture-list/by-catalog")
    public ResponseEntity<Object> getFactureListByCatalog(
            @RequestParam String tin,
            @RequestParam String code,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        return ResponseEntity.ok(mipApiClient.sendFactureListByCatalogCode(tin, code, fromDate, toDate));
    }

    /**
     * Ijara shartnomalari (e-ijara) haqida ma'lumot oladi.
     * @param objectCode Obyektning kadastr kodi (ixtiyoriy).
     * @param pinfl Jismoniy shaxsning JShShIR raqami (ixtiyoriy).
     * @param tin Yuridik shaxsning STIR raqami (ixtiyoriy).
     * @return Tashqi API'dan kelgan javob.
     */
    @PostMapping("/rental")
    public ResponseEntity<Object> getRentalInfo(@RequestParam(required = false) String objectCode, @RequestParam(required = false) String pinfl, @RequestParam(required = false) String tin) {
        return ResponseEntity.ok(mipApiClient.sendRentalRequest(objectCode, pinfl, tin));
    }

    /**
     * JShShIR bo'yicha bojxona pochta jo'natmalari haqida ma'lumot oladi.
     * @param pin Jismoniy shaxsning JShShIR (PINFL) raqami.
     * @return Tashqi API'dan kelgan javob.
     */
    @PostMapping("/customs-mail/{pin}")
    public ResponseEntity<Object> getCustomsMailInfo(@PathVariable String pin) {
        return ResponseEntity.ok(mipApiClient.sendCustomsMail(pin));
    }

    /**
     * Kadastr raqami bo'yicha issiq suv ta'minoti iste'molchisi haqida ma'lumot oladi.
     * @param cadastre Obyektning kadastr raqami.
     * @return Tashqi API'dan kelgan javob.
     */
    @PostMapping("/hot-water/{cadastre}")
    public ResponseEntity<Object> getHotWaterInfo(@PathVariable String cadastre) {
        return ResponseEntity.ok(mipApiClient.sendHotWater(cadastre));
    }

    /**
     * "E-auksion" platformasidagi lot raqami bo'yicha batafsil ma'lumot oladi.
     * @param lotNumber Auktsion lotining unikal raqami.
     * @return Tashqi API'dan kelgan javob.
     */
    @PostMapping("/auction/lot-info/{lotNumber}")
    public ResponseEntity<Object> getAuctionLotInfo(@PathVariable String lotNumber) {
        return ResponseEntity.ok(mipApiClient.sendMipAuctionLotInfo(lotNumber));
    }

    /**
     * "E-auksion" platformasidagi lotlarni so'rov tanasidagi parametrlar asosida qidiradi.
     * @param request Qidiruv parametrlarini (viloyat, tuman, sana va hk.) o'z ichiga olgan DTO.
     * @return Tashqi API'dan kelgan javob.
     */
    @PostMapping("/auction/lots")
    public ResponseEntity<Object> searchAuctionLots(@RequestBody MipRequestDTOs.AuctionLotsRequest request) {
        return ResponseEntity.ok(mipApiClient.sendMipAuctionLots(request));
    }

    /**
     * Kadastr raqami bo'yicha chiqindilar bilan bog'liq xizmatlar haqida ma'lumot oladi.
     * @param cadNum Obyektning kadastr raqami.
     * @return Tashqi API'dan kelgan javob.
     */
    @PostMapping("/waste/{cadNum}")
    public ResponseEntity<Object> getWasteInfo(@PathVariable String cadNum) {
        return ResponseEntity.ok(mipApiClient.sendWaste(cadNum));
    }

    /**
     * DTM tomonidan berilgan chet tilini bilish sertifikati ma'lumotlarini oladi.
     * @param pinfl Jismoniy shaxsning JShShIR (PINFL) raqami.
     * @return Tashqi API'dan kelgan javob.
     */
    @GetMapping("/language-certificate/{pinfl}")
    public ResponseEntity<Object> getLanguageCertificate(@PathVariable String pinfl) {
        return ResponseEntity.ok(mipApiClient.sendOnlineLanguageCertificate(pinfl));
    }

    /**
     * O'rta maxsus ta'lim (kollej/texnikum) diplomi haqida ma'lumot oladi.
     * @param pinfl Jismoniy shaxsning JShShIR (PINFL) raqami.
     * @return Tashqi API'dan kelgan javob.
     */
    @GetMapping("/college/{pinfl}")
    public ResponseEntity<Object> getCollegeInfo(@PathVariable String pinfl) {
        return ResponseEntity.ok(mipApiClient.sendCollege(pinfl));
    }
}