package com.campusconnect.service;

import com.campusconnect.dto.MatchResponse;
import com.campusconnect.entity.*;
import com.campusconnect.repository.ItemRepository;
import com.campusconnect.repository.MatchRecordRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Smart Matching Module
 * Java-based weighted similarity algorithm.
 *
 * Scoring Formula (Max 100.0):
 * - Category Match (app.matching.weight.category = 20.0 pts): Strict/fuzzy category equality.
 * - Title Similarity (app.matching.weight.title = 25.0 pts): Normalized token-overlap and Levenshtein similarity.
 * - Color Match (app.matching.weight.color = 15.0 pts): Color token comparison.
 * - Brand Match (app.matching.weight.brand = 10.0 pts): Brand token comparison.
 * - Location Match (app.matching.weight.location = 15.0 pts): Location keyword overlap / building proximity.
 * - Date Proximity (app.matching.weight.date = 10.0 pts): Time gap between lost and found dates.
 * - Description Overlap (app.matching.weight.description = 5.0 pts): Jaccard keyword coefficient.
 */
@Service
public class SmartMatchingService {

    private static final Logger logger = LoggerFactory.getLogger(SmartMatchingService.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MatchRecordRepository matchRecordRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private AuditLogService auditLogService;

    @Value("${app.matching.weight.category:20.0}")
    private double weightCategory;

    @Value("${app.matching.weight.title:25.0}")
    private double weightTitle;

    @Value("${app.matching.weight.color:15.0}")
    private double weightColor;

    @Value("${app.matching.weight.brand:10.0}")
    private double weightBrand;

    @Value("${app.matching.weight.location:15.0}")
    private double weightLocation;

    @Value("${app.matching.weight.date:10.0}")
    private double weightDate;

    @Value("${app.matching.weight.description:5.0}")
    private double weightDescription;

    @Value("${app.matching.threshold:50.0}")
    private double matchThreshold;

    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
            "a", "an", "the", "in", "on", "at", "to", "for", "of", "and", "or", "is", "was",
            "near", "inside", "outside", "room", "floor", "block", "with", "has", "lost", "found"
    ));

    /**
     * Compute similarity score between a lost item and a found item.
     */
    public MatchEvaluation evaluateMatch(Item lost, Item found) {
        List<String> reasons = new ArrayList<>();
        double totalScore = 0.0;

        // 1. Category Score (20 pts)
        if (lost.getCategory() != null && found.getCategory() != null &&
                lost.getCategory().equalsIgnoreCase(found.getCategory())) {
            totalScore += weightCategory;
            reasons.add(String.format("Exact Category Match: %s (+%.1f pts)", lost.getCategory(), weightCategory));
        }

        // 2. Title Similarity (25 pts)
        double titleSim = computeStringSimilarity(lost.getTitle(), found.getTitle());
        double titleScore = titleSim * weightTitle;
        if (titleScore > 0.0) {
            totalScore += titleScore;
            reasons.add(String.format("Title Similarity: %.0f%% (+%.1f pts)", titleSim * 100, titleScore));
        }

        // 3. Color Match (15 pts)
        if (lost.getColor() != null && found.getColor() != null) {
            double colorSim = computeTokenOverlap(lost.getColor(), found.getColor());
            if (colorSim > 0.0) {
                double colorScore = colorSim * weightColor;
                totalScore += colorScore;
                reasons.add(String.format("Color Match: %s (+%.1f pts)", lost.getColor(), colorScore));
            }
        }

        // 4. Brand Match (10 pts)
        if (lost.getBrand() != null && found.getBrand() != null) {
            double brandSim = computeStringSimilarity(lost.getBrand(), found.getBrand());
            if (brandSim > 0.4) {
                double brandScore = brandSim * weightBrand;
                totalScore += brandScore;
                reasons.add(String.format("Brand Match: %s (+%.1f pts)", lost.getBrand(), brandScore));
            }
        }

        // 5. Location Proximity (15 pts)
        if (lost.getLocation() != null && found.getLocation() != null) {
            double locSim = computeTokenOverlap(lost.getLocation(), found.getLocation());
            if (locSim > 0.0) {
                double locScore = locSim * weightLocation;
                totalScore += locScore;
                reasons.add(String.format("Location Proximity: %s (+%.1f pts)", found.getLocation(), locScore));
            }
        }

        // 6. Date Proximity (10 pts)
        if (lost.getIncidentDate() != null && found.getIncidentDate() != null) {
            long daysApart = ChronoUnit.DAYS.between(lost.getIncidentDate(), found.getIncidentDate());
            double dateScore = 0.0;
            if (daysApart == 0) {
                dateScore = weightDate;
                reasons.add(String.format("Same Day Incident (+%.1f pts)", dateScore));
            } else if (daysApart > 0 && daysApart <= 2) {
                dateScore = weightDate * 0.9;
                reasons.add(String.format("Found within %d days (+%.1f pts)", daysApart, dateScore));
            } else if (daysApart > 2 && daysApart <= 7) {
                dateScore = weightDate * 0.6;
                reasons.add(String.format("Found within %d days (+%.1f pts)", daysApart, dateScore));
            } else if (daysApart > 7 && daysApart <= 14) {
                dateScore = weightDate * 0.3;
                reasons.add(String.format("Found within 2 weeks (+%.1f pts)", dateScore));
            }
            totalScore += dateScore;
        }

        // 7. Description Keyword Overlap (5 pts)
        if (lost.getDescription() != null && found.getDescription() != null) {
            double descSim = computeKeywordJaccard(lost.getDescription(), found.getDescription());
            if (descSim > 0.1) {
                double descScore = descSim * weightDescription;
                totalScore += descScore;
                reasons.add(String.format("Description Keyword Overlap (+%.1f pts)", descScore));
            }
        }

        // Round to 1 decimal place
        double finalScore = Math.min(100.0, Math.round(totalScore * 10.0) / 10.0);
        return new MatchEvaluation(finalScore, reasons);
    }

    /**
     * Automatic re-check of matches when an item is created or updated.
     */
    @Transactional
    public void findAndRecordMatchesForItem(Item targetItem) {
        if (targetItem.getItemType() == ItemType.LOST) {
            List<Item> activeFound = itemRepository.findActiveItemsByType(ItemType.FOUND);
            for (Item found : activeFound) {
                processCandidatePair(targetItem, found);
            }
        } else if (targetItem.getItemType() == ItemType.FOUND) {
            List<Item> activeLost = itemRepository.findActiveItemsByType(ItemType.LOST);
            for (Item lost : activeLost) {
                processCandidatePair(lost, targetItem);
            }
        }
    }

    private void processCandidatePair(Item lost, Item found) {
        MatchEvaluation eval = evaluateMatch(lost, found);
        if (eval.score >= matchThreshold) {
            Optional<MatchRecord> existing = matchRecordRepository.findByLostItemIdAndFoundItemId(lost.getId(), found.getId());
            String reasonsJson;
            try {
                reasonsJson = objectMapper.writeValueAsString(eval.reasons);
            } catch (JsonProcessingException e) {
                reasonsJson = String.join(", ", eval.reasons);
            }

            MatchRecord record;
            if (existing.isPresent()) {
                record = existing.get();
                record.setSimilarityScore(eval.score);
                record.setMatchReasons(reasonsJson);
            } else {
                record = new MatchRecord(lost, found, eval.score, reasonsJson);
                matchRecordRepository.save(record);

                // Notify both users about high-confidence match
                String notifyMsg = String.format("A potential match (%.1f%% similarity) was detected between your report '%s' and found item '%s'.",
                        eval.score, lost.getTitle(), found.getTitle());

                notificationService.createNotification(
                        lost.getReporter(),
                        "Potential Match Detected (" + eval.score + "%)",
                        notifyMsg,
                        NotificationType.MATCH_FOUND,
                        found.getId(),
                        null
                );
            }
        }
    }

    @Transactional(readOnly = true)
    public List<MatchResponse> getMatchesForUser(Long userId) {
        return matchRecordRepository.findActiveMatchesForUser(userId).stream()
                .map(MatchResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MatchResponse> getAllActiveMatches() {
        return matchRecordRepository.findByStatusOrderBySimilarityScoreDesc("ACTIVE").stream()
                .map(MatchResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // Helper algorithms: Token Overlap & Normalized Levenshtein
    private double computeStringSimilarity(String s1, String s2) {
        if (s1 == null || s2 == null) return 0.0;
        String clean1 = s1.trim().toLowerCase();
        String clean2 = s2.trim().toLowerCase();
        if (clean1.equals(clean2)) return 1.0;

        double tokenOverlap = computeTokenOverlap(clean1, clean2);
        double levenshteinSim = computeNormalizedLevenshtein(clean1, clean2);
        return Math.max(tokenOverlap, levenshteinSim);
    }

    private double computeTokenOverlap(String s1, String s2) {
        Set<String> set1 = tokenize(s1);
        Set<String> set2 = tokenize(s2);
        if (set1.isEmpty() || set2.isEmpty()) return 0.0;

        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);

        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);

        return (double) intersection.size() / union.size();
    }

    private double computeKeywordJaccard(String text1, String text2) {
        Set<String> tokens1 = tokenize(text1);
        tokens1.removeAll(STOP_WORDS);
        Set<String> tokens2 = tokenize(text2);
        tokens2.removeAll(STOP_WORDS);

        if (tokens1.isEmpty() || tokens2.isEmpty()) return 0.0;

        Set<String> common = new HashSet<>(tokens1);
        common.retainAll(tokens2);

        Set<String> all = new HashSet<>(tokens1);
        all.addAll(tokens2);

        return (double) common.size() / all.size();
    }

    private Set<String> tokenize(String input) {
        if (input == null) return Collections.emptySet();
        String[] parts = input.toLowerCase().replaceAll("[^a-z0-9\\s]", " ").split("\\s+");
        Set<String> tokens = new HashSet<>();
        for (String p : parts) {
            if (p.length() > 1) tokens.add(p);
        }
        return tokens;
    }

    private double computeNormalizedLevenshtein(String s1, String s2) {
        int maxLen = Math.max(s1.length(), s2.length());
        if (maxLen == 0) return 1.0;
        int distance = levenshteinDistance(s1, s2);
        return 1.0 - ((double) distance / maxLen);
    }

    private int levenshteinDistance(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];
        for (int i = 0; i <= a.length(); i++) dp[i][0] = i;
        for (int j = 0; j <= b.length(); j++) dp[0][j] = j;

        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                int cost = (a.charAt(i - 1) == b.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1), dp[i - 1][j - 1] + cost);
            }
        }
        return dp[a.length()][b.length()];
    }

    public static class MatchEvaluation {
        public final double score;
        public final List<String> reasons;

        public MatchEvaluation(double score, List<String> reasons) {
            this.score = score;
            this.reasons = reasons;
        }
    }
}
