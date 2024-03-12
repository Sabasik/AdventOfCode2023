package aoc;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ReadFile {
    public static List<String> readLines(String path) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8))) {
            return br.lines().toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}