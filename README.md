# MedFind — Medicine Availability & Price Finder

MedFind is a full-stack medicine finder with a React/Vite frontend and a Spring Boot REST API. Patients can search a medicine name or prescription code, browse eight categories with at least ten sample listings apiece, compare stock/prices at participating pharmacies, and find medicines in the same generic group. For delivery orders, patients type a delivery address at checkout; the app does not request device location. **Cash on delivery is the only payment method**. Pharmacists have a role-protected dashboard to update their own prices and stock and review delivery orders.

## Requirements

- Java 17+
- Maven 3.8+
- Node.js 18+ and npm

## Start the backend

```powershell
cd backend
mvn spring-boot:run
```

The API starts at `http://localhost:8080/api`. Without database environment variables, the development H2 database is in-memory and populated with sample Bengaluru pharmacies, medicines, and stock on startup. The H2 console is available at `http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:mem:medfind`, username `sa`, blank password).

## Start the frontend

In a second terminal:

```powershell
cd folder
npm install
npm run dev
```

Open the local Vite URL printed by the command (normally `http://localhost:5173`). To use a different backend URL, copy `folder/.env.example` to `folder/.env` and change `VITE_API_URL`.

## Deploy to Render

1. Push this repository to GitHub.
2. In the [Render Dashboard](https://dashboard.render.com), choose **New +** → **Blueprint** and connect this repository.
3. Render reads `render.yaml` and creates the `medicine-pro-api` backend and `medicine-pro-frontend` static site. Select **Apply** to deploy them.
4. When the deploy finishes, open the frontend URL shown for `medicine-pro-frontend` (normally `https://medicine-pro-frontend.onrender.com`).

The Blueprint sets the frontend API URL and allows that frontend origin through backend CORS. It uses the sample in-memory H2 database by default, so records can be lost when the backend restarts or redeploys. This lets the demo deploy without sharing database credentials. For persistent Aiven MySQL, set the following backend environment variables manually in Render:

| Variable | Value |
| --- | --- |
| `SPRING_DATASOURCE_URL` | Aiven JDBC URL, for example `jdbc:mysql://<HOST>:<PORT>/<DATABASE>?sslMode=REQUIRED` |
| `SPRING_DATASOURCE_USERNAME` | `avnadmin` |
| `SPRING_DATASOURCE_PASSWORD` | Aiven database password (enter as a secret in Render; never commit it) |
| `SPRING_DATASOURCE_DRIVER_CLASS_NAME` | `com.mysql.cj.jdbc.Driver` |

The Blueprint generates `JWT_SECRET` securely and sets `CORS_ALLOWED_ORIGINS` and `H2_CONSOLE_ENABLED` for the demo deployment. Render supplies `PORT` automatically.

If Render displays different service URLs, update `VITE_API_URL` on the frontend and `CORS_ALLOWED_ORIGINS` on the backend to match. Use this setup for demos; confirm medicine and availability details with a licensed pharmacist or healthcare professional.

## Demo accounts

Register a patient account in the app to place a delivery order. Seeded pharmacist accounts:

| Pharmacy | Email | Password |
| --- | --- | --- |
| GreenLeaf Pharmacy | `admin@greenleaf.com` | `Pharmacy123!` |
| CityCare Pharmacy | `admin@citycare.com` | `Pharmacy123!` |
| Neighborhood Meds | `admin@neighborhood.com` | `Pharmacy123!` |

## API overview

- `POST /api/auth/register` and `POST /api/auth/login`
- `GET /api/medicines/search?q=...`
- `GET /api/medicines/{id}/alternatives`
- `GET /api/pharmacy/inventory` and `PUT /api/pharmacy/inventory/{id}` (pharmacist only)
- `POST /api/reservations` and `GET /api/reservations/mine` (signed in); order requests require a typed delivery address. Payment is always cash on delivery.

Orders hold the selected stock for 24 hours; the application does not process payment, arrange a real courier, or verify prescriptions. The included catalog, pharmacy listings, inventory and signing key are for local development only. Configure a persistent production database, a strong `JWT_SECRET`, a delivery provider, HTTPS, operational monitoring, and appropriate privacy/compliance controls before deployment. Do not use demo records as real medical or availability advice; prescription requirements and generic substitutions must be confirmed by a licensed pharmacist or healthcare professional.
"# medicine-pro" 
