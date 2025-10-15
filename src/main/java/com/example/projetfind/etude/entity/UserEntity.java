package com.example.projetfind.etude.entity;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
// pour indiqquer que le nom de ma table sera users
@Table(name = "users")
public class UserEntity {

   @Column(name = "password_hash", nullable = false, length = 60)
   // pour empecher JSON d'apparaitre
   @JsonIgnore
   // pour stoker uniquement le hash du mot de passe genre par ex si l'utilisateur tape rama comme 
   // mot de passe on stoke $2a$10$k9D... genre jamais le vrai mot de passe
    private String passwordHash;

    // colonne clé primaire
     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)

     private Long id;

     @Column(nullable = false)
     private String name;

     @Column(nullable = false, unique = true)
     private String email;

     @Column(nullable = false, unique = true)
     private String username;

     // pour generer automatiquement la date et l'heure 
     @CreationTimestamp
     // pas modifiable apres creation 
     @Column(updatable = false, nullable = false)
     private Instant createdAt;

   

     // constructeur vide obligatoire pour le jpa 
     public UserEntity(){}

     // Creaction d'un objet
     public UserEntity(String name, String email, String username){
        this.name = name; 
        this.email = email;
        this.username = username;
     }

     // getters = pour lire la valeur d'un champ / Setters = pour modifier la valeur d'un champ
     // mon champs id 
     public Long getId() {
        return id;
     }
     public void setId(Long id){
        this.id = id;
     }

     // champ name pour lire le nom de l'utilisateur
     public String getName(){
        return name;
     }
     // et pour modifier le nom de l'utilisateur
     public void setName(String name){
        this.name = name;
     }
     // email 
     public String getEmail(){
        return email;
     }
     // modification 
     public void setEmail(String email){
        this.email = email;
     }
     // username
     public String getUsername(){
        return username;
     }
     // modification
     public void setUsername(String username){
        this.username = username; 
     }

     // createdAt
     public Instant getCreatedAt() { 
        return createdAt; 
    }
     // modification 
     public void setCreatedAt(Instant createdAt){
        this.createdAt = createdAt;
     }
     // getters /setters pour le mot de passe
     public String getPasswordHash(){ return passwordHash; }
     public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash;}

 
     

}
