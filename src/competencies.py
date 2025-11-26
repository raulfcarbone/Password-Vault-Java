"""Competency structures per patient.

The module outlines a reusable template to capture competency status across
social, communication, play, literacy, and language domains.
"""
from __future__ import annotations

from dataclasses import dataclass, field
from enum import Enum
from typing import Dict, List, Optional


class CompetencyArea(Enum):
    SOCIAL_SKILLS = "habilidades_sociales"
    SOCIAL_COMMUNICATION = "comunicacion_social"
    PLAY_SKILL = "juego_habilidad"
    PLAY_SOCIAL = "juego_habilidades_sociales"
    LITERACY = "lectura_escritura"
    SPEECH_PRODUCTION = "habla"
    LANGUAGE_COMPREHENSION = "lenguaje_comprensivo"
    LANGUAGE_EXPRESSION = "lenguaje_expresivo"


class LinguisticSubdomain(Enum):
    MORPHOSYNTAX = "morfosintaxis"
    LEXICO_SEMANTIC = "lexico_semantica"
    PRAGMATICS = "pragmatica"
    PHONOLOGY = "fonologia"


@dataclass
class SubdomainCompetency:
    subdomain: LinguisticSubdomain
    description: str
    level: Optional[str] = None  # e.g., emerging, consolidating, mastered
    notes: Optional[str] = None


@dataclass
class Competency:
    area: CompetencyArea
    description: str
    level: Optional[str] = None
    subdomains: List[SubdomainCompetency] = field(default_factory=list)
    evidence: Optional[str] = None


@dataclass
class PatientCompetencyProfile:
    patient_id: str
    competencies: Dict[CompetencyArea, List[Competency]] = field(
        default_factory=dict
    )

    def add_competency(self, competency: Competency) -> None:
        """Attach a competency instance to the patient profile."""
        self.competencies.setdefault(competency.area, []).append(competency)


