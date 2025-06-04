# OSINT-Web-Application

## Building the osint-app Container

### Build the application JAR
```bash
mvn clean install
```
### Build Docker containers

```bash
docker-compose build
```
### Start all services (osint-app will start with default params)
```bash
docker-compose up
```

### Run in Scanning Mode
```bash
docker-compose run --rm osint-app --scan -o stdout microsoft.com
```

### Run in Retrieve Mode
```bash
docker-compose run --rm osint-app --retrieve -o stdout <SCAN_ID>
```

## Docker.hub
To load osint-app from docker.hub
```bash
docker pull rymishal/osint-app:latest
```