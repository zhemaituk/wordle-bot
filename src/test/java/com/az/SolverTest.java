package com.az;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
}