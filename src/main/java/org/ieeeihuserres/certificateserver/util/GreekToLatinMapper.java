package org.ieeeihuserres.certificateserver.util;

import java.util.HashMap;
import java.util.Map;

public class GreekToLatinMapper {

    private final Map<String, String> characterMap;

    public GreekToLatinMapper() {
        Map<String, String> characterMap = new HashMap<>();

        characterMap.put("α", "a");
        characterMap.put("ά", "a");
        characterMap.put("β", "v");
        characterMap.put("γ", "g");
        characterMap.put("δ", "d");
        characterMap.put("ε", "e");
        characterMap.put("έ", "e");
        characterMap.put("ζ", "z");
        characterMap.put("η", "i");
        characterMap.put("ή", "i");
        characterMap.put("θ", "th");
        characterMap.put("ι", "i");
        characterMap.put("ί", "i");
        characterMap.put("ϊ", "i");
        characterMap.put("κ", "k");
        characterMap.put("λ", "l");
        characterMap.put("μ", "m");
        characterMap.put("ν", "n");
        characterMap.put("ξ", "x");
        characterMap.put("ο", "o");
        characterMap.put("ό", "o");
        characterMap.put("π", "p");
        characterMap.put("ρ", "r");
        characterMap.put("σ", "s");
        characterMap.put("ς", "s");
        characterMap.put("τ", "t");
        characterMap.put("υ", "y");
        characterMap.put("ύ", "y");
        characterMap.put("φ", "f");
        characterMap.put("χ", "ch");
        characterMap.put("ψ", "ps");
        characterMap.put("ω", "o");
        characterMap.put("ώ", "o");

        this.characterMap = characterMap;
    }


    public String mapToLatin(final String greek) {
        final StringBuilder latin = new StringBuilder();
        for (int i = 0; i < greek.length(); i++) {
            latin.append(mapCharacter(greek, i));
        }
        return latin.toString();
    }

    private String mapCharacter(final String greek, final int index) {
        final char character = greek.charAt(index);
        final String latin = characterMap.get(String.valueOf(character).toLowerCase());

        if (latin == null) {
            return String.valueOf(character);
        }
        if (!Character.isUpperCase(character)) {
            return latin;
        }
        if (latin.length() == 1 || isInUpperCaseWord(greek, index)) {
            return latin.toUpperCase();
        }
        // Title case for multi-letter mappings inside regular words, e.g. Θεόδωρος -> Theodoros
        return Character.toUpperCase(latin.charAt(0)) + latin.substring(1);
    }

    private boolean isInUpperCaseWord(final String text, final int index) {
        if (index + 1 < text.length() && Character.isLetter(text.charAt(index + 1))) {
            return Character.isUpperCase(text.charAt(index + 1));
        }
        return index > 0
                && Character.isLetter(text.charAt(index - 1))
                && Character.isUpperCase(text.charAt(index - 1));
    }
}
