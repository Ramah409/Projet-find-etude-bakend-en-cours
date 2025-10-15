package com.example.projetfind.etude.controller;
// fichier logique qui correspond au chemin de mon fichier.

import com.example.projetfind.etude.dto.UserDto;           
import com.example.projetfind.etude.service.UserService;   

import org.springframework.http.HttpStatus;                 
import org.springframework.http.ResponseEntity;             
import org.springframework.web.bind.annotation.*;          


import java.util.List;                                      

/**
 
 * - Il reçoit les requêtes HTTP (GET/POST/PUT/DELETE) du client (Postman/Angular).
 * - Il appelle le SERVICE qui contient la logique métier (vérifs, accès BDD via repository).
 * - Il renvoie des réponses JSON + codes HTTP.
 */
@RestController                              // Indique que cette classe expose des endpoints REST (JSON)
@RequestMapping("/api/users")                // Préfixe commun pour toutes les routes de ce contrôleur
public class UserController {                // ✅ Nom de la classe = UserController (avec 2 "l")

    // Dépendance vers la couche service (logique métier)
    private final UserService userService;

    // ✅ Constructeur : le nom DOIT être EXACTEMENT le même que la classe
    public UserController(UserService userService){
        this.userService = userService;      // On enregistre la dépendance pour l’utiliser dans les méthodes
    }
   

    //  LISTER TOUS LES UTILISATEURS 
    @GetMapping                              // Associe cette méthode à GET /api/users
    public List<UserDto> getAll(){
        // Appel au service → SELECT * FROM users → conversion en DTO
        return userService.getAll();         // Réponse : une liste JSON
    }

    // ===================== LIRE UN UTILISATEUR PAR ID =====================
    @GetMapping("/{id}")                     // Associe à GET /api/users/123 (exemple)
    public UserDto getById(@PathVariable Long id) {
        // @PathVariable récupère {id} depuis l’URL
        return userService.getById(id);      // Si non trouvé → 404 géré par ErrorHandler
    }

    // ===================== CRÉER UN NOUVEL UTILISATEUR =====================
    @PostMapping                             // Associe à POST /api/users
    public ResponseEntity<UserDto> create(@RequestBody UserDto dto){
        // @RequestBody convertit le JSON du body en objet Java (UserDto)
        // Ex JSON à envoyer :
        // { "name": "Ramata", "email": "ramata@example.com", "username": "mai" }

        UserDto created = userService.create(dto); // Vérifs (email/username), INSERT en BDD

        // On renvoie 201 Created + le user créé (avec son id)
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ===================== METTRE À JOUR UN UTILISATEUR =====================
    @PutMapping("/{id}")                     // Associe à PUT /api/users/123
    public UserDto update(@PathVariable Long id, @RequestBody UserDto dto){
        // On met à jour name/email/username de l’utilisateur {id}
        // Le service vérifie l’unicité si tu changes email/username
        return userService.update(id, dto);  // Réponse : l’objet à jour
    }

    // SUPPRIMER UN UTILISATEUR
    @DeleteMapping("/{id}")                  // Associe à DELETE /api/users/123
    public ResponseEntity<Void> delete(@PathVariable Long id){
        userService.delete(id);              // 404 si l’id n’existe pas (géré par ErrorHandler)
        return ResponseEntity.noContent().build(); // 204 No Content si OK
    }
}
