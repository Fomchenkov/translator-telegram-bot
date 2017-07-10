package com.kronver.telegram.translaterbot;

import com.mashape.unirest.http.exceptions.UnirestException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Translate Telegram Bot.
 * @author Fomchenkov Vyacheslav.
 * @version 10.07.2017.
 */
public class Bot extends TelegramLongPollingBot {

    private final String BotToken = "387480715:AAGE4QwIkF6OaTaCln8vB4eAWPwD8Nes_fM";
    private final String BotUsername = "ewtgjerkgltjnblktnbtbnkbot";

    private HashMap<Long, String> userCodes = new HashMap<>();

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message_text = update.getMessage().getText();
            long chat_id = update.getMessage().getChatId();
            long uid = update.getMessage().getFrom().getId();

            switch (message_text) {
                case "/start": {
                    String answer = "Я бот-переводчик. Выберите язык, на который я должен буду переводить. ";
                    answer += "(Что бы сбросить настройку, отправьте команду /revoke)";
                    String markupString = "ru;en;zh;fr";

                    SendMessage message = new SendMessage()
                            .setChatId(chat_id)
                            .setText(answer)
                            .setReplyMarkup(createInlineKeyboardMarkup(markupString));

                    try {
                        sendMessage(message);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case "/revoke": {

                    userCodes.remove(uid);

                    String answer = "Ваш язык сброшен. Теперь вам нужно заново выбрать язык. ";
                    answer += "Для этого отправьте комнду /start";

                    SendMessage message = new SendMessage()
                            .setChatId(chat_id)
                            .setText(answer);

                    try {
                        sendMessage(message);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                default:
                    if (userCodes.get(uid) != null) {
                        String langCode = userCodes.get(uid);
                        Translater trs = new Translater(message_text, langCode);
                        String answer = "Не могу перевети.";

                        try {
                            answer = trs.translate();
                        } catch (UnirestException e) {
                            System.out.println(e.getMessage());
                        }

                        SendMessage message = new SendMessage()
                                .setChatId(chat_id)
                                .setText(answer);
                        try {
                            sendMessage(message);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    } else {
                        String answer = "Сначала выберите язык, на который я должен переводить. ";
                        answer += "Для этого отправьте команду /start";

                        SendMessage message = new SendMessage()
                                .setChatId(chat_id)
                                .setText(answer);
                        try {
                            sendMessage(message);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        } else if (update.hasCallbackQuery()) {
            String langCode = update.getCallbackQuery().getData();
            long uid = update.getCallbackQuery().getFrom().getId();
            long chat_id = update.getCallbackQuery().getMessage().getChatId();

            userCodes.put(uid, langCode);

            String answer = "Выбран язык *" + langCode + "*.\nЯ готов к переводу.";

            SendMessage message = new SendMessage()
                    .setChatId(chat_id)
                    .setParseMode("markdown")
                    .setText(answer);
            try {
                sendMessage(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Create inline markup from string.
     * @param buttons - string type as "button1;button2;button3"
     * @return InlineKeyboardMarkup - ready inline markup object.
     */
    private InlineKeyboardMarkup createInlineKeyboardMarkup(String buttons) {
        String[] buttonsArray = buttons.split(";");

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        for (String button : buttonsArray) {
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            rowInline.add(new InlineKeyboardButton().setText(button)
                    .setCallbackData(button));
            rowsInline.add(rowInline);
        }

        markupInline.setKeyboard(rowsInline);
        return markupInline;
    }

    @Override
    public String getBotUsername() {
        return BotUsername;
    }

    @Override
    public String getBotToken() {
        return BotToken;
    }
}
