# Plataforma terapéutica: diseño funcional

Este documento resume la estructura propuesta para cubrir la ficha clínica del paciente, la plantilla de competencias, el seguimiento de objetivos con GAS y la navegación de la UI.

## 1. Modelos y relaciones
- **Paciente** (id, nombre, fecha de nacimiento, timestamps) con asociaciones 1:N hacia **Cuidadores**, **Terapias concurrentes** y 1:1 hacia **Antecedentes prenatales**, **Antecedentes médicos**, **Antecedentes educativos**, **Desarrollo**.
- **Cuidadores** incluyen nombre, parentesco, teléfono y correo para notificaciones.
- **Antecedentes** se dividen en:
  - Prenatales: tipo de parto, edad gestacional, peso al nacer, complicaciones.
  - Médicos: condiciones crónicas, alergias, cirugías, hospitalizaciones y medicación activa.
  - Educativos: grado actual, centro, apoyos y dificultades de aprendizaje.
  - Desarrollo: hitos motores, de lenguaje y sociales con observaciones.
- **Competencias**: plantilla por paciente con áreas de habilidades sociales, comunicación social, juego (habilidad y habilidades sociales), lectura/escritura, habla y lenguaje comprensivo/expresivo. Las áreas de lenguaje se desglosan en morfosintaxis, léxico-semántica, pragmática y fonología.
- **Objetivos (GAS)**: objetivos por área con estado (planificado, en progreso, en pausa, completado) y calificaciones semanales usando la escala GAS (-2 a +2) más observaciones.

## 2. Flujo de UI y navegación
- **Ficha del paciente**: muestra datos demográficos, antecedentes, cuidadores y terapias concurrentes. Acciones para editar y exportar (PDF/CSV).
- **Plantilla de competencias**: tablero por paciente con filtros por área; permite registrar nivel, evidencia y notas en subdominios lingüísticos.
- **Tablero de objetivos y seguimiento**: lista objetivos GAS por paciente con filtrado por área y estado; incluye formulario para añadir calificación semanal y observaciones.
- **Barra lateral**: selector de paciente y accesos rápidos a ficha, competencias y objetivos.

## 3. Seguridad y privacidad
- Autenticación obligatoria (OAuth2/JWT) con sesiones expiranbles.
- Autorización por paciente (scopes y listas de acceso por rol); trazabilidad con auditoría de lectura/escritura en registros clínicos.
- Cifrado en tránsito (HTTPS/TLS) y cifrado en reposo para copias de seguridad.
- Respaldos automáticos versionados y pruebas de restauración; políticas de retención.

## 4. Exportes y reportes
- Exportación PDF/CSV de ficha del paciente (datos demográficos, antecedentes y terapias).
- Exportación de competencias con niveles y evidencias por área/subdominio.
- Exportación del progreso GAS: tabla semanal con estado y escala (-2 a +2) más observaciones.
- Marca de tiempo y responsable incluido en cada export para trazabilidad.

## 5. Trazabilidad de cambios
- Bitácora de eventos: quién creó/actualizó cada antecedente, competencia o calificación GAS.
- Versionado lógico de objetivos y competencias para preservar histórico.
