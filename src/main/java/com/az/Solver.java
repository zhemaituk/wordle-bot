package com.az;

import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * https://freshman.dev/wordle/#/leaderboard
 */
public class Solver {

    private Set<String> answers;
    private Set<String> guesses;

    public void init() {
        answers = new LinkedHashSet<>(readLines("/answers.txt"));
        guesses = new HashSet<>(readLines("/guesses.txt"));
    }

    public List<String> guessesFor(String answer) {
        return guessesFor(answer, List.of());
    }

    public List<String> guessesFor(String answer, List<String> seed) {
        List<String> result = new ArrayList<>();

        Set<String> answers = this.answers;

        String match = null;
        for (String guess : seed) {
            match = match(guess, answer);
            answers = filter(answers, guess, match);
            result.add(guess);
        }

        while (!"GGGGG".equals(match)) {
            String guess = nextGuess(answers);
            match = match(guess, answer);
            answers = filter(answers, guess, match);
            result.add(guess);
        }

        return result;
    }

    private String nextGuess(Set<String> answers) {
        int max = 0;
        String best = null;
        for (String guess : answers) {
            Set<String> various = new HashSet<>();
            for (String answer : answers) {
                various.add(match(guess, answer));
            }
            if (various.size() >= max) {
                max = various.size();
                best = guess;
            }
        }

        return best;
    }

    Set<String> filter(Set<String> answers, String guess, String match) {
        Set<String> result = new HashSet<>();

        nextAnswer:
        for (String answer : answers) {
            char[] answerArray = answer.toCharArray();

            for (int i = 0; i < match.length(); i++) {
                if (match.charAt(i) == 'G') {
                    if (answerArray[i] != guess.charAt(i)) {
                        continue nextAnswer;
                    }
                    answerArray[i] = '0';
                }
            }

            for (int i = 0; i < match.length(); i++) {
                char guessChar = guess.charAt(i);
                if (match.charAt(i) == 'Y') {
                    if (guessChar == answerArray[i]) {
                        continue nextAnswer;
                    }
                    final int foundIndex = answer.indexOf(guessChar);
                    if (foundIndex < 0) {
                        continue nextAnswer;
                    }
                    answerArray[foundIndex] = '0';
                }
            }

            for (int i = 0; i < match.length(); i++) {
                if (match.charAt(i) == 'B') {
                    if (contains2(answerArray, guess.charAt(i))) {
                        continue nextAnswer;
                    }
                }
            }
            result.add(answer);
        }

        if (result.isEmpty()) {
            throw new IllegalStateException("No words left");
        }

        return result;
    }

    public static boolean contains2(final char[] array, final char v) {
        for (final char e : array) {
            if (e == v) {
                return true;
            }
        }

        return false;
    }

    String match(String guess, String answer) {
        char[] guessArray = guess.toCharArray();
        char[] answerArray = answer.toCharArray();
        char[] result = new char[5];

        for (int i = 0; i < guessArray.length; i++) {
            char guessChar = guessArray[i];
            if (guessChar == answerArray[i]) {
                result[i] = 'G';
                answerArray[i] = '0';
            }
        }

        outer:
        for (int i = 0; i < guessArray.length; i++) {
            if (result[i] > 0) {
                continue;
            }

            char guessChar = guessArray[i];
            for (int j = 0; j < answerArray.length; j++) {
                if (guessChar == answerArray[j]) {
                    result[i] = 'Y';
                    answerArray[j] = '0';
                    continue outer;
                }
            }
            result[i] = 'B';
        }
        return String.valueOf(result);
    }

    private List<String> readLines(String path) {
        try {
            return Arrays.asList(Files.readString(Paths.get(getClass().getResource(path).toURI())).split("\n"));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws URISyntaxException, IOException {
        final Solver solver = new Solver();
        solver.init();

        try (FileWriter writer = new FileWriter("results.txt")) {
            for (String answer : solver.answers) {
                List<String> result = solver.guessesFor(answer, List.of("trace"));
                writer.write(String.join(",", result));
                writer.write("\n");
            }
        }
    }

}
