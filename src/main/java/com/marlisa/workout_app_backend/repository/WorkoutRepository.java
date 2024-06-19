package com.marlisa.workout_app_backend.repository;

import com.marlisa.workout_app_backend.entity.Workout;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutRepository extends JpaRepository<Workout, Long> {
}

