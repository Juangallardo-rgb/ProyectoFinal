# Sistema de Inventario — Pipeline DevSecOps CI/CD

## Descripción

Este proyecto corresponde a una aplicación básica de inventario desarrollada en Java. La aplicación fue adaptada como una API HTTP mínima para implementar y demostrar un flujo automatizado de integración continua, despliegue continuo y seguridad DevSecOps.

El pipeline se ejecuta automáticamente mediante GitHub Actions cada vez que se realiza un cambio en la rama `main`.

## Objetivo

Implementar un pipeline CI/CD que incluya:

- Compilación del proyecto.
- Ejecución de pruebas unitarias.
- Generación de un artefacto JAR.
- Análisis estático de seguridad SAST.
- Construcción de una imagen Docker.
- Verificación de integridad del artefacto.
- Despliegue automático en Kubernetes.
- Análisis dinámico de seguridad DAST.
- Generación de reportes y evidencias descargables.

## Tecnologías utilizadas

| Tecnología | Propósito |
|---|---|
| Java 17 | Desarrollo de la aplicación |
| Maven | Compilación, pruebas y generación del JAR |
| JUnit 5 | Pruebas unitarias |
| GitHub Actions | Automatización CI/CD |
| SonarQube Cloud | Análisis estático de seguridad SAST |
| Docker | Empaquetado de la aplicación |
| SHA-256 | Validación de integridad del artefacto |
| kind | Creación de un clúster Kubernetes temporal |
| Kubernetes | Despliegue y ejecución de la aplicación |
| OWASP ZAP | Análisis dinámico de seguridad DAST |
| GitHub Artifacts | Almacenamiento de resultados y evidencias |

## Endpoints de la aplicación

La aplicación se ejecuta en el puerto `8080`.

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/` | Información general de la aplicación |
| GET | `/health` | Estado de disponibilidad |
| GET | `/products` | Lista básica de productos |

Ejemplo de respuesta del endpoint `/health`:

```json
{
  "status": "UP"
}
```

Ejemplo de respuesta del endpoint `/products`:

```json
[
  {
    "name": "Laptop",
    "quantity": 5,
    "price": 1000.0
  }
]
```

## Arquitectura del pipeline

```text
Cambio enviado a la rama main
              |
              v
      GitHub Actions
              |
              v
   Compilación con Maven
              |
              v
    Pruebas con JUnit
              |
              v
  Generación del archivo JAR
              |
              v
 Análisis SAST con SonarQube
              |
              v
 Verificación SHA-256 del JAR
              |
              v
 Construcción de imagen Docker
              |
              v
 Creación de clúster con kind
              |
              v
 Despliegue en Kubernetes
              |
              v
 Validación de endpoints HTTP
              |
              v
 Análisis DAST con OWASP ZAP
              |
              v
 Publicación de evidencias
```

## Etapas del pipeline

### 1. Compilación y pruebas

Maven limpia el proyecto, compila el código, ejecuta las pruebas unitarias y genera el artefacto ejecutable:

```bash
mvn clean package
```

El artefacto generado es:

```text
target/inventory-app.jar
```

### 2. Análisis SAST

SonarQube Cloud realiza un análisis estático del código fuente para identificar vulnerabilidades, errores, problemas de mantenibilidad y posibles riesgos de seguridad.

El resultado de la ejecución queda almacenado en:

```text
sonarqube-output.txt
sonarqube-status.txt
```

### 3. Integridad del artefacto

El pipeline genera una suma criptográfica SHA-256 del archivo JAR:

```bash
sha256sum target/inventory-app.jar
```

Posteriormente, verifica la huella generada para comprobar que el artefacto no haya sido modificado.

Evidencias:

```text
artifact-sha256.txt
integrity-verification.txt
```

### 4. Construcción de la imagen Docker

El archivo JAR se empaqueta en una imagen Docker denominada:

```text
inventory-app:latest
```

La imagen contiene Java 17 y expone el puerto `8080`.

### 5. Despliegue en Kubernetes

GitHub Actions crea un clúster Kubernetes temporal utilizando kind.

Dentro del clúster se crean:

- Un Deployment llamado `inventory-deployment`.
- Un Pod con la aplicación.
- Un Service llamado `inventory-service`.
- Pruebas de disponibilidad y estado mediante `/health`.

El pipeline espera que el Deployment finalice correctamente antes de continuar.

### 6. Análisis DAST

OWASP ZAP Baseline analiza dinámicamente la aplicación después de que ha sido desplegada y está respondiendo mediante HTTP.

Los resultados se generan en los siguientes formatos:

```text
zap-report.html
zap-report.json
zap-report.md
zap-console-output.txt
```

### 7. Publicación de artefactos

Al finalizar la ejecución, GitHub Actions publica dos artefactos descargables:

```text
aplicacion-inventario-jar
evidencias-devsecops
```

El primero contiene la aplicación compilada y el segundo contiene los reportes del pipeline, Kubernetes, SonarQube, OWASP ZAP e integridad SHA-256.

## Evidencias generadas

Entre las principales evidencias se encuentran:

```text
build-status.txt
inventory-app.jar
surefire-reports/
artifact-sha256.txt
integrity-verification.txt
sonarqube-output.txt
docker-build.txt
docker-image-inspect.json
kind-cluster-creation.txt
kubernetes-cluster-info.txt
kubernetes-pods.txt
kubernetes-resources.txt
kubernetes-rollout.txt
health-response.json
products-response.json
zap-report.html
zap-report.json
zap-report.md
```

## Estructura principal

```text
ProyectoFinal/
├── .github/
│   └── workflows/
│       └── ci.yml
├── k8s/
│   └── inventory.yaml
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/inventory/
│   │           └── Main.java
│   └── test/
├── Dockerfile
├── pom.xml
└── README.md
```

## Ejecución automática

El pipeline se activa mediante:

```yaml
on:
  push:
    branches:
      - main
  workflow_dispatch:
```

Esto permite ejecutarlo automáticamente al enviar cambios a `main` o manualmente desde la pestaña Actions.

## Resultado

Se implementó un flujo DevSecOps automatizado capaz de compilar, probar, analizar, empaquetar y desplegar la aplicación en Kubernetes.

Además, el pipeline incorpora:

- SAST con SonarQube Cloud.
- DAST con OWASP ZAP.
- Validación de integridad con SHA-256.
- Evidencias descargables.
- Verificación del estado de la aplicación desplegada.

## Consideración sobre el despliegue

El clúster Kubernetes utilizado es temporal y se crea dentro del entorno de GitHub Actions mediante kind. El clúster existe durante la ejecución del pipeline y permite demostrar automáticamente el proceso de despliegue, validación y análisis DAST sin utilizar infraestructura de pago.
