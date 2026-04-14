package com.example.projetfind.etude.controller;
// fichier logique qui correspond au chemin de mon fichier.
/**
 
 *  reçoit les requêtes HTTP (GET/POST/PUT/DELETE) du client (Postman/Angular).
 *  appelle le SERVICE qui contient la logique métier (vérifs, accès BDD via repository).
 * renvoie des réponses JSON + codes HTTP.
 */

import com.example.projetfind.etude.dto.UserDto;           
import com.example.projetfind.etude.service.UserService;   

import org.springframework.http.HttpStatus;                 
import org.springframework.http.ResponseEntity;             
import org.springframework.web.bind.annotation.*;          


import java.util.List;                                      


@RestController                              
@RequestMapping("/api/users")                
public class UserController {             

    // Dépendance vers la couche service (logique métier)
    private final UserService userService;

    // mon Constructeur : le nom DOIT être EXACTEMENT le même que la classe
    public UserController(UserService userService){
         // j' enregistre la dépendance pour l’utiliser dans les méthodes
        this.userService = userService;     
    }
    //  LISTER TOUS LES UTILISATEURS 
    @GetMapping                              
    public List<UserDto> getAll(){
        // Appel au service 
        return userService.getAll();        
    }
    // LIRE UN UTILISATEUR PAR ID 
    @GetMapping("/{id}")                     
    public UserDto getById(@PathVariable Long id) {
        return userService.getById(id);      
    }
    // CRÉER UN NOUVEL UTILISATEUR 
    @PostMapping  
    // @RequestBody convertit le JSON du body en objet Java (UserDto)
        // Ex JSON à envoyer :
        // { "name": "Ramata", "email": "ramata@example.com", "username": "mai" }                           
    public ResponseEntity<UserDto> create(@RequestBody UserDto dto){
        // Vérifs (email/username), INSERT en BDD
        UserDto created = userService.create(dto); 

        // je renvoie 201 Created + le user créé (avec son id)
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    //  METTRE À JOUR UN UTILISATEUR 
    @PutMapping("/{id}")                     
    public UserDto update(@PathVariable Long id, @RequestBody UserDto dto){
        // Le service vérifie l’unicité si tu changes email/username
        return userService.update(id, dto);  // Réponse : l’objet à jour
    }

    // SUPPRIMER UN UTILISATEUR
    @DeleteMapping("/{id}")                  
    public ResponseEntity<Void> delete(@PathVariable Long id){
         // 404 si l’id n’existe pas (géré par ErrorHandler)
        userService.delete(id); 
        // 204 No Content si OK            
        return ResponseEntity.noContent().build(); 
    }
}
