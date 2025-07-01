package io.joshuasalcedo.fx.domain.clipboard;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ClipboardRepository extends JpaRepository<ClipboardEntry, Long> {

  // Find methods
  List<ClipboardEntry> findByIsPinnedTrueOrderByTimestampDesc();

  List<ClipboardEntry> findByIsPinnedFalseOrderByTimestampDesc();

  List<ClipboardEntry> findByTimestampAfterOrderByTimestampDesc(LocalDateTime timestamp);

  Page<ClipboardEntry> findByContentContainingIgnoreCase(String searchTerm, Pageable pageable);

  Optional<ClipboardEntry> findFirstByContentAndTimestampAfter(
      String content, LocalDateTime timestamp);

  // Count methods
  long countByisPinnedTrue();

  // Delete methods
  @Modifying
  @Query("DELETE FROM ClipboardEntry e WHERE e.isPinned = false")
  int deleteByIsPinnedFalse();

  @Modifying
  int deleteByTimestampBefore(LocalDateTime timestamp);

  @Modifying
  @Query("DELETE FROM ClipboardEntry e WHERE e.timestamp < :timestamp AND e.isPinned = false")
  int deleteByTimestampBeforeAndIsPinnedFalse(@Param("timestamp") LocalDateTime timestamp);

  // Custom queries for statistics
  @Query("SELECT MIN(e.timestamp) FROM ClipboardEntry e")
  LocalDateTime findOldestTimestamp();

  @Query("SELECT MAX(e.timestamp) FROM ClipboardEntry e")
  LocalDateTime findNewestTimestamp();

  // Additional useful queries
  @Query("SELECT e FROM ClipboardEntry e WHERE e.isPinned = true ORDER BY e.timestamp DESC")
  List<ClipboardEntry> findAllPinnedOrderByTimestampDesc();

  @Query("SELECT e FROM ClipboardEntry e ORDER BY e.timestamp DESC")
  Page<ClipboardEntry> findAllOrderByTimestampDesc(Pageable pageable);

  // Check existence
  boolean existsByContentAndTimestampAfter(String content, LocalDateTime timestamp);
}
