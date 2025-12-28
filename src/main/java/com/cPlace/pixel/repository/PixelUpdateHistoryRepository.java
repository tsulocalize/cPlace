package com.cPlace.pixel.repository;

import com.cPlace.pixel.domain.PixelUpdateHistory;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PixelUpdateHistoryRepository extends JpaRepository<PixelUpdateHistory, Long> {

    List<PixelUpdateHistory> findByOrderByCreatedAtDesc(Limit limit);
}
