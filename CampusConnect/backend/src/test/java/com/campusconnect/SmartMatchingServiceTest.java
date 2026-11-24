package com.campusconnect;

import com.campusconnect.entity.Item;
import com.campusconnect.entity.ItemType;
import com.campusconnect.service.SmartMatchingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class SmartMatchingServiceTest {

    private SmartMatchingService matchingService;

    @BeforeEach
    public void setup() {
        matchingService = new SmartMatchingService();
        // Inject default weights for unit test
        ReflectionTestUtils.setField(matchingService, "weightCategory", 20.0);
        ReflectionTestUtils.setField(matchingService, "weightTitle", 25.0);
        ReflectionTestUtils.setField(matchingService, "weightColor", 15.0);
        ReflectionTestUtils.setField(matchingService, "weightBrand", 10.0);
        ReflectionTestUtils.setField(matchingService, "weightLocation", 15.0);
        ReflectionTestUtils.setField(matchingService, "weightDate", 10.0);
        ReflectionTestUtils.setField(matchingService, "weightDescription", 5.0);
        ReflectionTestUtils.setField(matchingService, "matchThreshold", 50.0);
    }

    @Test
    @DisplayName("Should produce high match score for identical lost and found wallet on same day")
    public void testHighMatchScore() {
        Item lost = new Item();
        lost.setItemType(ItemType.LOST);
        lost.setTitle("Black Leather Wallet");
        lost.setCategory("Wallets");
        lost.setColor("Black");
        lost.setBrand("Titan");
        lost.setLocation("College Library");
        lost.setIncidentDate(LocalDate.of(2026, 11, 20));
        lost.setDescription("Black leather wallet with cards");

        Item found = new Item();
        found.setItemType(ItemType.FOUND);
        found.setTitle("Black Leather Wallet");
        found.setCategory("Wallets");
        found.setColor("Black");
        found.setBrand("Titan");
        found.setLocation("College Library");
        found.setIncidentDate(LocalDate.of(2026, 11, 20));
        found.setDescription("Black leather wallet found on table");

        SmartMatchingService.MatchEvaluation eval = matchingService.evaluateMatch(lost, found);

        assertTrue(eval.score >= 85.0, "Score should be at least 85% for closely matching items");
        assertFalse(eval.reasons.isEmpty(), "Reasons list should not be empty");
        assertTrue(eval.reasons.stream().anyMatch(r -> r.contains("Category Match")));
    }

    @Test
    @DisplayName("Should produce low match score for completely distinct items")
    public void testLowMatchScore() {
        Item lost = new Item();
        lost.setItemType(ItemType.LOST);
        lost.setTitle("Blue Umbro Sports Bag");
        lost.setCategory("Bags");
        lost.setColor("Blue");
        lost.setBrand("Umbro");
        lost.setLocation("Football Ground");
        lost.setIncidentDate(LocalDate.of(2026, 10, 1));
        lost.setDescription("Heavy sports bag with shoes");

        Item found = new Item();
        found.setItemType(ItemType.FOUND);
        found.setTitle("Gold Ring");
        found.setCategory("Accessories");
        found.setColor("Gold");
        found.setBrand("Tanishq");
        found.setLocation("Main Chemistry Lab");
        found.setIncidentDate(LocalDate.of(2026, 11, 20));
        found.setDescription("Small gold band ring");

        SmartMatchingService.MatchEvaluation eval = matchingService.evaluateMatch(lost, found);

        assertTrue(eval.score < 30.0, "Score should be low for completely different items");
    }
}
