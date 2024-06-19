package com.marlisa.workout_app_backend.repository;

import com.marlisa.workout_app_backend.entity.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
}
