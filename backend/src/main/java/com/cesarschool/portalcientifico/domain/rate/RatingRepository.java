package com.cesarschool.portalcientifico.domain.rate;

import com.cesarschool.portalcientifico.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    boolean existsByMaterialIdAndUser(Long materialId, User user);

}