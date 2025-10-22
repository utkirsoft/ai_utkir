package uz.department.uai.config.test;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component // Bu klassni Spring komponenti sifatida ro'yxatdan o'tkazish
public class ConnectionTester implements CommandLineRunner {

    private final JdbcTemplate postgresJdbcTemplate;
//    private final JdbcTemplate oracleJdbcTemplate;

    // Konstruktor orqali ikkala DataSource'ni qabul qilib olamiz
    public ConnectionTester(
            @Qualifier("postgresDataSource") DataSource postgresDataSource
//            @Qualifier("oracleDataSource") DataSource oracleDataSource
    ) {
        this.postgresJdbcTemplate = new JdbcTemplate(postgresDataSource);
//        this.oracleJdbcTemplate = new JdbcTemplate(oracleDataSource);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=========================================");
        System.out.println("MA'LUMOTLAR BAZASI BILAN ULANISHNI TEKSHIRISH...");

        // 1. PostgreSQL'ni tekshirish
        try {
            // "SELECT 1" - bu eng oddiy va tezkor so'rov. Hech qanday jadvalga bog'liq emas.
            Integer result = postgresJdbcTemplate.queryForObject("SELECT 1", Integer.class);
            if (result != null && result == 1) {
                System.out.println("✅ PostgreSQL bilan ulanish muvaffaqiyatli!");
            } else {
                System.out.println("❌ PostgreSQL bilan ulanishda noma'lum muammo.");
            }
        } catch (Exception e) {
            System.err.println("🔥 PostgreSQL'ga ulanishda xatolik: " + e.getMessage());
        }

//        // 2. Oracle'ni tekshirish
//        try {
//            // Oracle uchun "SELECT 1 FROM DUAL" standart so'rov hisoblanadi.
//            Integer result = oracleJdbcTemplate.queryForObject("SELECT 1 FROM DUAL", Integer.class);
//            if (result != null && result == 1) {
//                System.out.println("✅ Oracle bilan ulanish muvaffaqiyatli!");
//            } else {
//                System.out.println("❌ Oracle bilan ulanishda noma'lum muammo.");
//            }
//        } catch (Exception e) {
//            System.err.println("🔥 Oracle'ga ulanishda xatolik: " + e.getMessage());
//        }
        System.out.println("=========================================");
    }
}