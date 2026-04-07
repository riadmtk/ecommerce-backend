# 🛒 E-Commerce Backend — Microservices

Backend de l'application E-Commerce Modulaire basé sur une architecture
**Microservices**, paradigme **Headless & Composable Commerce**,
avec Architecture Hexagonale (Ports & Adapters) par service.

> ⚠️ Ce projet est en cours de développement.
> Les microservices seront ajoutés progressivement au fil des sprints.

---

## 📋 Description

Ce dépôt contiendra le backend de la plateforme e-commerce, composé de
microservices indépendants exposant des API REST consommées par les
applications Angular (Front Office & Back Office).

## 🏗 Architecture Cible

- **API Gateway** — Spring Cloud Gateway
- **Event Bus** — Apache Kafka
- **Auth** — JWT / Spring Security
- **BDD** — PostgreSQL (schéma isolé par service)
- **Recherche** — ElasticSearch
- **Conteneurisation** — Docker + Kubernetes

## 📦 Microservices Prévus

| **Service**          | **Port prévu** | **Statut** | **Description**             |
| -------------------- | -------------- | ---------- | ----------------------------|
| API Gateway          | 8080           | 🔜 À venir | Point d'entrée unique       |
| User Service         | 8081           | 🔜 À venir | Auth, profil, comptes       |
| Product Service      | 8082           | 🔜 À venir | Catalogue, stock            |
| Search Service       | 8083           | 🔜 À venir | Recherche full-text         |
| Cart Service         | 8084           | 🔜 À venir | Panier, checkout            |
| Order Service        | 8085           | 🔜 À venir | Commandes                   |
| Payment Service      | 8086           | 🔜 À venir | Paiement Stripe/PayPal      |
| Notification Service | 8087           | 🔜 À venir | SMS/Email                   |
| Wishlist Service     | 8088           | 🔜 À venir | La liste des produits aimés |

## ⚙️ Prérequis

- Java 17+
- Maven 3.8+
- Docker & Docker Compose
- PostgreSQL 15+
- Apache Kafka 3+
- ElasticSearch 8+

## 🚀 Getting Started

### 1. Cloner le dépôt
```bash
git clone https://github.com/riadmtk/ecommerce-backend.git
cd ecommerce-backend
```

### 2. Configurer les variables d'environnement
```bash
cp .env.example .env
# Ouvrir .env et remplir toutes les valeurs CHANGE_ME
```

> ⚠️ Ne jamais committer le fichier `.env`.
> Seul `.env.example` est versionné dans ce dépôt.


## 🐳 Lancement avec Docker

### Prérequis
- Docker Desktop installé et démarré
- Fichier `.env` configuré à partir de `.env.example`

### Lancer uniquement l'infrastructure (BDD + Kafka + ElasticSearch)
```bash
docker-compose up -d \
  zookeeper kafka elasticsearch \
  postgres-users postgres-products postgres-cart \
  postgres-orders postgres-payments \
  postgres-notifications postgres-wishlist
```

### Vérifier que l'infrastructure est prête
```bash
docker-compose ps
```
Tous les services doivent afficher `healthy`.

### Lancer tous les services
```bash
docker-compose up -d
```

### Lancer un seul service (exemple : payment-service)
```bash
docker-compose up -d payment-service
```

### Voir les logs d'un service
```bash
docker-compose logs -f payment-service
```

### Arrêter tout
```bash
docker-compose down
```

### Arrêter et supprimer les volumes (reset complet)
```bash
docker-compose down -v
```

### Ports exposés
| Service              | URL / Port              |
| -------------------- | ----------------------- |
| API Gateway          | [http://localhost:8080] |
| User Service         | [http://localhost:8081] |
| Product Service      | [http://localhost:8082] |
| Search Service       | [http://localhost:8083] |
| Cart Service         | [http://localhost:8084] |
| Order Service        | [http://localhost:8085] |
| Payment Service      | [http://localhost:8086] |
| Notification Service | [http://localhost:8087] |
| Wishlist Service     | [http://localhost:8088] |

| Service       | URL / Port                                     |
| ------------- | ---------------------------------------------- |
| Kafka         | localhost:9092                                 |
| Elasticsearch | http://localhost:9200                          |         




## 🌿 Convention de Nommage des Branches

| **Type**                | **Format**                   | **Exemple**                      |
| ----------------------- | ---------------------------- | -------------------------------- |
| Nouvelle fonctionnalité | `feature/ESP-XX-description` | `feature/ESP-42-payment-service` |
| Correction de bug       | `fix/ESP-XX-description`     | `fix/ESP-15-jwt-token-expiry`    |
| Correction urgente      | `hotfix/ESP-XX-description`  | `hotfix/ESP-99-stripe-webhook`   |


## 📝 Convention des Commits
```
ESP-XX: description courte de ce qui a été fait
```

Exemples :
- `ESP-42: init payment service structure`
- `ESP-42: add StripeAdapter implementation`
- `ESP-42: add webhook signature verification`

## 🔗 Liens

- 📋 **Jira** : [Lien vers le projet Jira]
- 📖 **Confluence** : [Lien vers la documentation Confluence]
- 🎨 **Front Office** : [Lien vers le repo frontend]


## 📅 Avancement

Le développement des microservices suivra les tickets Jira du projet.
Chaque service sera documenté ici au fur et à mesure de son implémentation.