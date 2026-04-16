// c'est fichier recoit DTO genre registerRequest et LoginRequest il fait des 
// verification et communique avec la BDD via UserRepository
package com.example.projetfind.etude.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.projetfind.etude.dto.AuthResponse;
import com.example.projetfind.etude.dto.LoginRequest;
import com.example.projetfind.etude.dto.RegisterRequest;
import com.example.projetfind.etude.entity.UserEntity;
import com.example.projetfind.etude.error.ErrorHondler;
import com.example.projetfind.etude.repository.UserRepository;
import com.example.projetfind.etude.security.JwtService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;



@Service
public class AuthService {

    // pour avoir accés a la table user sur mon pgAdmin
    private final UserRepository userRepository;

    // c'est outile pour transformer un mot de passe en hash et comparer
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // pour garder une reference  au service JWT qui sera injecter par le constructeur
    private final JwtService jwtService;

    // mon constructeur on injecte UserRepository + JwtService
    public AuthService(UserRepository userRepository, JwtService jwtService){
        this.userRepository = userRepository;
        this.jwtService =jwtService;
    }

    // les inscriptions 
    public AuthResponse register(RegisterRequest req){
        // vérification des champs sinon 400 
        // req c'est comme une boite qui transportte les données depuis angular jusqu'a mon bakend 
        // c'est l'abrevation de requete bref il recoit les infos tapée dans le formulaire
        // sans req mon bakend ne saurait pas ce que mon utilisateur a tapé
        // mais avec je peux recuperer les infos pour les traiter
        if (req.name == null || req.name.isBlank())
            throw new ValidationException("Nom obligatoire");
        if (req.email == null || req.email.isBlank())
            throw new ValidationException("Email obligatoire");
        if (req.username == null || req.username.isBlank())
            throw new ValidationException("Pseudo obligatoire");
        if (req.password == null || req.password.length() < 8)
           throw new ValidationException("Mot de passe minimum 8 caractères");

         // vérification des champs pour éviter des doublons
        if (userRepository.existsByEmail(req.email))
           throw new ErrorHondler.DuplicateResourceException("Email déjà utilisé");   // ← 409
           
        if (userRepository.existsByUsername(req.username))
          throw new ErrorHondler.DuplicateResourceException("Pseudo déjà utilisé");  // ← 409


    // pour crée un objet UserEntity genre celui qui ira en base

    UserEntity nouvelutilisateur = new UserEntity();
    nouvelutilisateur.setName(req.name);
    nouvelutilisateur.setEmail(req.email);
    nouvelutilisateur.setUsername(req.username);

    // pour eviter de stoker le mot de passe en claire
    nouvelutilisateur.setPasswordHash(encoder.encode(req.password));

    // le sauvegarder en base
    UserEntity saved = userRepository.save(nouvelutilisateur);

    // info public sans le mot de passe
    return new AuthResponse(saved.getId(), saved.getName(), saved.getEmail(), saved.getUsername());

    }

    // la partie connexion 
public AuthResponse login(LoginRequest req){
    // vérification simple
    if (req.identifier == null || req.identifier.isBlank())
       throw new ValidationException("Identifiant obligatoire");
    if (req.password == null || req.password.isBlank())
       throw new ValidationException("Mot de passe obligatoire");

// pour chercher l'utilisateur par son email ou son urname
UserEntity nouvelutilisateur = userRepository.findByEmailOrUsername(req.identifier, req.identifier)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable"));

// pour vérifier que le mot de passe correspond au hash stoké
if (!encoder.matches(req.password, nouvelutilisateur.getPasswordHash())) {
     throw new ValidationException("mot de passe incorrect");
}

// concernant le token
// pour determiner le role logique coté app 

// code qui me posait probleme 
//String role = "admin".equalsIgnoreCase(nouvelutilisateur.getUsername()) ? "ADMIN": "USER";

// Rôle déterminé sans champ en base : admin si username = "admin" OU si email est ajouté à une liste d’autorisations

//  ADMIN si username == "admin" OU si l'email est demba@gmail.com
String role = (
    "admin".equalsIgnoreCase(nouvelutilisateur.getUsername())
    || "ramahsissoko@gmail.com".equalsIgnoreCase(nouvelutilisateur.getEmail())
) ? "ADMIN" : "USER";


// pour generer un JWT signé
String token = jwtService.generateToken(
    // id → sub
    nouvelutilisateur.getId(),
    // username (claim)
    nouvelutilisateur.getUsername(),
    // role (claim)
    role
);

// construison de la réponse envoyer au front
AuthResponse resp = new AuthResponse(
    nouvelutilisateur.getId(),
    nouvelutilisateur.getName(),
    nouvelutilisateur.getEmail(),
    nouvelutilisateur.getUsername()
);
// je complete avec le role et le token
// soit ADMIN ou USER
resp.role = role; 
resp.token = token;

// retour au controlleur il sera converti en json automatiquement
return resp;

}

}
