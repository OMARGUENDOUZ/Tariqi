# Current API Contract Snapshot

> **Date du snapshot :** Avril 2026  
> **Branche :** main  
> **Commit de référence :** 385d72b  
> **Stack :** Spring Boot · Spring Data JPA · JWT (JwtService)  
> **Base URL locale :** http://localhost:8080

Ce fichier documente l'état réel de l'API au moment du snapshot,
y compris ses incohérences. Il ne représente pas l'état cible.
Il sert de référence de non-régression pendant la refactorisation.

---

## Table des matières

1. [Auth](#1-auth)
2. [ExamStudent](#2-examstudent)
3. [ExamSlot](#3-examslot)
4. [Student](#4-student)
5. [Instructor](#5-instructor)
6. [Modèles de données](#6-modèles-de-données)
7. [Incohérences connues](#7-incohérences-connues)
8. [Checklist de non-régression](#8-checklist-de-non-régression)

---

## 1. Auth

**Controller :** `AuthController.java`  
**Base path :** `/auth`  
**Sécurité :** Endpoints publics (pas de filtre JWT)

---

### POST /auth/login

Authentifie un utilisateur par email et mot de passe.

**Request body :**
```json
{
  "email": "string",
  "password": "string"
}
```

**Réponses :**

| Status | Body | Condition |
|--------|------|-----------|
| `200 OK` | `AuthResponse` (token + objet `User` complet) | Credentials valides |
| `401 Unauthorized` | *(vide)* | Email absent ou mot de passe incorrect |

**Comportement actuel connu :**
- La comparaison du mot de passe se fait en clair via `.equals()`
- La réponse 200 renvoie l'objet `User` complet (y compris le champ `password`)
- Aucune annotation `@Valid` sur le `LoginRequest`

---

### POST /auth/logout

Simule un logout.

**Request body :** aucun

**Réponses :**

| Status | Body | Condition |
|--------|------|-----------|
| `200 OK` | *(vide)* | Toujours |

**Comportement actuel connu :**
- Ne fait rien : aucune invalidation du token JWT
- Retourne simplement `200 OK` sans logique

---

## 2. ExamStudent

**Controller :** `ExamController.java`  
**Base path :** `/ExamStudent`  
**Dépendances injectées :** `ExamRepository` (direct) + `ExamService`

> ⚠️ Le path utilise PascalCase et commence par `/ExamStudent`.
> Le controller injecte directement `ExamRepository` en plus du service.

---

### GET /ExamStudent/{id}

Récupère une inscription d'examen par son ID.

**Path param :** `id` (long)

**Réponses :**

| Status | Body | Condition |
|--------|------|-----------|
| `200 OK` | `ExamStudent` (entité JPA) | Trouvé |
| `404 Not Found` | *(vide)* | ID inexistant |

---

### GET /ExamStudent

Recherche des inscriptions avec filtres optionnels.

**Query params :**

| Param | Type | Obligatoire | Description |
|-------|------|-------------|-------------|
| `studentId` | Long | Non | Filtre par étudiant |
| `status` | ExamStatus | Non | Filtre par statut |
| `examSlotId` | Long | Non | Filtre par créneau |

**Logique de filtrage actuelle (dans le controller) :**
1. Si `examSlotId` présent → `findByExamSlotId`
2. Sinon si `studentId` et `status` tous les deux absents → `findAll`
3. Sinon si `studentId` présent et `status` absent → `findByStudentId`
4. Sinon si `studentId` absent et `status` présent → `findByStatus`
5. Sinon → `findByStudentIdAndStatus`

**Réponses :**

| Status | Body | Condition |
|--------|------|-----------|
| `200 OK` | `List<ExamStudent>` | Résultats trouvés |
| `200 OK` | `[]` | Aucun résultat |

**Comportement actuel connu :**
- Logique de filtrage métier dans le controller (violation SRP)
- Pas de pagination
- Retourne toujours `200`, même si liste vide

---

### POST /ExamStudent

Inscrit un étudiant à un créneau d'examen.

**Request body :** `ExamStudent` (entité JPA directement)
```json
{
  "studentId": 1,
  "examSlotId": 2,
  "category": "string (ExamCategory enum)"
}
```

**Réponses :**

| Status | Body | Condition |
|--------|------|-----------|
| `201 Created` | `ExamStudent` avec header `Location` | Inscription réussie |
| `400 Bad Request` | *(vide)* | `RuntimeException` levée par le service |

**Comportement actuel connu :**
- Reçoit l'entité JPA directement (pas de DTO)
- Le `catch (RuntimeException e)` renvoie un `400` sans message d'erreur
- Le commentaire dans le code dit "Simplify error handling" (dette technique explicite)

---

### PUT /ExamStudent/{id}

Met à jour le résultat, le statut ou la catégorie d'une inscription.

**Path param :** `id` (long)

**Request body :** `ExamStudent` (entité JPA partielle)
```json
{
  "result": "string (ExamResult enum, optionnel)",
  "status": "string (ExamStatus enum, optionnel)",
  "category": "string (ExamCategory enum, optionnel)"
}
```

**Réponses :**

| Status | Body | Condition |
|--------|------|-----------|
| `200 OK` | `ExamStudent` mis à jour | Trouvé et mis à jour |
| `404 Not Found` | *(vide)* | ID inexistant |

**Comportement actuel connu :**
- Fusionne les champs non-null du body sur l'entité existante
- Utilise `examRepository.save(existing)` directement depuis le controller

---

### DELETE /ExamStudent/{id}

Supprime une inscription.

**Path param :** `id` (long)

**Réponses :**

| Status | Body | Condition |
|--------|------|-----------|
| `204 No Content` | *(vide)* | Suppression réussie |
| `404 Not Found` | *(vide)* | ID inexistant |

---

## 3. ExamSlot

**Controller :** `ExamSlotController.java`  
**Base path :** `ExamSlot` *(sans slash initial)*  
**Dépendances injectées :** `ExamSlotRepository` (direct, pas de service)

> ⚠️ Pas de couche service. Toutes les opérations passent directement par le repository.
> ⚠️ Le `@RequestMapping` n'a pas de slash initial (`"ExamSlot"` au lieu de `"/ExamSlot"`).

---

### GET /ExamSlot/{id}

Récupère un créneau d'examen par son ID.

**Path param :** `id` (long)

**Réponses :**

| Status | Body | Condition |
|--------|------|-----------|
| `200 OK` | `ExamSlot` (entité JPA) | Trouvé |
| `404 Not Found` | *(vide)* | ID inexistant |

---

### GET /ExamSlot

Récupère tous les créneaux, avec filtre optionnel sur l'état actif.

**Query params :**

| Param | Type | Obligatoire | Description |
|-------|------|-------------|-------------|
| `active` | Boolean | Non | Si `true`, renvoie uniquement les créneaux actifs |

**Réponses :**

| Status | Body | Condition |
|--------|------|-----------|
| `200 OK` | `List<ExamSlot>` | Résultats trouvés |
| `200 OK` | `[]` | Aucun résultat |

---

### POST /ExamSlot

Crée un nouveau créneau d'examen.

**Request body :** `ExamSlot` (entité JPA directement)

**Réponses :**

| Status | Body | Condition |
|--------|------|-----------|
| `200 OK` | `ExamSlot` | Toujours (bug) |

**Comportement actuel connu :**
- La méthode déclare un type de retour `ExamSlot` (pas `ResponseEntity`)
- Elle appelle `ResponseEntity.created(location).body(examSlot).getBody()` pour extraire le body, ce qui fait que le code HTTP `201 Created` n'est **jamais** renvoyé au client
- L'URI construite dans `uriComponentsBuilder.path("/Exam/{id}")` est incorrecte (pointe vers `/Exam/` au lieu de `/ExamSlot/`)
- Aucune annotation `@Valid`

---

### PUT /ExamSlot/{id}

Met à jour un créneau d'examen.

**Path param :** `id` (long)

**Request body :** `ExamSlot` (entité JPA directement)

**Réponses :**

| Status | Body | Condition |
|--------|------|-----------|
| `200 OK` | `ExamSlot` mis à jour | Trouvé |
| `404 Not Found` | *(vide)* | ID inexistant |

**Comportement actuel connu :**
- Sauvegarde directement le `body` reçu du client sans forcer l'ID du path
- Un client malveillant peut donc passer un ID différent dans le body et créer un nouvel enregistrement

---

### DELETE /ExamSlot/{id}

Supprime un créneau.

**Path param :** `id` (long)

**Réponses :**

| Status | Body | Condition |
|--------|------|-----------|
| `204 No Content` | *(vide)* | Suppression réussie |
| `404 Not Found` | *(vide)* | ID inexistant |

---

## 4. Student

**Controller :** `StudentController.java`  
**Base path :** `/Student`  
**Dépendances injectées :** `StudentService` (via `@Autowired` sur champ)

> ⚠️ Injection de dépendance par champ (`@Autowired`) au lieu de constructeur.

---

### GET /Student/{id}

Récupère un étudiant par son ID.

**Path param :** `id` (long)

**Réponses :**

| Status | Body | Condition |
|--------|------|-----------|
| `200 OK` | `Student` (entité JPA) | Trouvé |
| `404 Not Found` | *(vide)* | ID inexistant |

---

### GET /Student

Recherche des étudiants avec filtres.

**Query params :** objet `StudentFilterDto` (filtres passés directement comme query params)

**Réponses :**

| Status | Body | Condition |
|--------|------|-----------|
| `200 OK` | `List<Student>` | Résultats trouvés |
| `204 No Content` | *(vide)* | Aucun résultat |

**Comportement actuel connu :**
- Retourne `204` quand la liste est vide (vs `200 []` dans `ExamController`) : incohérence inter-controllers

---

### POST /Student/{id}/photo

Upload d'une photo encodée en Base64 pour un étudiant.

**Path param :** `id` (long)

**Request body :**
```json
{
  "photo": "base64encodedstring"
}
```

**Réponses :**

| Status | Body | Condition |
|--------|------|-----------|
| `200 OK` | `Student` mis à jour | Succès |
| `404 Not Found` | *(vide)* | Student null retourné par le service |

**Comportement actuel connu :**
- Contrôle du retour `null != null` au lieu d'utiliser `Optional`

---

### POST /Student

Crée un nouvel étudiant.

**Request body :** `Student` (entité JPA directement)

**Réponses :**

| Status | Body | Condition |
|--------|------|-----------|
| `200 OK` | `Student` | Toujours (bug) |

**Comportement actuel connu :**
- Même bug que `ExamSlotController.save()` : retourne le body de `ResponseEntity.created(...)` via `.getBody()`, donc le `201 Created` n'est jamais renvoyé
- Aucun `@Valid`

---

### PUT /Student/{id}

Met à jour un étudiant.

**Path param :** `id` (long)

**Request body :** `Student` (entité JPA directement)

**Réponses :**

| Status | Body | Condition |
|--------|------|-----------|
| `200 OK` | `Student` mis à jour | Trouvé |
| `404 Not Found` | *(vide)* | Student null retourné par le service |

---

### DELETE /Student/{id}

Supprime un étudiant.

**Path param :** `id` (long)

**Réponses :**

| Status | Body | Condition |
|--------|------|-----------|
| `204 No Content` | *(vide)* | Suppression réussie |
| `404 Not Found` | *(vide)* | ID inexistant |

---

## 5. Instructor

**Controller :** `InstructorController.java`  
**Base path :** `/Instructor`  
**Dépendances injectées :** `InstructorRepository` (direct, pas de service)

> ⚠️ Pas de couche service. Toutes les opérations passent directement par le repository.

---

### GET /Instructor/{id}

Récupère un instructeur par son ID.

**Path param :** `id` (long)

**Réponses :**

| Status | Body | Condition |
|--------|------|-----------|
| `200 OK` | `Instructor` (entité JPA) | Trouvé |
| `404 Not Found` | *(vide)* | ID inexistant |

---

### GET /Instructor

Récupère tous les instructeurs.

**Réponses :**

| Status | Body | Condition |
|--------|------|-----------|
| `200 OK` | `Iterable<Instructor>` | Toujours |

**Comportement actuel connu :**
- Pas de pagination
- Type de retour de la méthode est `Iterable<Instructor>` (pas `ResponseEntity`)
- Appelle `ResponseEntity.ok(students).getBody()` : inutilement indirect

---

### POST /Instructor

Crée un instructeur.

**Request body :** `Instructor` (entité JPA directement)

**Réponses :**

| Status | Body | Condition |
|--------|------|-----------|
| `200 OK` | `Instructor` | Toujours (bug) |

**Comportement actuel connu :**
- Même pattern défaillant que `StudentController.save()` et `ExamSlotController.save()`
- Le `201 Created` n'est jamais renvoyé au client

---

### PUT /Instructor/{id}

Met à jour un instructeur.

**Path param :** `id` (long)

**Request body :** `Instructor` (entité JPA directement)

**Réponses :**

| Status | Body | Condition |
|--------|------|-----------|
| `200 OK` | `Instructor` mis à jour | Trouvé |
| `404 Not Found` | *(vide)* | ID inexistant |

**Comportement actuel connu :**
- Sauvegarde directement le `body` reçu du client sans forcer `body.setId(id)`
- Risque de créer un nouvel enregistrement si le body ne contient pas l'ID

---

### DELETE /Instructor/{id}

Supprime un instructeur.

**Path param :** `id` (long)

**Réponses :**

| Status | Body | Condition |
|--------|------|-----------|
| `204 No Content` | *(vide)* | Suppression réussie |
| `404 Not Found` | *(vide)* | ID inexistant |

---

## 6. Modèles de données

### LoginRequest

email : String
password : String

### AuthResponse
token : String (JWT)
user : User (objet complet, incluant password)

### ExamStudent
id : Long
studentId : Long
examSlotId : Long
category : ExamCategory (enum)
status : ExamStatus (enum)
result : ExamResult (enum)

### ExamSlot
id : Long
active : Boolean
(autres champs à confirmer selon l'entité complète)

### Student
id : Long
photo : String (Base64)
(autres champs à confirmer selon l'entité complète)


### Instructor
id : Long
firstName : String
lastName : String
(autres champs à confirmer selon l'entité complète)


### Enums identifiés
ExamCategory : (valeurs à confirmer)
ExamStatus : (valeurs à confirmer — ex: SCHEDULED, PASSED, FAILED)
ExamResult : (valeurs à confirmer)


---

## 7. Incohérences connues

| # | Localisation | Problème |
|---|-------------|---------|
| 1 | Tous les controllers | Routes en PascalCase (`/Student`, `/Instructor`, `/ExamStudent`, `ExamSlot`) |
| 2 | `ExamSlotController` | Pas de slash initial sur `@RequestMapping("ExamSlot")` |
| 3 | `AuthController` | Mot de passe comparé en clair |
| 4 | `AuthController` | `logout()` ne fait rien |
| 5 | `AuthController` | `AuthResponse` renvoie l'objet `User` complet avec le champ `password` |
| 6 | `StudentController` | Injection par champ `@Autowired` au lieu de constructeur |
| 7 | `ExamController` | Accès direct à `ExamRepository` depuis le controller |
| 8 | `ExamSlotController` | Pas de couche service, accès direct au repository |
| 9 | `InstructorController` | Pas de couche service, accès direct au repository |
| 10 | `ExamController` | Logique de filtrage (5 branches if/else) dans le controller |
| 11 | `StudentController.save()` | Retourne `200` au lieu de `201` |
| 12 | `InstructorController.save()` | Retourne `200` au lieu de `201` |
| 13 | `ExamSlotController.save()` | Retourne `200` au lieu de `201` |
| 14 | `ExamSlotController.save()` | URI construite sur `/Exam/{id}` au lieu de `/ExamSlot/{id}` |
| 15 | `ExamSlotController.update()` | `save(body)` sans forcer l'ID du path |
| 16 | `InstructorController.update()` | `save(body)` sans forcer l'ID du path |
| 17 | `StudentController.findAll()` | Retourne `204` pour liste vide |
| 18 | `ExamController.findAll()` | Retourne `200 []` pour liste vide |
| 19 | Tous les controllers | Aucun `@Valid` sur les `@RequestBody` |
| 20 | Tous les controllers | Entités JPA exposées directement (pas de DTOs) |
| 21 | Aucun | Pas de `@RestControllerAdvice` global |
| 22 | Aucun | Pas de versioning d'API (`/api/v1/`) |
| 23 | `package` | Nom de package `com.example.carly` non aligné sur le nom du projet |

---

## 8. Checklist de non-régression

À vérifier manuellement après chaque commit de refacto.

### Auth
- [ ] `POST /auth/login` avec credentials valides renvoie un token
- [ ] `POST /auth/login` avec credentials invalides renvoie `401`
- [ ] `POST /auth/logout` renvoie `200`

### Student
- [ ] `GET /Student/1` renvoie un étudiant existant
- [ ] `GET /Student/9999` renvoie `404`
- [ ] `GET /Student` sans filtres renvoie une liste ou une réponse vide
- [ ] `POST /Student` crée un étudiant
- [ ] `PUT /Student/{id}` met à jour un étudiant existant
- [ ] `DELETE /Student/{id}` supprime un étudiant existant

### Instructor
- [ ] `GET /Instructor` renvoie tous les instructeurs
- [ ] `GET /Instructor/{id}` renvoie un instructeur existant
- [ ] `POST /Instructor` crée un instructeur
- [ ] `PUT /Instructor/{id}` met à jour un instructeur existant
- [ ] `DELETE /Instructor/{id}` supprime un instructeur existant

### ExamStudent
- [ ] `GET /ExamStudent/{id}` renvoie une inscription existante
- [ ] `GET /ExamStudent?status=...` filtre par statut
- [ ] `GET /ExamStudent?studentId=...` filtre par étudiant
- [ ] `GET /ExamStudent?examSlotId=...` filtre par créneau
- [ ] `POST /ExamStudent` inscrit un étudiant
- [ ] `PUT /ExamStudent/{id}` met à jour le résultat
- [ ] `DELETE /ExamStudent/{id}` supprime une inscription

### ExamSlot
- [ ] `GET /ExamSlot` renvoie tous les créneaux
- [ ] `GET /ExamSlot?active=true` renvoie uniquement les créneaux actifs
- [ ] `GET /ExamSlot/{id}` renvoie un créneau existant
- [ ] `POST /ExamSlot` crée un créneau
- [ ] `PUT /ExamSlot/{id}` met à jour un créneau
- [ ] `DELETE /ExamSlot/{id}` supprime un créneau

## Roadmap post-refactor
- [ ] Introduire une entité Organization alignée sur FHIR Organization
- [ ] Lier User à Organization
- [ ] Lier Student, Instructor, ExamSlot, Invoice, Payment à Organization
- [ ] Filtrer toutes les requêtes métier par organizationId
- [ ] Ajouter organizationId dans le JWT
- [ ] Gérer les rôles au sein d'une organization