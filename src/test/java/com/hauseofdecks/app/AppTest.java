package com.hauseofdecks.app;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for https://deckofcardsapi.com/api/deck/new/draw/?count=2. Each run is having shuffled deck.
 */
public class AppTest {
    static Response response;
    static JsonPath JSONBody;
    static int numberOfDraws;
    static int deck = 52;
    /*
     * if only deckofcardsapi provided also endpoint to list of all suits and cards in deck to elegantly declare those
     * elegantly with call to those endpoint. Haven't found them though. In similar way I would test code and value of card
     * which was drawn. See L:64
     */
    static ArrayList suits = new ArrayList(Arrays.asList("HEARTS", "DIAMONDS", "CLUBS", "SPADES"));


    @BeforeAll
    static void setup() {
        RestAssured.baseURI = "https://deckofcardsapi.com";
        numberOfDraws = 2;
        try {
            response = given().
                    get("/api/deck/new/draw/?count=" + numberOfDraws);
            JSONBody = new JsonPath(response.getBody().asString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testStatusCode() {
        response.then().statusCode(200);
    }

    @Test
    void testDeck() {
        assertTrue(JSONBody.get("success").equals(true));
        assertNotEquals(JSONBody.get("deck_id"), "");
        assertEquals(JSONBody.getInt("remaining"), deck - numberOfDraws);
    }

    @Test
    void testCards() {
        ArrayList cards = JSONBody.get("cards");
        assertEquals(cards.size(), numberOfDraws);

//      Testing 1st card
        assertNotNull(JSONBody.get("cards[0].code"));
        assertNotNull(JSONBody.get("cards[0].value"));
        assertTrue(suits.contains(JSONBody.get("cards[0].suit")));

//      Testing 1st card
        assertNotNull(JSONBody.get("cards[1].code"));
        assertNotNull(JSONBody.get("cards[1].value"));
        assertNotNull(suits.contains(JSONBody.get("cards[1].suit")));
    }
}