# Plataforma terapéutica

Propuesta inicial para la plataforma de terapia que cubre la ficha clínica del paciente, la plantilla de competencias y el seguimiento de objetivos con metodología GAS.

## Contenido
- `src/models.py`: dataclasses para pacientes, cuidadores, antecedentes, desarrollo y terapias concurrentes.
- `src/competencies.py`: plantilla de competencias por paciente con áreas y subdominios lingüísticos.
- `src/gas.py`: objetivos por área con escala GAS semanal, estado y observaciones.
- `docs/architecture.md`: resumen funcional de vistas, seguridad, trazabilidad y exportes.

## Próximos pasos
- Mapear los modelos a una base de datos (ORM) y exponerlos vía API segura.
- Construir UI con vistas independientes para ficha, competencias y tablero GAS con filtrado por paciente y área.
- Implementar exportes PDF/CSV y auditoría de cambios.
