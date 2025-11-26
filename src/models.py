"""Core data models for the therapy platform.

The module contains plain dataclasses to model patients and their related
clinical records. It is framework-agnostic and can be mapped to an ORM or
serialized for persistence.
"""
from __future__ import annotations

from dataclasses import dataclass, field
from datetime import date
from enum import Enum
from typing import List, Optional


class Relationship(Enum):
    """Relationship options between a caregiver and a patient."""

    MOTHER = "mother"
    FATHER = "father"
    GRANDPARENT = "grandparent"
    LEGAL_GUARDIAN = "legal_guardian"
    OTHER = "other"


@dataclass
class Caregiver:
    id: str
    full_name: str
    relationship: Relationship
    contact_phone: Optional[str] = None
    email: Optional[str] = None


@dataclass
class PrenatalHistory:
    id: str
    pregnancy_details: str
    complications: Optional[str] = None
    birth_type: Optional[str] = None  # e.g., cesarean, natural
    gestational_age_weeks: Optional[int] = None
    birth_weight_grams: Optional[int] = None


@dataclass
class MedicalHistory:
    id: str
    chronic_conditions: List[str] = field(default_factory=list)
    allergies: List[str] = field(default_factory=list)
    medications: List[str] = field(default_factory=list)
    surgeries: List[str] = field(default_factory=list)
    hospitalizations: List[str] = field(default_factory=list)


@dataclass
class EducationalBackground:
    id: str
    current_grade: Optional[str] = None
    school_name: Optional[str] = None
    support_services: List[str] = field(default_factory=list)
    learning_difficulties: List[str] = field(default_factory=list)


@dataclass
class DevelopmentHistory:
    id: str
    motor_milestones: str
    language_milestones: str
    social_milestones: str
    comments: Optional[str] = None


@dataclass
class ConcurrentTherapy:
    id: str
    therapy_type: str
    provider: str
    frequency_per_week: int
    start_date: Optional[date] = None
    notes: Optional[str] = None


@dataclass
class Patient:
    id: str
    full_name: str
    birth_date: date
    caregivers: List[Caregiver] = field(default_factory=list)
    prenatal_history: Optional[PrenatalHistory] = None
    medical_history: Optional[MedicalHistory] = None
    educational_background: Optional[EducationalBackground] = None
    development_history: Optional[DevelopmentHistory] = None
    concurrent_therapies: List[ConcurrentTherapy] = field(default_factory=list)
    created_at: Optional[date] = None
    updated_at: Optional[date] = None

