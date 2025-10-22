Albatta, suhbatimiz davomida Docker va Docker Compose bo'yicha muhokama qilingan barcha buyruqlarni tushuntirishlari bilan birga yagona Markdown (`.md`) fayli uchun to'liq jamlanma qilib tayyorladim. Buni `DOCKER_COMMANDS.md` kabi faylga saqlab qo'yishingiz mumkin.

-----

# Docker va Docker Compose Buyruqlari: To'liq Qo'llanma

Bu hujjat Spring Boot loyihasini Docker yordamida ishga tushirish va boshqarish uchun kerak bo'ladigan asosiy buyruqlar va ularning izohlarini o'z ichiga oladi.

## Mundarija

1.  [Ubuntu'da Docker O'rnatish](https://www.google.com/search?q=%231-ubuntuda-docker-ornatish-muhim-qadamlar)
2.  [Dockerfile bilan ishlash](https://www.google.com/search?q=%232-dockerfile-bilan-ishlash)
3.  [Docker Compose Hayotiy Sikli Buyruqlari](https://www.google.com/search?q=%233-docker-compose-hayotiy-sikli-buyruqlari-eng-kop-ishlatiladiganlar)
4.  [Konteynerlarni Boshqarish va Nazorat Qilish](https://www.google.com/search?q=%234-konteynerlarni-boshqarish-va-nazorat-qilish)
5.  [Tizimni Tozalash Buyruqlari](https://www.google.com/search?q=%235-tizimni-tozalash-buyruqlari)

-----

### 1\. Ubuntuda Docker O'rnatish (Muhim Qadamlar)

Docker bilan ishlashdan oldin uni tizimga to'g'ri o'rnatish kerak.

* **Paketlar ro'yxatini yangilash:**

  ```bash
  sudo apt update
  ```

  Tizimdagi barcha paketlar haqidagi ma'lumotlarni yangilaydi.

* **Docker'ni o'rnatish:**

  ```bash
  sudo apt install docker.io docker-compose -y
  ```

  `docker.io` (Docker Engine) va `docker-compose` paketlarini o'rnatadi. `-y` bayrog'i barcha so'rovlarni avtomatik tasdiqlaydi.

* **Docker xizmatini yoqish va ishga tushirish:**

  ```bash
  sudo systemctl start docker
  sudo systemctl enable docker
  ```

  Docker xizmatini ishga tushiradi va kompyuter yoqilganda avtomatik ishga tushadigan qilib belgilaydi.

* **Foydalanuvchini `docker` guruhiga qo'shish (Juda Muhim\!):**

  ```bash
  sudo usermod -aG docker $USER
  ```

  Har safar `docker` buyrug'ini yozganda `sudo` yozmaslik uchun joriy foydalanuvchini `docker` guruhiga qo'shadi. Bu o'zgarish kuchga kirishi uchun tizimdan chiqib, qayta kirish kerak.

-----

### 2\. Dockerfile bilan ishlash

`Dockerfile` asosida loyihangiz uchun maxsus image yaratiladi.

* **Image yaratish (build):**
  ```bash
  docker build -t my-spring-app .
  ```
    - `docker build`: Image yaratish uchun asosiy buyruq.
    - `-t my-spring-app`: Yaratilayotgan image'ga nom (`tag`) beradi. Bu yerda nom `my-spring-app`.
    - `.`: `Dockerfile` joriy papkada ekanligini bildiradi.

-----

### 3\. Docker Compose Hayotiy Sikli Buyruqlari (Eng Ko'p Ishlatiladiganlar)

Bu buyruqlar `docker-compose.yml` fayli joylashgan papkada ishga tushirilishi kerak.

* **Barcha servislarni yaratish va ishga tushirish:**

  ```bash
  docker compose up
  ```

  `docker-compose.yml` faylidagi barcha servislarni (konteynerlarni) yaratadi va ishga tushiradi. Loglar (jarayonlar) to'g'ridan-to'g'ri terminalda ko'rinib turadi. `Ctrl+C` bosilsa, konteynerlar to'xtaydi.

* **Servislarni fonda (background) ishga tushirish:**

  ```bash
  docker compose up -d
  ```

    - `-d` (detached mode): Konteynerlarni orqa fonda ishga tushiradi va terminalni bo'shatadi. Bu eng ko'p ishlatiladigan usul.

* **Image'larni qayta qurish bilan birga ishga tushirish:**

  ```bash
  docker compose up --build
  ```

    - `--build`: `docker compose up` buyrug'ini ishlatishdan oldin `Dockerfile` (`build: .` yozilgan servislar uchun) asosida image'larni majburan qaytadan quradi. Kodingizga o'zgarish kiritganingizda shu buyruqni ishlatasiz.

* **Barcha servislarni to'xtatish:**

  ```bash
  docker compose stop
  ```

  Ishlayotgan konteynerlarni to'xtatadi, lekin ularni o'chirmaydi. Ma'lumotlar saqlanib qoladi.

* **To'xtatilgan servislarni qayta ishga tushirish:**

  ```bash
  docker compose start
  ```

  `docker compose stop` bilan to'xtatilgan konteynerlarni qayta ishga tushiradi.

* **Barcha servislarni to'xtatish va o'chirish:**

  ```bash
  docker compose down
  ```

  Eng muhim buyruqlardan biri. Konteynerlarni to'xtatadi, ularni butunlay o'chiradi va ular bog'langan tarmoqlarni ham yo'q qiladi. `volumes` (`postgres_data` kabi) o'chirilmaydi.

-----

### 4\. Konteynerlarni Boshqarish va Nazorat Qilish

* **Ishlayotgan konteynerlar ro'yxatini ko'rish:**

  ```bash
  docker ps
  ```

  Hozirda ishlab turgan barcha konteynerlar haqida ma'lumot (ID, nomi, portlari) beradi.

* **Barcha (ishlayotgan va to'xtagan) konteynerlarni ko'rish:**

  ```bash
  docker ps -a
  ```

    - `-a` (all): To'xtatilgan konteynerlarni ham ro'yxatda ko'rsatadi.

* **Ma'lum bir konteynerning loglarini (jarayonlarini) ko'rish:**

  ```bash
  docker logs my-spring-boot-app
  ```

  `my-spring-boot-app` nomli konteynerning barcha loglarini chiqaradi. Dasturdagi xatoliklarni topish uchun juda muhim.

* **Loglarni real vaqtda kuzatib borish:**

  ```bash
  docker logs -f my-spring-boot-app
  ```

    - `-f` (follow): Konteyner loglarini real vaqtda (live) kuzatib borish imkonini beradi.

* **Konteyner ichiga kirish (interaktiv terminal):**

  ```bash
  docker exec -it my-spring-boot-app /bin/sh
  ```

    - `docker exec`: Ishlab turgan konteyner ichida buyruq bajaradi.
    - `-it`: Interaktiv terminal ochish imkonini beradi.
    - `my-spring-boot-app`: Konteyner nomi.
    - `/bin/sh`: Konteyner ichida ishga tushiriladigan buyruq (shell). Bu orqali konteyner ichidagi fayllarni ko'rish, sozlamalarni tekshirish mumkin.

-----

### 5\. Tizimni Tozalash Buyruqlari

Vaqt o'tishi bilan Docker tizimda ko'plab keraksiz image, konteyner va volume'larni to'plab qo'yishi mumkin.

* **Barcha to'xtatilgan konteynerlarni o'chirish:**

  ```bash
  docker container prune
  ```

* **Ishlatilmayotgan (dangling) image'larni o'chirish:**

  ```bash
  docker image prune
  ```

* **Ishlatilmayotgan volume'larni o'chirish:**

  ```bash
  docker volume prune
  ```

* **Barcha keraksiz narsalarni bir buyruqda tozalash (ehtiyot bo'ling\!):**

  ```bash
  docker system prune -a
  ```

  Barcha to'xtatilgan konteynerlarni, ishlatilmayotgan tarmoqlarni, barcha `dangling` va hatto bitta ham konteynerga bog'lanmagan image'larni o'chiradi. Bu buyruq tizimda ancha joy bo'shatadi.

* **docker-compose.yml fayli sizning butun dasturingizni — Spring Boot ilovasi, PostgreSQL ma'lumotlar bazasi, Redis keshi va ma'lumotlar bazasini boshqarish uchun pgAdmin interfeysini — birgalikda ishga tushirish uchun yozilgan "retsept"dir. 📜
   Ushbu fayl bir nechta konteynerni yagona buyruq bilan (docker compose up) boshqarish imkonini beradi.**

Faylning Umumiy Tuzilishi
Fayl to'rtta asosiy qismdan iborat: services, volumes, networks va version.

version: '3.8': Bu Docker Compose fayl formati versiyasini bildiradi.

services:: Bu asosiy blok bo'lib, har bir ishga tushiriladigan konteyner alohida "xizmat" (service) sifatida ta'riflanadi. Sizda to'rtta servis bor: app, db, redis, va pgadmin.

volumes:: Bu blok ma'lumotlarni doimiy saqlash uchun ishlatiladigan "jildlar"ni (volumes) e'lon qiladi.

networks:: Bu blok konteynerlar o'zaro "gaplashishi" uchun maxsus virtual tarmoq yaratadi.

Har bir Servisning Tahlili
1. app (Sizning Spring Boot Ilovangiz)
   Bu siz yozgan asosiy dastur uchun servis.

container_name: my-spring-boot-app: Konteynerga o'qilishi oson bo'lgan my-spring-boot-app nomini beradi.

build: .: Bu Docker'ga image'ni Docker Hub'dan tortib olish o'rniga, joriy papkadagi (.) Dockerfile asosida qurishni (build qilishni) buyuradi.

ports: - "8080:8080": Tashqi 8080-portni (sizning kompyuteringiz porti) konteynerning ichki 8080-portiga bog'laydi. Bu sizga brauzerda http://localhost:8080 orqali ilovangizga kirish imkonini beradi.

environment:: Konteyner ichiga muhit o'zgaruvchilarini (environment variables) o'tkazadi. Sizning Spring Boot ilovangiz application.properties fayli orqali shu o'zgaruvchilarni o'qib, ma'lumotlar bazasi va Redis'ga ulanadi.

depends_on: [db, redis]: Bu juda muhim qism. U app servisining faqat db (PostgreSQL) va redis servislari to'liq ishga tushganidan keyingina start olishini ta'minlaydi. Bu ilovangiz bazaga ulanishga harakat qilganida baza hali tayyor bo'lmasligi muammosining oldini oladi.

networks: [mynetwork]: Bu servisni mynetwork nomli tarmoqqa ulaydi.

2. db (PostgreSQL Ma'lumotlar Bazasi)
   Bu servis ma'lumotlaringizni saqlash uchun mas'ul.

image: postgres:15-alpine: Docker Hub'dan PostgreSQL'ning 15-versiyasiga tegishli, kichik hajmli alpine image'ini tortib oladi.

environment:: Bu o'zgaruvchilar PostgreSQL konteynerining o'zini sozlaydi. U utkirdb nomli baza, myuser nomli foydalanuvchi va mypassword parolini yaratadi.

volumes: - postgres_data:/var/lib/postgresql/data: Bu yana bir juda muhim qism. U postgres_data nomli jildni konteyner ichidagi /var/lib/postgresql/data papkasiga (PostgreSQL ma'lumotlarni shu yerda saqlaydi) bog'laydi. Bu degani, siz docker compose down qilib konteynerni o'chirsangiz ham, barcha ma'lumotlaringiz saqlanib qoladi. Keyingi safar up qilganingizda, ma'lumotlar joyida bo'ladi.

networks: [mynetwork]: Bu servis ham mynetwork tarmog'iga ulangan.

3. redis (Redis Kesh Servisi)
   Bu servis ma'lumotlarni tezkor xotirada (RAM) saqlab, dastur ishlashini tezlashtirish uchun ishlatiladi.

image: redis:7-alpine: Redis'ning 7-versiyasiga tegishli alpine image'ini tortib oladi.

networks: [mynetwork]: Boshqa servislar bilan aloqa qilish uchun mynetwork'ga ulangan.

4. pgadmin (Ma'lumotlar Bazasi uchun Grafik Interfeys)
   Bu servis sizga PostgreSQL bazasini brauzer orqali qulay boshqarish imkonini beradi.

image: dpage/pgadmin4: pgAdmin'ning rasmiy image'ini tortib oladi.

ports: - "5050:80": Kompyuteringizdagi 5050-portni pgAdmin'ning ichki 80-portiga bog'laydi. Bu sizga http://localhost:5050 orqali unga kirish imkonini beradi.

environment:: pgAdmin'ga kirish uchun standart login (admin@example.com) va parol (admin) o'rnatadi.

depends_on: [db]: Bu pgAdmin'ning faqat db (baza) ishga tushgandan so'ng start olishini ta'minlaydi.

Umumiy Ishlash Mantiqi
Siz docker compose up buyrug'ini berganingizda:

Docker mynetwork nomli virtual tarmoq yaratadi.

db va redis servislari birinchi bo'lib ishga tushadi.

Ular tayyor bo'lgach, app va pgadmin servislari start oladi.

app servisi mynetwork orqali db va redis xost nomlari yordamida ularga ulanadi. Masalan, SPRING_DATASOURCE_URL'dagi jdbc:postgresql://db:5432/... qismidagi db — bu postgres-db konteynerining tarmoqdagi nomi.

Bu fayl sizga murakkab tizimni bitta konfiguratsiya fayli orqali osongina boshqarish va boshqa dasturchilar bilan bo'lishish imkonini beradi.