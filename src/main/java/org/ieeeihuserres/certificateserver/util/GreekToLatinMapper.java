package org.ieeeihuserres.certificateserver.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        return arrayToList(greek.toCharArray())
                .stream()
                .map(character -> {
                    final String characterAsString = String.valueOf(character);
                    if (!(characterMap.containsKey(characterAsString.toLowerCase()))) {
                        return characterAsString;
                    }

                    final String latin = characterMap.get(characterAsString.toLowerCase());

                    if (Character.isUpperCase(character)) {
                        return latin.toUpperCase();
                    }
                    return latin;
                })
                .reduce(String::concat)
                .orElse("");
    }

    private List<Character> arrayToList(char[] toCharArray) {
        final List<Character> res = new ArrayList<>();
        for (char charToMap : toCharArray) {
            res.add(charToMap);
        }
        return res;
    }
}