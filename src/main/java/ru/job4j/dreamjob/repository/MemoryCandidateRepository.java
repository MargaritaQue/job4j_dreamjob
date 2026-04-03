package ru.job4j.dreamjob.repository;

import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Candidate;

import java.time.LocalDateTime;
import java.util.*;

@Repository
public class MemoryCandidateRepository implements CandidateRepository{

    private static final MemoryCandidateRepository INSTANCE = new MemoryCandidateRepository();

    private int nextId = 1;

    private final Map<Integer, Candidate> candidates = new HashMap<>();

    private MemoryCandidateRepository() {
        save(new Candidate(0, "Костин Матвей Фёдорович", "Кандидат на вакансию Стажер", LocalDateTime.now()));
        save(new Candidate(0, "Степанов Степан Даниилович", "Кандидат на вакансию Джуниор", LocalDateTime.now()));
        save(new Candidate(0, "Наумова Ирина Егоровна", "Кандидат на вакансию Джуниор+", LocalDateTime.now()));
        save(new Candidate(0, "Егоров Алексей Евгеньевич", "Кандидат на вакансию Мидл", LocalDateTime.now()));
        save(new Candidate(0, "Киселев Антон Эмирович", "Кандидат на вакансию Мидл+", LocalDateTime.now()));
        save(new Candidate(0, "Ермаков Семён Максимович", "Кандидат на вакансию Синьор", LocalDateTime.now()));
    }

    public static MemoryCandidateRepository getInstance() {
        return INSTANCE;
    }

    @Override
    public Candidate save(Candidate candidate) {
        candidate.setId(nextId++);
        candidates.put(candidate.getId(), candidate);
        return candidate;
    }

    @Override
    public void deleteById(int id) {
        candidates.remove(id);
    }

    @Override
    public boolean update(Candidate candidate) {
        return candidates.computeIfPresent(candidate.getId(),
                (id, oldCandidate) -> new Candidate(oldCandidate.getId(), candidate.getName(),
                        candidate.getDescription(), oldCandidate.getCreationDate())) != null;
    }

    @Override
    public Optional<Candidate> findById(int id) {
        return Optional.ofNullable(candidates.get(id));
    }

    @Override
    public Collection<Candidate> findAll() {
        return candidates.values();
    }
}
