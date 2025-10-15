package com.example.projetfind.etude.error;

// NOTE: Logger SLF4J (correct) → permet d'utiliser log.error(...)
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
// pour le JSON SOIT AUTOMATIQUE 
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class ErrorHondler {

    // pour voir la vrai cause coté serveur genre terminal
    // NOTE: on utilise SLF4J (et pas java.util.logging)
    private static final Logger log = LoggerFactory.getLogger(ErrorHondler.class);

    // ’affichage de petits détails dans les erreurs 500 côté API
    @Value("${app.errors.include-details:false}")
    private boolean includeDetails;

    // gestion des erreurs 404 ex en mettant un faux :  /api/users/999
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String,Object>> notFound(EntityNotFoundException ex, HttpServletRequest request){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(

            // date et heure exate de l'erreur
            "timestamp", Instant.now(),
            // code HTTP renvoyé
            "status", HttpStatus.NOT_FOUND.value(),
            // type d'erreur
            "error", "Utilisateur introuvable",
            // message précis
            "message", ex.getMessage(),
            // genre sur quel URL il a échoué
            "path", request.getRequestURI()

        ));
    }

    // gestion des erreurs 400 règle métier simple  Requête invalide (ex: champs vides)
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String,Object>> badRequest(ValidationException ex, HttpServletRequest request){
        // pour envoyer une réponse avec le status 400
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
            "timestamp", Instant.now(),
            "status", HttpStatus.BAD_REQUEST.value(),
            // ex: "Email obligatoire
            "error", "Requête invalide",
            "message", ex.getMessage(),
            // NOTE: utile pour déboguer : sur quelle route ça a échoué
            "path", request.getRequestURI()
        ));
    }

    // gestion des erreurs 409 si y'a un doublon genre conflit
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<Map<String,Object>> conflict(DuplicateResourceException ex, HttpServletRequest request){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
            "timestamp", Instant.now(),
            "status", HttpStatus.CONFLICT.value(),
            "error", "Doublons",
            "message", ex.getMessage(),
            "path", request.getRequestURI()
        ));
    }

    // concerne les erreurs de validation 422 par si email invalide, password < 6, etc.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,Object>> handleBeanValidation(MethodArgumentNotValidException ex, HttpServletRequest request){
        // pour transformer en sous forme de liste qui sera lisible
        List<Map<String, String>> details = ex.getBindingResult().getFieldErrors().stream()
            //  pour chaque erreur on fabrique un mini-objet
            .map(err -> Map.of(
                "field", err.getField(),
                "message", err.getDefaultMessage() == null ? "Invalide" : err.getDefaultMessage()
            ))
            .toList();

        return ResponseEntity
            .status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(Map.of(
                "timestamp", Instant.now(),
                "status", HttpStatus.UNPROCESSABLE_ENTITY.value(),
                // ⚠️ correction clé: "error"
                "error", "Erreur de validation",
                // message globale
                "message", "Certains champs sont invalides",
                "path", request.getRequestURI(),
                // liste qui sera exploitable dans la partie front
                "details", details
            ));
    }

    //  500 erreur interne soit y'a un bug 
    @ExceptionHandler(Exception.class) 
    public ResponseEntity<Map<String, Object>> generic (Exception ex, HttpServletRequest request){

        // on écrit un log d’erreur côté serveur (console)
        log.error(
            "Erreur 500 sur {} {} : {}",
            request.getMethod(),
            request.getRequestURI(),
            // ex  = nom + message
            ex.toString(),
            // ET on passe aussi l'objet ex pour avoir la stack complète
            ex
        );

        // ici c'est pour garder l'ordre des clés
        var body = new java.util.LinkedHashMap<String, Object>();
        body.put("timestamp", Instant.now());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Erreur interne du serveur");
        // on ne révèle pas la cause au client
        body.put("message", "Une erreur inattendue est survenue");
        body.put("path", request.getRequestURI());

        // optionnel c'est pour montrer plus d'info c'est pratique en local
        if (includeDetails){
            body.put("exception", ex.getClass().getSimpleName());
            String first = ex.getStackTrace().length > 0 ? ex.getStackTrace()[0].toString() : "no-trace";
            body.put("trace", first);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    // gestion des eereur 500 
    // pour toutes les autres erreurs 
    // NOTE: ces deux commentaires appartenaient à un 2e handler 500 en doublon.
    // NOTE: on les conserve ici pour ne pas perdre ton intention, mais on garde UN SEUL handler 500 (celui juste au-dessus).

    // pour signaler un doublon
    // NOTE: on rend la classe "public static" pour pouvoir la lancer depuis un autre package (ex: service)
    public static class DuplicateResourceException extends RuntimeException {
        public DuplicateResourceException(String message) { super(message); }
    }
}
