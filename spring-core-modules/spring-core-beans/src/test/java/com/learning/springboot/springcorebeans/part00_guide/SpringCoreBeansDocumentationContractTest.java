package com.learning.springboot.springcorebeans.part00_guide;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

public class SpringCoreBeansDocumentationContractTest {

    private static final Path MODULE_ROOT = findModuleRoot();
    private static final Path DOCS_DIR = MODULE_ROOT.resolve("docs");
    private static final Pattern MARKDOWN_LINK = Pattern.compile("!?\\[[^\\]]*]\\(([^)]+)\\)");
    private static final Pattern TEST_CLASS_REFERENCE = Pattern.compile("SpringCoreBeans[A-Za-z0-9_]*Test");

    @Test
    void readmeDirectoryListsEveryDocsMarkdownFile() throws IOException {
        Set<String> documented = localMarkdownTargets(MODULE_ROOT.resolve("README.md")).stream()
                .filter(path -> path.startsWith(DOCS_DIR))
                .filter(path -> path.getFileName().toString().endsWith(".md"))
                .map(SpringCoreBeansDocumentationContractTest::moduleRelativePath)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        Set<String> actual = docsMarkdownFiles().stream()
                .map(SpringCoreBeansDocumentationContractTest::moduleRelativePath)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        assertThat(documented)
                .as("README.md 是 docs/ 的唯一顺序来源；新增文档必须出现在 README 目录中")
                .containsAll(actual);
    }

    @Test
    void allLocalMarkdownLinksResolve() throws IOException {
        List<String> brokenLinks = markdownSources().stream()
                .flatMap(source -> markdownLinks(source).stream())
                .map(link -> resolveLocalTarget(link)
                        .filter(target -> !Files.exists(target))
                        .map(target -> moduleRelativePath(link.source()) + " -> " + link.rawTarget())
                        .orElse(null))
                .filter(value -> value != null)
                .toList();

        assertThat(brokenLinks)
                .as("README 与 docs 中的本地 Markdown 链接必须可解析")
                .isEmpty();
    }

    @Test
    void readerFacingDocsHaveChapterCardEntry() throws IOException {
        List<String> missingMarkers = docsMarkdownFiles().stream()
                .filter(path -> !path.getFileName().toString().startsWith("deepening-"))
                .filter(path -> {
                    String text = read(path);
                    return !text.contains("<!-- CHAPTER-CARD:START -->")
                            || !text.contains("<!-- CHAPTER-CARD:END -->");
                })
                .map(SpringCoreBeansDocumentationContractTest::moduleRelativePath)
                .toList();

        assertThat(missingMarkers)
                .as("面向读者的正文页必须保留章节入口卡片；维护型 deepening-* 文档除外")
                .isEmpty();
    }

    @Test
    void documentedSpringCoreBeansTestClassesExist() throws IOException {
        Set<String> existingTestClasses = testClassNames();
        Set<String> referencedTestClasses = markdownSources().stream()
                .flatMap(source -> referencedTestClasses(source).stream())
                .collect(Collectors.toCollection(LinkedHashSet::new));

        Set<String> missing = referencedTestClasses.stream()
                .filter(reference -> !existingTestClasses.contains(reference))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        assertThat(missing)
                .as("文档中引用的 SpringCoreBeans*Test 必须存在，避免 Lab 入口漂移")
                .isEmpty();
    }

    private static List<Path> markdownSources() throws IOException {
        List<Path> sources = new ArrayList<>();
        sources.add(MODULE_ROOT.resolve("README.md"));
        sources.addAll(docsMarkdownFiles());
        return sources;
    }

    private static List<Path> docsMarkdownFiles() throws IOException {
        try (Stream<Path> stream = Files.list(DOCS_DIR)) {
            return stream
                    .filter(path -> path.getFileName().toString().endsWith(".md"))
                    .sorted()
                    .toList();
        }
    }

    private static List<MarkdownLink> markdownLinks(Path source) {
        Matcher matcher = MARKDOWN_LINK.matcher(read(source));
        List<MarkdownLink> links = new ArrayList<>();
        while (matcher.find()) {
            links.add(new MarkdownLink(source, matcher.group(1).trim()));
        }
        return links;
    }

    private static List<Path> localMarkdownTargets(Path source) {
        return markdownLinks(source).stream()
                .map(SpringCoreBeansDocumentationContractTest::resolveLocalTarget)
                .flatMap(Optional::stream)
                .toList();
    }

    private static Optional<Path> resolveLocalTarget(MarkdownLink link) {
        String target = link.rawTarget();
        if (target.startsWith("<") && target.endsWith(">")) {
            target = target.substring(1, target.length() - 1);
        }
        if (target.isBlank() || target.startsWith("#") || hasExternalScheme(target)) {
            return Optional.empty();
        }

        int queryIndex = target.indexOf('?');
        int anchorIndex = target.indexOf('#');
        int endIndex = target.length();
        if (queryIndex >= 0) {
            endIndex = Math.min(endIndex, queryIndex);
        }
        if (anchorIndex >= 0) {
            endIndex = Math.min(endIndex, anchorIndex);
        }

        String pathPart = target.substring(0, endIndex).trim();
        if (pathPart.isBlank()) {
            return Optional.empty();
        }
        return Optional.of(link.source().getParent().resolve(pathPart).normalize());
    }

    private static boolean hasExternalScheme(String target) {
        String lower = target.toLowerCase(Locale.ROOT);
        return lower.startsWith("http:")
                || lower.startsWith("https:")
                || lower.startsWith("mailto:");
    }

    private static List<String> referencedTestClasses(Path source) {
        Matcher matcher = TEST_CLASS_REFERENCE.matcher(read(source));
        Set<String> references = new LinkedHashSet<>();
        while (matcher.find()) {
            references.add(matcher.group());
        }
        return new ArrayList<>(references);
    }

    private static Set<String> testClassNames() throws IOException {
        Path testJavaRoot = MODULE_ROOT.resolve("src/test/java");
        try (Stream<Path> stream = Files.walk(testJavaRoot)) {
            return stream
                    .filter(path -> path.getFileName().toString().endsWith(".java"))
                    .map(path -> path.getFileName().toString().replaceFirst("\\.java$", ""))
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        }
    }

    private static String read(Path path) {
        try {
            return Files.readString(path);
        }
        catch (IOException ex) {
            throw new IllegalStateException("Cannot read " + path, ex);
        }
    }

    private static String moduleRelativePath(Path path) {
        return MODULE_ROOT.relativize(path.normalize()).toString().replace('\\', '/');
    }

    private static Path findModuleRoot() {
        Path cwd = Path.of("").toAbsolutePath().normalize();
        List<Path> candidates = new ArrayList<>();
        candidates.add(cwd);

        Path cursor = cwd;
        while (cursor != null) {
            candidates.add(cursor.resolve("spring-core-modules/spring-core-beans"));
            cursor = cursor.getParent();
        }

        for (Path candidate : candidates) {
            if (Files.isRegularFile(candidate.resolve("pom.xml"))
                    && Files.isDirectory(candidate.resolve("docs"))
                    && "spring-core-beans".equals(candidate.getFileName().toString())) {
                return candidate;
            }
        }
        throw new IllegalStateException("Cannot locate spring-core-beans module root from " + cwd);
    }

    private record MarkdownLink(Path source, String rawTarget) {
    }
}
