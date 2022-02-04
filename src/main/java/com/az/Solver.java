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
    private Set<String> guessesAndAnswers;

    public void init() {
        answers = new LinkedHashSet<>(readLines("/answers.txt"));
        guesses = new HashSet<>(readLines("/guesses.txt"));
        guessesAndAnswers = new HashSet<>();
        guessesAndAnswers.addAll(answers);
        guessesAndAnswers.addAll(guesses);
    }

    public List<String> guessesFor(String answer) {
        return guessesFor(answer, List.of());
    }

    public List<String> guessesFor(String answer, List<String> seed) {
        List<String> result = new ArrayList<>();

        Set<String> answers = this.answers;
        Set<String> guesses = this.answers;
        //        Set<String> guesses = this.guesses;
        //        Set<String> guesses = this.guessesAndAnswers;

        String match = null;
        for (String guess : seed) {
            match = match(guess, answer);
            answers = filter(answers, guess, match);
            guesses = filter(guesses, guess, match);
            result.add(guess);
        }

        while (!"GGGGG".equals(match)) {
            String guess = nextGuess(guesses, answers);
            match = match(guess, answer);
            answers = filter(answers, guess, match);
            guesses = filter(guesses, guess, match);
            result.add(guess);
        }

        return result;
    }

    private String nextGuess(Set<String> guesses, Set<String> answers) {
        int maxGuess = 0;
        String bestGuess = null;
        for (String guess : guesses) {
            Set<String> various = new HashSet<>();
            for (String answer : answers) {
                various.add(match(guess, answer));
            }
            if (various.size() >= maxGuess) {
                maxGuess = various.size();
                bestGuess = guess;
            }
        }

        return bestGuess;
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
                    final int foundIndex = indexOf(answerArray, guessChar);
                    if (foundIndex < 0) {
                        continue nextAnswer;
                    }
                    answerArray[foundIndex] = '0';
                }
            }

            for (int i = 0; i < match.length(); i++) {
                if (match.charAt(i) == 'B') {
                    if (contains(answerArray, guess.charAt(i))) {
                        continue nextAnswer;
                    }
                }
            }
            result.add(answer);
        }

        //        if (result.isEmpty()) {
        //            throw new IllegalStateException("No words left");
        //        }

        return result;
    }

    public static boolean contains(final char[] array, final char v) {
        return indexOf(array, v) >= 0;
    }

    public static int indexOf(final char[] array, final char v) {
        for (int i = 0; i < array.length; i++) {
            char e = array[i];
            if (e == v) {
                return i;
            }
        }

        return -1;
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

    public static void main(String[] args) {
        final Solver solver = new Solver();
        solver.init();

        //        Set<String> answers = solver.answers;
        //        answers = solver.filter(answers, "bingo", "BYBGB");
        //        answers = solver.filter(answers, "rates", "BYBYB");
        //        System.out.println(solver.nextGuess(answers, answers));
//        System.out.println(solver.guessesFor("pleat", List.of()));
                guessAll(solver);
    }

    private static void guessAll(Solver solver) {
        int total = 0;
        int failed = 0;
        long start = System.nanoTime();
        try (FileWriter writer = new FileWriter("results.txt")) {
            for (String answer : solver.answers) {
                List<String> result = solver.guessesFor(answer, List.of("trace"));
                total += result.size();
                if (result.size() > 6) {
                    failed++;
                }
                writer.write(String.join(",", result));
                writer.write("\n");
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        System.out.println("Time : " + (System.nanoTime() - start) / 1_000_000_000F);
        System.out.println("Total : " + total);
        System.out.println("Failed : " + failed);
    }

}
