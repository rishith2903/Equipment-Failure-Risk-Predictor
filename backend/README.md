# Equipment Failure Risk Predictor - Backend

AI-powered industrial equipment health monitoring system built with Spring Boot.

## 🚀 Features

- Complete REST API for equipment management
- AI-based risk prediction engine with weighted formula
- Real-time sensor log processing
- Automatic risk event tracking
- PostgreSQL database with Flyway migrations
- Comprehensive error handling and validation
- Ready for deployment on Render/Railway

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 15+
- (Optional) Docker & Docker Compose

## ⚙️ Setup Instructions

### Option 1: Local PostgreSQL

1. **Install and start PostgreSQL**

2. **Create database:**
   ```bash
   createdb equipment_predictor
   ```

3. **Configure database connection:**
   Edit `src/main/resources/application-dev.properties` if needed

4. **Build and run:**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

Server will start at `http://localhost:8080`

### Option 2: Docker Compose (Recommended)

1. **Start PostgreSQL with Docker:**
   ```bash
   cd .. # Go to project root
   docker-compose up -d
   ```

   This starts PostgreSQL on port 5432 and PgAdmin on port 5050

2. **Run application:**
   ```bash
   mvn spring-boot:run
   ```

##📚 API Endpoints

### Equipment Management
- `GET /api/v1/equipment` - Get all equipment
- `POST /api/v1/equipment` - Create new equipment
- `GET /api/v1/equipment/{id}` - Get equipment by ID
- `PUT /api/v1/equipment/{id}` - Update equipment
- `DELETE /api/v1/equipment/{id}` - Delete equipment

### Sensor Logs
- `POST /api/v1/equipment/{id}/logs` - Add sensor log
- `GET /api/v1/equipment/{id}/logs` - Get sensor logs (supports filtering)
- `GET /api/v1/equipment/{id}/logs/latest` - Get latest sensor log

### Risk & Alerts
- `GET /api/v1/equipment/{id}/risk/latest` - Get latest risk assessment
- `GET /api/v1/equipment/{id}/risk/history` - Get risk history
- `GET /api/v1/alerts?level={LEVEL}` - Get alerts (optional level filter)
- `GET /api/v1/dashboard/stats` - Get dashboard statistics

## 🧮 Risk Calculation Formula

The AI engine uses a weighted formula to calculate risk scores:

```
Normalized values (0-100):
- Temperature: 0-150°C range
- Vibration: 0-50 mm/s range  
- Load: 0-100% range

Risk Score = 0.4 × Temp + 0.35 × Vibration + 0.25 × Load

Risk Levels:
- LOW: 0-39
- MEDIUM: 40-64
- HIGH: 65-84
- CRITICAL: 85-100
```

## 🗄️ Database Schema

See `src/main/resources/db/migration/V1__initial_schema.sql` for complete schema.

### Tables:
- **equipment**: Equipment information
- **sensor_log**: Sensor readings with timestamps
- **risk_event**: Calculated risk events for auditing

## 🌱 Seed Data

Load sample data:
```bash
psql -d equipment_predictor -f src/main/resources/seed-data.sql
```

This creates 10 equipment items with various risk levels.

## 🔧 Environment Variables

For production deployment:

```properties
DATABASE_URL=jdbc:postgresql://host:port/database
DATABASE_USERNAME=username
DATABASE_PASSWORD=password
SPRING_PROFILE=prod
PORT=8080
```

## 🐳 Docker Deployment

Build Docker image:
```bash
docker build -t equipment-predictor-backend .
```

Run container:
```bash
docker run -p 8080:8080 \
  -e DATABASE_URL=your_db_url \
  -e DATABASE_USERNAME=your_username \
  -e DATABASE_PASSWORD=your_password \
  backend
```

## 🚀 Deploying to Render

1. Create new Web Service on Render
2. Connect your GitHub repository
3. Configure:
   - Build Command: `mvn clean install -DskipTests`
   - Start Command: `java -jar target/*.jar`
4. Add environment variables (DATABASE_URL, etc.)
5. Create PostgreSQL database on Render
6. Deploy!

## 📊 API Testing

Import `Equipment-Predictor-API.postman_collection.json` into Postman for pre-configured API requests.

## 🛠️ Development

**Run tests:**
```bash
mvn test
```

**Build JAR:**
```bash
mvn clean package
```

**Run with specific profile:**
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

## 📝 Project Structure

```
src/main/java/com/equipmentpredictor/
├── config/          # CORS and web configuration
├── controller/      # REST API controllers
├── dto/             # Data Transfer Objects
├── exception/       # Global exception handling
├── model/           # JPA entities
├── repository/      # Spring Data repositories
└── service/         # Business logic & AI engine
```

## 🤝 Contributing

This is a demonstration project. Feel free to fork and modify!

## 📄 License

MIT License - feel free to use for your projects.
