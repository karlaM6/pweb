package edu.tuuni.regata.repo;


import edu.tuuni.regata.domain.Boat;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BoatRepository extends JpaRepository<Boat, Long> {
    long countByModel_Id(Long modelId);
    long countByOwner_Id(Long ownerId);
}