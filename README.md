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

## Create and run with image pulled from docker hub(I created new docker-compose-dev file to make creation process faster)

```bash
docker-compose up -f docker-compose-dev.yml
```
```bash
docker-compose -f docker-compose-dev.yml run osint-app-dev --scan -o stdout microsoft.com
```

```bash
docker-compose run osint-app-dev --rm osint-app-dev --retrieve -o stdout <SCAN_ID>
```