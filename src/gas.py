"""Goal Attainment Scaling (GAS) structures and helpers."""
from __future__ import annotations

from dataclasses import dataclass, field
from datetime import date
from enum import Enum
from typing import List, Optional


class GASLevel(Enum):
    """GAS scale values."""

    MUCH_LESS_THAN_EXPECTED = -2
    LESS_THAN_EXPECTED = -1
    EXPECTED = 0
    MORE_THAN_EXPECTED = 1
    MUCH_MORE_THAN_EXPECTED = 2


class ObjectiveStatus(Enum):
    PLANNED = "planned"
    IN_PROGRESS = "in_progress"
    ON_HOLD = "on_hold"
    COMPLETED = "completed"


@dataclass
class WeeklyScore:
    week_start: date
    level: GASLevel
    observations: Optional[str] = None


@dataclass
class GoalObjective:
    id: str
    patient_id: str
    area: str  # e.g., social, communication, literacy
    description: str
    baseline: Optional[str] = None
    target_description: Optional[str] = None
    status: ObjectiveStatus = ObjectiveStatus.PLANNED
    weekly_scores: List[WeeklyScore] = field(default_factory=list)

    def add_score(self, score: WeeklyScore) -> None:
        self.weekly_scores.append(score)


@dataclass
class GoalBoard:
    patient_id: str
    objectives: List[GoalObjective] = field(default_factory=list)

    def add_objective(self, objective: GoalObjective) -> None:
        self.objectives.append(objective)

    def filter_by_area(self, area: str) -> List[GoalObjective]:
        return [obj for obj in self.objectives if obj.area == area]

