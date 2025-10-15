// Ce fichier contient la logique métier : vérifie les règles (ex: email/username uniques)
// et utilise UserRepository pour accéder à la base.
package com.example.projetfind.etude.service;

import com.example.projetfind.etude.dto.UserDto;         // Forme échangée par l’API
import com.example.projetfind.etude.entity.UserEntity;   // Table "users"
import com.example.projetfind.etude.repository.UserRepository; // Accès BD

import jakarta.persistence.EntityNotFoundException;      // Pour 404
import jakarta.validation.ValidationException;           // Pour 400

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * SERVICE = logique métier.
 * - Convertit Entity <-> DTO
 * - Vérifie les règles (champ requis, unicité)
 * - Appelle le repository
 */
@Service
public class UserService {

    // Dépendance vers la couche d’accès aux données
    private final UserRepository userRepository;

    // Injection par constructeur : Spring fournit automatiquement un UserRepository
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /* ================== Helpers de conversion ================== */

    // Entité -> DTO (pour répondre côté API)
    private UserDto toDto(UserEntity e) {
        return new UserDto(e.getId(), e.getName(), e.getEmail(), e.getUsername(), e.getPasswordHash());
    }

    // DTO -> Entité (pour enregistrer en base)
    private UserEntity toEntity(UserDto d) {
        UserEntity e = new UserEntity();
        e.setName(d.getName());
        e.setEmail(d.getEmail());
        e.setUsername(d.getUsername()); // ✅ getUsername (pas "getUrsername")
        return e;
    }

    /* ============================ CRUD ============================ */

    // Lire tous les utilisateurs
    public List<UserDto> getAll() {
        return userRepository.findAll()          // SELECT * FROM users
                .stream()                        // transforme la liste en flux
                .map(this::toDto)                // convertit chaque entité en DTO
                .collect(Collectors.toList());   // retransforme en liste
    }

    // Lire un utilisateur par id
    public UserDto getById(Long id) {
        UserEntity e = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found id=" + id)); // 404 si absent
        return toDto(e);
    }

    

    // Créer un nouvel utilisateur
    public UserDto create(UserDto dto) {
        // 1) Champs requis
        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            throw new ValidationException("Email is required"); // 400
        }
        if (dto.getUsername() == null || dto.getUsername().isBlank()) { // ✅ getUsername + isBlank()
            throw new ValidationException("Username is required");      // 400
        }

        // 2) Unicité
        if (userRepository.existsByEmail(dto.getEmail())) {             // ✅ plus simple que ifPresent
            throw new ValidationException("Email already exists");
        }
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new ValidationException("Username already exists");
        }

        // 3) Sauvegarde
        UserEntity saved = userRepository.save(toEntity(dto)); // INSERT

        // 4) Retourner le DTO créé (avec id généré)
        return toDto(saved);
    }

    // Mettre à jour un utilisateur existant
    public UserDto update(Long id, UserDto dto) {
        // 1) Vérifie l’existence
        UserEntity existing = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found id=" + id));

        // 2) Si email changé -> recontrôle unicité
        if (!existing.getEmail().equals(dto.getEmail())) {
            if (userRepository.existsByEmail(dto.getEmail())) {
                throw new ValidationException("Email already exists");
            }
        }

        // 3) Si username changé -> recontrôle unicité
        if (!existing.getUsername().equals(dto.getUsername())) {        // ✅ getUsername
            if (userRepository.existsByUsername(dto.getUsername())) {   // ✅ existsByUsername
                throw new ValidationException("Username already exists");
            }
        }

        // 4) Appliquer les modifications
        existing.setName(dto.getName());
        existing.setEmail(dto.getEmail());
        existing.setUsername(dto.getUsername()); 

        // 5) Sauvegarder et renvoyer
        return toDto(userRepository.save(existing)); // UPDATE
    }

    // Supprimer un utilisateur
    public void delete(Long id) {
        if (!userRepository.existsById(id)) { // 404 si déjà supprimé ou inexistant
            throw new EntityNotFoundException("User not found id=" + id);
        }
        userRepository.deleteById(id);        // DELETE
    }
}
