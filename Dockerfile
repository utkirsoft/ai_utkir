# --- 1-Bosqich: Loyihani build qilish ---
# Build uchun asosiy image'ni tanlaymiz (Java 17 va Maven). O'zingiznikiga moslang.
FROM maven:3-openjdk-17 AS build

# Ishchi papkani belgilaymiz
WORKDIR /app

# Loyiha bog'liqliklarini (dependencies) ko'chirib, yuklab olamiz
COPY pom.xml .
RUN mvn dependency:go-offline

# Qolgan barcha kodni ko'chiramiz
COPY src ./src

# Loyihani .jar fayliga build qilamiz
RUN mvn package -DskipTests

# --- 2-Bosqich: Yakuniy image'ni yaratish ---
# Kichikroq hajmdagi JRE (Java Runtime Environment) image'ini tanlaymiz
FROM eclipse-temurin:17-jre-jammy

# Ishchi papkani belgilaymiz
WORKDIR /app

# Build qilingan .jar faylni 1-bosqichdan ko'chirib olamiz
# "your-app-name" o'rniga pom.xml dagi <artifactId> qiymatini yozing
COPY --from=build /app/target/uai-0.0.1-SNAPSHOT.jar app.jar

# Ilova ishga tushadigan portni ochamiz
EXPOSE 8081

# Ilovani ishga tushirish uchun buyruq
ENTRYPOINT ["java", "-jar", "app.jar"]

