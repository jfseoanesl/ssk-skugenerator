# 🧸 SKU Generator - Sistema de Generación de Códigos de Productos

<div align="center">

![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.0-green?style=flat-square&logo=spring-boot)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=flat-square&logo=mysql)
![License](https://img.shields.io/badge/License-MIT-yellow?style=flat-square)
![Version](https://img.shields.io/badge/Version-1.0.0-red?style=flat-square)

**Sistema completo para la generación automática de códigos SKU de 12 dígitos para productos de una tienda de ropa infantil**

[🚀 Inicio Rápido](#-inicio-rápido) • [📋 Funcionalidades](#-funcionalidades) • [🛠️ Instalación](#️-instalación) • [📖 Documentación](#-documentación) • [🤝 Contribuir](#-contribuir)

</div>

---

## 📋 Tabla de Contenidos

- [Descripción General](#-descripción-general)
- [Funcionalidades Principales](#-funcionalidades-principales)
- [Tecnologías Utilizadas](#-tecnologías-utilizadas)
- [Arquitectura](#-arquitectura)
- [Inicio Rápido](#-inicio-rápido)
- [Instalación Detallada](#️-instalación-detallada)
- [Configuración](#-configuración)
- [Uso](#-uso)
- [API Documentation](#-api-documentation)
- [Testing](#-testing)
- [Deployment](#-deployment)
- [Monitoreo](#-monitoreo)
- [Contribuir](#-contribuir)
- [Licencia](#-licencia)
- [Soporte](#-soporte)

---

## 🎯 Descripción General

El **SKU Generator** es una aplicación web empresarial desarrollada con Spring Boot que automatiza la generación de códigos SKU únicos de 12 dígitos para productos de una tienda de ropa infantil. El sistema implementa una estructura de codificación específica que incorpora información sobre tipo de producto, categoría, subcategoría, talla, color, temporada y un consecutivo único.

### 🎨 Estructura del Código SKU

```
T CC S TT CC S ###
│ │  │ │  │  │ └── Consecutivo (3 dígitos)
│ │  │ │  │  └── Temporada (1 dígito)
│ │  │ │  └── Color (2 dígitos)
│ │  │ └── Talla (2 dígitos)
│ │  └── Subcategoría (1 dígito)
│ └── Categoría (2 dígitos)
└── Tipo de Producto (1 dígito)
```

**Ejemplo:** `110110205001`
- `1` = Producto Simple
- `10` = Ropa Superior
- `1` = Camisetas básicas
- `02` = 4-6 años
- `05` = Rojo
- `1` = Primavera-Verano
- `001` = Primer producto con estas características

---

## ✨ Funcionalidades Principales

### 🏭 Generación de Productos
- ✅ **Generación automática de códigos SKU** únicos de 12 dígitos
- ✅ **Generación automática de nombres** descriptivos de productos
- ✅ **Validación de duplicados** con confirmación inteligente
- ✅ **Gestión completa CRUD** de productos
- ✅ **Búsqueda avanzada** con múltiples filtros

### ⚙️ Gestión de Configuraciones
- ✅ **Administración de catálogos**: tipos, categorías, subcategorías, tallas, colores, temporadas
- ✅ **Validaciones de integridad** referencial
- ✅ **Relaciones jerárquicas** entre categorías y subcategorías
- ✅ **Soft delete** para mantener historial

### 📊 Reportes y Estadísticas
- ✅ **Dashboard interactivo** con métricas en tiempo real
- ✅ **Reportes detallados** por categorías, colores, tallas y temporadas
- ✅ **Gráficos dinámicos** con drill-down
- ✅ **Análisis de tendencias** temporales

### 🔐 Seguridad y Control de Acceso
- ✅ **Autenticación JWT** segura
- ✅ **Gestión de usuarios** con múltiples roles
- ✅ **Control de acceso granular** (ADMIN, USER, VIEWER)
- ✅ **Auditoría completa** de acciones

### 📤 Importación y Exportación
- ✅ **Exportación multi-formato**: CSV, Excel, JSON
- ✅ **Importación masiva** con validaciones
- ✅ **Sistema de backup** automático
- ✅ **Consola SQL** para administradores

### 🎨 Interfaz de Usuario
- ✅ **Diseño responsive** para todos los dispositivos
- ✅ **Interfaz moderna** y profesional
- ✅ **Experiencia de usuario optimizada**
- ✅ **Accesibilidad web** (WCAG 2.1)

---

## 🛠️ Tecnologías Utilizadas

### Backend
- **Java 17** - Lenguaje de programación principal
- **Spring Boot 3.5.0** - Framework de aplicación
- **Spring Security 6.2** - Seguridad y autenticación
- **Spring Data JPA** - Persistencia de datos
- **Hibernate 6.2** - ORM
- **MySQL 8.0** - Base de datos principal
- **Flyway** - Migraciones de base de datos
- **JWT** - Autenticación basada en tokens

### Frontend
- **Thymeleaf** - Motor de plantillas
- **Bootstrap 5** - Framework CSS
- **JavaScript ES6+** - Interactividad
- **Chart.js** - Gráficos y visualizaciones

### Testing
- **JUnit 5** - Framework de testing
- **Mockito** - Mocking para tests
- **TestContainers** - Tests de integración
- **H2 Database** - Base de datos en memoria para tests

### DevOps y Herramientas
- **Maven** - Gestión de dependencias
- **Docker** - Containerización
- **Docker Compose** - Orquestación local
- **Actuator** - Monitoreo y métricas
- **Swagger/OpenAPI** - Documentación de API

### Calidad de Código
- **JaCoCo** - Cobertura de código
- **SpotBugs** - Análisis estático de código
- **SonarQube** - Calidad de código (opcional)

---

## 🏗️ Arquitectura

El sistema implementa una **arquitectura por capas** que garantiza separación de responsabilidades, mantenibilidad y escalabilidad:

```
┌─────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                       │
│  Controllers (Web & API) • Templates • Static Resources     │
├─────────────────────────────────────────────────────────────┤
│                     SERVICE LAYER                          │
│     Business Logic • Validation • Transaction Management    │
├─────────────────────────────────────────────────────────────┤
│                   REPOSITORY LAYER                         │
│        Data Access • JPA Repositories • Query Methods      │
├─────────────────────────────────────────────────────────────┤
│                    PERSISTENCE LAYER                       │
│              MySQL Database • Entity Mapping               │
└─────────────────────────────────────────────────────────────┘
```

### Patrones Implementados
- **Repository Pattern** - Abstracción de acceso a datos
- **DTO Pattern** - Transferencia de datos entre capas
- **Builder Pattern** - Construcción de objetos complejos
- **Strategy Pattern** - Diferentes tipos de exportación
- **Factory Pattern** - Generación de códigos SKU

---

## 🚀 Inicio Rápido

### Prerrequisitos
- ☕ **Java 17+**
- 🐬 **MySQL 8.0+**
- 🔧 **Maven 3.8+**
- 🐳 **Docker** (opcional)

### 1️⃣ Clonación del Repositorio
```bash
git clone https://github.com/empresa/sku-generator.git
cd sku-generator
```

### 2️⃣ Configuración de Base de Datos
```sql
-- Crear base de datos
CREATE DATABASE sku_generator_dev CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Crear usuario
CREATE USER 'sku_dev_user'@'localhost' IDENTIFIED BY 'sku_dev_password';
GRANT ALL PRIVILEGES ON sku_generator_dev.* TO 'sku_dev_user'@'localhost';
FLUSH PRIVILEGES;
```

### 3️⃣ Ejecución con Maven
```bash
# Compilar proyecto
mvn clean compile

# Ejecutar tests
mvn test

# Ejecutar aplicación
mvn spring-boot:run
```

### 4️⃣ Ejecución con Docker Compose
```bash
# Iniciar todos los servicios
docker-compose up -d

# Ver logs
docker-compose logs -f app
```

### 5️⃣ Acceso a la Aplicación
- 🌐 **Aplicación**: http://localhost:8080
- 📚 **API Docs**: http://localhost:8080/swagger-ui.html
- ❤️ **Health Check**: http://localhost:8080/actuator/health
- 📊 **Métricas**: http://localhost:8080/actuator/metrics

### 6️⃣ Credenciales por Defecto
```
👤 Administrador:
   Usuario: admin
   Contraseña: admin123

👤 Usuario:
   Usuario: user
   Contraseña: user123
```

---

##