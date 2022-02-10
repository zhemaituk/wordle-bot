package com.az;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SolverTest {

    static Solver solver = new Solver();

    @BeforeAll
    static void beforeAll() {
        solver.init();
    }

    @Test
    public void testMatch() {
        assertEquals("BGBGG", solver.match("kitty", "fifty"));
        assertEquals("GGGGG", solver.match("kitty", "kitty"));
    }

    @Test
    public void testFilter() {
        assertEquals(Set.of("fifty"), solver.filter(Set.of("kitty", "fifty"), "kitty", "BGBGG"));
        assertEquals(Set.of("plush"), solver.filter(Set.of("plush", "lupus"), "lupus", "YYYBY"));
    }

    @Test
    public void testFilter2() {
        assertEquals(Set.of("allay"), solver.filter(Set.of("bylaw", "allay"), "villa", "BBGYY"));
    }

    @Test
    public void testFilter3() {
        assertEquals(Set.of("baker"), solver.filter(Set.of("taker", "baker"), "taker", "BGGGG"));
    }

    @Test
    public void testBuild() {
        Solver solver = new Solver();
        solver.init();
        solver.build("salet",
                Set.of("salet", "waker", "maker", "baker"),
                Set.of("salet", "caaed", "fayer", "jaker", "waker", "maker", "baker", "gamer", "parer", "wafer")
        );
        System.out.println(solver.resolve("baker"));
        System.out.println(solver.resolve("maker"));
    }

    @Test
    public void testBuild2() {
        Solver solver = new Solver();
        solver.init();
        final Set<String> answers = Set.of("salet",
                "hilly",
                "dilly",
                "filly",
                "willy",
                "filmy",
                "holly",
                "igloo",
                "jolly",
                "milky",
                "moldy",
                "mulch",
                "nylon",
                "polyp",
                "pulpy",
                "bulky",
                "bully",
                "dully",
                "colon",
                "folio",
                "billy",
                "color");
        Set<String> guesses = new LinkedHashSet<>();
        guesses.addAll(Set.of("salet",
                "colin",
                "gulfy",
                "bilgy",
                "hilly",
                "dilly",
                "filly",
                "caaed",
                "fayer",
                "jaker",
                "waker",
                "maker",
                "baker",
                "gamer",
                "parer",
                "wafer",
                "filmy",
                "holly",
                "igloo",
                "jolly",
                "gulpy",
                "dully"));
        guesses.addAll(answers);

        solver.build("salet",
                answers,
                guesses
        );
        int max = 0;
        for (String answer : answers) {
            final List<String> resolve = solver.resolve(answer);
            max = Math.max(max, resolve.size());
            System.out.println(resolve);
        }
        System.out.println(max);
    }

}