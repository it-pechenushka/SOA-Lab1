package app.repository;

import app.model.StudyGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<StudyGroup, Long> {

    @Override
    Optional<StudyGroup> findById(Long aLong);

    @Override
    <S extends StudyGroup> S save(S entity);

    @Override
    void delete(StudyGroup entity);
}
