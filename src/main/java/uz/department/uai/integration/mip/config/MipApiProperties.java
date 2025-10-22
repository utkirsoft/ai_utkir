package uz.department.uai.integration.mip.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "integration.mip")
public class MipApiProperties {
    private String baseUrl;
    private String tokenUrl;
    private Credentials credentials;
    private Paths paths;


    @Data
    public static class Credentials {
        private String grantType;
        private String username;
        private String password;
        private String basicAuth;
    }

    @Data
    public static class Paths { // <-- YANGI ICHKI KLASS
        private String narcological;
        private String psychological;
        private String selfEmployment;
        private String taxPerson;
        private String personSearch;
        private String address;
        private String workHistory;
        private String workCurrent;
        private String disability;
        private String physicalSalary;
        private String physicalDebt;
        private String legalEntityDebt;
        private String staffCount;
        private String womanNotebook;
        private String certificateSearch;
        private String financialReport;
        private String factureListByCatalog;
        private String rental;
        private String customsMail;
        private String hotWater;
        private String auctionLotInfo;
        private String auctionLots;
        private String waste;
        private String languageCertificate;
        private String college;
        private String schoolCertificate;
        private String schoolCertificateSenderPin;
        private String student;
        private String diploma;
        private String legalEntity;
        private String staffPeople;
        private String mibDebt;
        private String mibBanDebt;
        private String mibAlimony;

    }
}
