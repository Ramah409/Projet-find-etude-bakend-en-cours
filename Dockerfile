# image de base avec java 21
FROM eclipse-temurin:21-jdk

# dossier d travail dans le conteneur
WORKDIR /app

# pour copier tout le projet Maven dans l'image 
COPY . .

# pour donner aux exécution 
RUN chmod +x mvnw

# construction de l'application
RUN ./mvnw clean package -DskipTests

# pour exposer le port  HTTP genre par spring Boot
EXPOSE 8080

# pour lancer jar spring boot 
CMD ["java", "-jar", "target/mon-app.jar"]