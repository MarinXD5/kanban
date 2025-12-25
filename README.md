# Kanban aplikacija

Ovaj repozitorij sadrži **full-stack Kanban aplikaciju** koja se sastoji od Spring Boot backend servisa i frontend korisničkog sučelja.
Projekt implementira sve funkcionalne i nefunkcionalne zahtjeve definirane u dokumentu **`java_zadatak.pdf`**.

---

## 📁 Struktura projekta

```text
kanban/
├── kanban/                 # Backend (Spring Boot)
├── kanban-ui/              # Frontend (React)
└── .github/
    └── workflows/
        └── backend-ci.yml  # GitHub Actions CI/CD pipeline
```

---

## 🧩 Pregled modula

### 1. kanban (Backend)

Java Spring Boot aplikacija koja pruža potpuno funkcionalan Kanban REST API.

**Glavne značajke:**

* REST CRUD API za zadatke
* Paginacija, sortiranje i filtriranje
* Validacija ulaznih podataka i globalni error handling
* WebSocket/STOMP notifikacije za promjene nad zadacima
* Automatski generiran OpenAPI 3 / Swagger UI
* Stateless JWT autentikacija
* Unit i integracijski testovi (≥ 80 % coverage)
* Dockerizirana aplikacija i baza podataka
* Actuator endpointi za observability

**Korištene tehnologije:**

* Java 17+
* Spring Boot
* Spring Data JPA (Hibernate)
* Spring Security (JWT)
* Spring WebSocket + STOMP
* PostgreSQL / MySQL
* Flyway ili Liquibase
* JUnit 5, Mockito, Testcontainers
* Docker i Docker Compose

---

### 2. kanban-ui (Frontend)

Frontend aplikacija u Reactu koja komunicira s backendom.

**Odgovornosti:**

* Prikaz Kanban boarda (`To Do`, `In Progress`, `Done`)
* Kreiranje, ažuriranje i brisanje zadataka
* Reakcija na real-time WebSocket događaje

> Frontend je u potpunosti odvojen od backenda i komunicira isključivo putem REST i WebSocket API-ja.

---

## 🔌 API pregled (Backend)

### 📝 Task API

Osnovni path: `/api/tasks`

| Metoda | Endpoint          | Opis                                  |
| ------ | ----------------- | ------------------------------------- |
| GET    | `/api/tasks`      | Dohvat liste zadataka (paging/filter) |
| GET    | `/api/tasks/{id}` | Dohvat zadatka po ID-u                |
| POST   | `/api/tasks`      | Kreiranje novog zadatka               |
| PUT    | `/api/tasks/{id}` | Potpuni update (optimistic locking)   |
| PATCH  | `/api/tasks/{id}` | Djelomični update (JSON Merge Patch)  |
| DELETE | `/api/tasks/{id}` | Brisanje zadatka                      |

**WebSocket topic:**

```
/topic/tasks
```

Svaka promjena nad zadatkom emitira real-time događaj.

---

### 👤 User API

Osnovni path: `/api/users`

| Metoda | Endpoint                 | Opis                          |
| ------ | ------------------------ | ----------------------------- |
| GET    | `/api/users`             | Dohvat svih korisnika         |
| PUT    | `/api/users/change/{id}` | Ažuriranje podataka korisnika |

---

### 📁 Project API

Osnovni path: `/api/projects`

| Metoda | Endpoint                                   | Opis                             |
| ------ | ------------------------------------------ | -------------------------------- |
| POST   | `/api/projects`                            | Kreiranje novog projekta         |
| POST   | `/api/projects/{projectId}/users/{userId}` | Dodavanje korisnika u projekt    |
| DELETE | `/api/projects/{projectId}/users/{userId}` | Uklanjanje korisnika iz projekta |
| GET    | `/api/projects/user/{userId}`              | Dohvat projekata za korisnika    |
| GET    | `/api/projects/{projectId}/users`          | Dohvat korisnika na projektu     |

---

## 🔐 Sigurnost

* Stateless JWT autentikacija
* `/api/**` endpointi su zaštićeni
* Swagger i OpenAPI endpointi su javni:

  * `/swagger-ui.html`
  * `/v3/api-docs/**`

---

## 📄 API dokumentacija

Swagger UI je dostupan na:

```
http://localhost:8080/swagger-ui.html
```

---

## 🧪 Testiranje

Backend uključuje:

* **Unit testove** (service i mapper sloj)
* **Integracijske testove** s Testcontainers (PostgreSQL)
* **WebSocket testove** koji potvrđuju broadcast nakon POST zahtjeva

Minimalni test coverage: **≥ 80 %**

---

## 🐳 Docker podrška

Projekt sadrži:

* Multi-stage Dockerfile za backend
* `docker-compose.yml` za aplikaciju i bazu podataka

### Pokretanje s Docker Composeom

```bash
docker-compose up --build
```

---

## ⚙️ CI/CD – GitHub Actions

CI/CD pipeline definiran je u:

```
.github/workflows/backend-ci.yml
```

**Pipeline koraci:**

1. Build backend aplikacije
2. Pokretanje testova
3. Provjera test coverage-a (≥ 80 %)
4. Build Docker image-a

Pipeline se automatski izvršava na push i pull request događaje.

---

## 🚀 Lokalno pokretanje

### Backend

```bash
cd kanban
./mvnw spring-boot:run
```

### Frontend

```bash
cd kanban-ui
npm install
npm start
```

---

## 📄 Licenca

Projekt je izrađen u edukacijske i evaluacijske svrhe.
