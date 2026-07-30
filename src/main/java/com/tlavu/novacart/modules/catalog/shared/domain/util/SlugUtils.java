package com.tlavu.novacart.modules.catalog.shared.domain.util;

import lombok.experimental.UtilityClass;

import java.text.Normalizer;

@UtilityClass
public class SlugUtils {

    public static final int MAX_PRODUCT_SLUG_LENGTH = 255;

    public String generate(String input) {
        if (input == null) return "";

        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);

        return normalized
                .replaceAll("\\p{M}", "")
                .replace("đ", "d")
                .replace("Đ", "d")
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
    }

    /**
     * Creates the first or a suffixed slug candidate from an already-normalized
     * base slug. Candidate {@code 1} is the base itself; later candidates use
     * {@code -2}, {@code -3}, and so on.
     */
    public String createCandidate(String baseSlug, int candidateNumber) {
        if (baseSlug == null || baseSlug.isBlank()) {
            throw new IllegalArgumentException("Slug base must not be blank");
        }

        if (candidateNumber < 1) {
            throw new IllegalArgumentException("Slug candidate number must be at least 1");
        }

        String suffix = candidateNumber == 1 ? "" : "-" + candidateNumber;
        int maximumBaseLength = MAX_PRODUCT_SLUG_LENGTH - suffix.length();

        return baseSlug.substring(0, Math.min(baseSlug.length(), maximumBaseLength)) + suffix;
    }
}
