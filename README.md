# 🏨 Projet de Gestion de Réservations Hôtelières

Ce projet est une application complète pour la gestion des réservations hôtelières, comprenant une **backend Spring Boot** et quatre **frontends** :
1. **Java + Retrofit**
2. **Java + Volley**
3. **Kotlin + Retrofit**
4. **Kotlin + Volley**

L'application permet de gérer les chambres, les clients, les réservations et inclut un système d'authentification sécurisé.

---

## 📂 Structure du Projet

### Backend (Spring Boot)
Le backend est développé avec Spring Boot et expose une API REST pour les fonctionnalités suivantes :
- Authentification (login/signup).
- Gestion des chambres (ajout, modification, suppression, consultation).
- Gestion des réservations (création, modification, suppression, consultation).

Structure des dossiers :
src/
├── config/ # Configuration de l'application
├── controllers/ # Contrôleurs pour gérer les requêtes HTTP
├── models/ # Modèles de données (entités)
├── repositories/ # Interfaces pour accéder aux données
├── services/ # Logique métier
└── ReservationApplication.java # Point d'entrée de l'application


### Frontend Java + Retrofit
Ce frontend utilise Retrofit pour communiquer avec l'API backend. Il inclut des écrans pour :
- Authentification (login/signup).
- Gestion des chambres (liste, détails, ajout).
- Gestion des réservations (liste, détails, ajout).

Structure des dossiers :
src/
├── adapters/ # Adaptateurs pour les RecyclerView
├── api/ # Configuration de Retrofit et interfaces API
├── models/ # Modèles de données
├── ui/ # Activités et fragments
│ ├── auth/ # Écrans d'authentification
│ ├── chambres/ # Écrans de gestion des chambres
│ ├── reservations/ # Écrans de gestion des réservations
│ └── MainActivity.java


### Frontend Java + Volley
Ce frontend utilise Volley pour communiquer avec l'API backend. Il offre les mêmes fonctionnalités que le frontend Retrofit.

Structure des dossiers :
src/
├── adapters/ # Adaptateurs pour les RecyclerView
├── models/ # Modèles de données
├── network/ # Configuration de Volley
├── ui/ # Activités et fragments
│ ├── auth/ # Écrans d'authentification
│ ├── chambres/ # Écrans de gestion des chambres
│ ├── reservations/ # Écrans de gestion des réservations
│ └── MainActivity.java
├── utils/ # Utilitaires (ex : gestion des préférences)


### Frontend Kotlin + Retrofit
Ce frontend est similaire au frontend Java + Retrofit, mais il est développé en Kotlin.

### Frontend Kotlin + Volley
Ce frontend est similaire au frontend Java + Volley, mais il est développé en Kotlin.

---

## 🛠️ Prérequis

- **Java JDK 11+**
- **Android Studio** (pour les frontends)
- **Maven** (pour le backend Spring Boot)
- **Retrofit** et **Volley** (pour les frontends)
- **Base de données** (ex : MySQL, PostgreSQL, H2)

---

## 🚀 Installation et Exécution

### Backend (Spring Boot)
1. Accédez au dossier `backend/spring-backend`.
2. Configurez la base de données dans le fichier `application.properties`.
3. Exécutez la commande suivante pour démarrer le serveur :
   ```bash
   mvn spring-boot:run
   
### Frontend Kotlin + Retrofit
Ce frontend est similaire au frontend Java + Retrofit, mais il est développé en Kotlin.

### Frontend Kotlin + Volley
Ce frontend est similaire au frontend Java + Volley, mais il est développé en Kotlin.

---

## 🛠️ Prérequis

- **Java JDK 11+**
- **Android Studio** (pour les frontends)
- **Maven** (pour le backend Spring Boot)
- **Retrofit** et **Volley** (pour les frontends)
- **Base de données** (ex : MySQL, PostgreSQL, H2)

---

## 🚀 Installation et Exécution

### Backend (Spring Boot)
1. Accédez au dossier `backend/spring-backend`.
2. Configurez la base de données dans le fichier `application.properties`.
3. Exécutez la commande suivante pour démarrer le serveur :
   ```bash
   mvn spring-boot:run
   
### Frontend Kotlin + Retrofit
Ce frontend est similaire au frontend Java + Retrofit, mais il est développé en Kotlin.

### Frontend Kotlin + Volley
Ce frontend est similaire au frontend Java + Volley, mais il est développé en Kotlin.

---

## 🛠️ Prérequis

- **Java JDK 11+**
- **Android Studio** (pour les frontends)
- **Maven** (pour le backend Spring Boot)
- **Retrofit** et **Volley** (pour les frontends)
- **Base de données** (ex : MySQL, PostgreSQL, H2)

---

## 🚀 Installation et Exécution

### Backend (Spring Boot)
1. Accédez au dossier `backend/spring-backend`.
2. Configurez la base de données dans le fichier `application.properties`.
3. Exécutez la commande suivante pour démarrer le serveur :
   ```bash
   mvn spring-boot:run
4. Le backend sera accessible à l'adresse http://localhost:8080.
## Frontend Java + Retrofit
1. Ouvrez le projet dans Android Studio.
2. Synchronisez le projet avec Gradle.
3. Configurez l'URL de l'API backend dans ApiClient.java.
4. Exécutez l'application sur un émulateur ou un appareil physique.
## Frontend Java + Volley
1. Ouvrez le projet dans Android Studio.
2. Synchronisez le projet avec Gradle.
3. Configurez l'URL de l'API backend dans VolleySingleton.java.
4. Exécutez l'application sur un émulateur ou un appareil physique.
## Frontend Kotlin + Retrofit
1. Ouvrez le projet dans Android Studio.
2. Synchronisez le projet avec Gradle.
3. Configurez l'URL de l'API backend dans le fichier correspondant.
4. Exécutez l'application sur un émulateur ou un appareil physique.

## Frontend Kotlin + Volley
1. Ouvrez le projet dans Android Studio.
2. Synchronisez le projet avec Gradle.
3. Configurez l'URL de l'API backend dans le fichier correspondant.
4. Exécutez l'application sur un émulateur ou un appareil physique.

## Documentation de l'API
### Authentification
 - POST /api/auth/login : Connexion utilisateur.
 - POST /api/auth/signup : Inscription utilisateur.
### Chambres
 - GET /api/chambres : Liste des chambres.
 - GET /api/chambres/{id} : Détails d'une chambre.
 - POST /api/chambres : Ajouter une chambre.
 - PUT /api/chambres/{id} : Modifier une chambre.
 - DELETE /api/chambres/{id} : Supprimer une chambre.

### Réservations
 - GET /api/reservations : Liste des réservations.
 - GET /api/reservations/{id} : Détails d'une réservation.
 - POST /api/reservations : Ajouter une réservation.
 - PUT /api/reservations/{id} : Modifier une réservation.
 - DELETE /api/reservations/{id} : Supprimer une réservation.
## Vidéo de Démonstration
 - https://github.com/user-attachments/assets/f506398c-b449-47c9-930c-b0a71ac3750a
## Auteurs
- Assia BOUJNAH


