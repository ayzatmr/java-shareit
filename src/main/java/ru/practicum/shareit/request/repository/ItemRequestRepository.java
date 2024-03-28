package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    @Query(value = "SELECT ir FROM ItemRequest ir " +
            "LEFT JOIN FETCH ir.items i " +
            "JOIN ir.user r " +
            "WHERE r.id != ?1",
            countQuery = "SELECT COUNT(ir) FROM ItemRequest ir " +
                    "LEFT JOIN ir.items i " +
                    "JOIN ir.user r " +
                    "WHERE r.id != ?1 " +
                    "GROUP BY ir.created")
    Page<ItemRequest> findAvailableRequests(long userId, Pageable pageable);

    @Query("SELECT ir FROM ItemRequest ir " +
            "LEFT JOIN FETCH ir.items i " +
            "JOIN ir.user r " +
            "WHERE r.id = ?1")
    List<ItemRequest> getUserItemRequests(Long userId, Sort sort);
}