package ru.job4j.dreamjob.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sql2o.Sql2o;
import ru.job4j.dreamjob.configuration.DatasourceConfiguration;
import ru.job4j.dreamjob.model.User;

import java.util.Optional;
import java.util.Properties;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class Sql2oUserRepositoryTest {

    private static Sql2oUserRepository sql2oUserRepository;

    private static Sql2o sql2o;

    @BeforeAll
    public static void initRepositories() throws Exception {
        var properties = new Properties();
        try (var inputStream = Sql2oUserRepositoryTest.class
                .getClassLoader()
                .getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        sql2o = configuration.databaseClient(datasource);

        try (var connection = sql2o.open()) {
            connection.createQuery("""
                    CREATE TABLE IF NOT EXISTS users
                    (
                        id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                        email VARCHAR UNIQUE NOT NULL,
                        name VARCHAR NOT NULL,
                        password VARCHAR NOT NULL
                    )
                    """).executeUpdate();
        }

        sql2oUserRepository = new Sql2oUserRepository(sql2o);
    }

    @AfterEach
    public void clearUsers() {
        try (var connection = sql2o.open()) {
            connection.createQuery("DELETE FROM users").executeUpdate();
        }
    }

    @Test
    public void whenSaveUserThenFindIt() {
        var user = new User(null, "test@mail.ru", "Petr", "123");

        var savedUser = sql2oUserRepository.save(user);
        var foundUser = sql2oUserRepository.findByEmailAndPassword("test@mail.ru", "123");

        assertThat(savedUser).isPresent();
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get()).usingRecursiveComparison().isEqualTo(savedUser.get());
    }

    @Test
    public void whenFindByInvalidEmailAndPasswordThenEmptyOptional() {
        var result = sql2oUserRepository.findByEmailAndPassword("wrong@mail.ru", "wrong");

        assertThat(result).isEqualTo(Optional.empty());
    }

    @Test
    public void whenSaveTwoUsersWithSameEmailThenSecondUserNotSaved() {
        var firstUser = new User(null, "same@mail.ru", "Petr", "123");
        var secondUser = new User(null, "same@mail.ru", "Ivan", "456");

        var firstSaved = sql2oUserRepository.save(firstUser);
        var secondSaved = sql2oUserRepository.save(secondUser);

        assertThat(firstSaved).isPresent();
        assertThat(secondSaved).isEmpty();
    }
}
