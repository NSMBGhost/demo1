package com.example.demo;

import com.vdurmont.emoji.EmojiParser;
import org.junit.jupiter.api.Test;

public class test {
    @Test
    public void te()
    {
        String str = "An :grinning:awesome :smiley:string &#128516;with a few :wink:emojis!";
        String result = EmojiParser.parseToUnicode(str);
        System.out.println(result);
    }
}
