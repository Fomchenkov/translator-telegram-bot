package com.kronver.telegram.translaterbot;

import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

/**
 * Created by slava on 10.07.17.
 * Translate to other languages.
 */
public class Translater {

    private final String TranslateApiToken = "trnsl.1.1.20170710T073529Z.631faefc04fe7462.e35d5f3184457b9be37d18e1198f62ead9f39156";

    private String sourceText;
    private String langCode;

    public Translater(String text, String lang) {
        sourceText = text;
        langCode = lang;
    }

    /**
     * Translate source text.
     * @return translated text.
     */
    public String translate() throws UnirestException {
        JsonNode node = Unirest.post("https://translate.yandex.net/api/v1.5/tr.json/translate")
                .queryString("key", TranslateApiToken)
                .queryString("lang", langCode)
                .queryString("format", "plain")
                .queryString("text", sourceText)
                .asJson().getBody();

        if (node.getObject().getInt("code") == 200) {
            return node.getObject().getJSONArray("text").getString(0);
        }

        return null;
    }

    /**
     * Try to detect user language.
     * @return found language code.
     */
    public String detectLanguage() throws UnirestException {
        JsonNode node = Unirest.post("https://translate.yandex.net/api/v1.5/tr.json/detect")
                .queryString("key", TranslateApiToken)
                .queryString("text", sourceText)
                .asJson().getBody();

        if (node.getObject().getInt("code") == 200) {
            return node.getObject().getString("lang");
        }

        return null;
    }
}
