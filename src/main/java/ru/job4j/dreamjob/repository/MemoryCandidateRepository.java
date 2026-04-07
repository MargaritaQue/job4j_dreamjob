package ru.job4j.dreamjob.repository;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Candidate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@ThreadSafe
@Repository
public class MemoryCandidateRepository implements CandidateRepository {

    private final AtomicInteger nextId = new AtomicInteger(0);

    private final Map<Integer, Candidate> candidates = new ConcurrentHashMap<>();

    private MemoryCandidateRepository() {
        save(new Candidate(0, "Костин Матвей Фёдорович", "Кандидат на вакансию Стажер", LocalDateTime.now()));
        save(new Candidate(0, "Степанов Степан Даниилович", "Кандидат на вакансию Джуниор", LocalDateTime.now()));
        save(new Candidate(0, "Наумова Ирина Егоровна", "Кандидат на вакансию Джуниор+", LocalDateTime.now()));
        save(new Candidate(0, "Егоров Алексей Евгеньевич", "Кандидат на вакансию Мидл", LocalDateTime.now()));
        save(new Candidate(0, "Киселев Антон Эмирович", "Кандидат на вакансию Мидл+", LocalDateTime.now()));
        save(new Candidate(0, "Ермаков Семён Максимович", "Кандидат на вакансию Синьор", LocalDateTime.now()));
    }

    @Override
    public Candidate save(Candidate candidate) {
        int value = nextId.incrementAndGet();
        candidate.setId(value);
        candidates.put(candidate.getId(), candidate);
        return candidate;
    }

    @Override
    public boolean deleteById(int id) {
        return candidates.remove(id) != null;
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
        return new ArrayList<>(candidates.values());
    }
}
