package me.dakto101.util;

import java.util.Arrays;
import java.util.Map;

import org.apache.commons.lang.Validate;

import com.google.common.collect.ImmutableMap;

public class RomanNumerals {

    /**
     * Supported roman numeral characters
     */
    private enum RomanNumber {
        //It is important that the order is in reverse
        M(1000), CM(900), D(500), CD(400), C(100), XC(90), L(50), XL(40), X(10), IX(9), V(5), IV(4), I(1);

        private final int value;

        RomanNumber(final int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    /**
     * Gets the Roman Numeral string representing the given value
     *
     * @param value value to be converted
     * @return Roman Numeral String
     */
    public static String toNumerals(int value) {
        Validate.isTrue(value > 0 && value < 100000, "Roman numbers can't express < 0 or > 100000");
        final StringBuilder builder = new StringBuilder();
        for (final RomanNumber romanNumber : RomanNumber.values()) {
            while (value >= romanNumber.getValue()) {
                value -= romanNumber.getValue();
                builder.append(romanNumber.name());
            }
        }
        return builder.toString();
    }

    /**
     * Parses a Roman Numeral string into an integer
     *
     * @param romanNumeral Roman Numeral string to parse
     * @return integer value (0 if invalid string)
     */
    public static int fromNumerals(final String romanNumeral) {
        if (romanNumeral == null || romanNumeral.isEmpty()) return 0;

        int result = 0;
        int next = getNumeralValue(romanNumeral.charAt(0));
        for (int i = 1; i < romanNumeral.length(); i++) {
            final int value = next;
            next = getNumeralValue(romanNumeral.charAt(i));
            // Invalid characters hint that it isn't a roman numeral string
            if (next == 0) return 0;
            result += next > value ? -value : value;
        }
        return result + next;
    }

    /**
     * Gets the value of the given Roman Numeral character
     *
     * @param numeral Roman Numeral character
     * @return value of the character
     */
    private static int getNumeralValue(char numeral) {
        return CHAR_TO_NUMBER.getOrDefault(numeral, 0);
    }

    private static final Map<Character, Integer> CHAR_TO_NUMBER = Arrays.stream(RomanNumber.values())
            .filter(num -> num.name().length() == 1)
            .collect(ImmutableMap.toImmutableMap(num -> num.name().charAt(0), RomanNumber::getValue));
}
