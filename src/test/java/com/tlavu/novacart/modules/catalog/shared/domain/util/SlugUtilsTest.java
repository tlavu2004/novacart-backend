package com.tlavu.novacart.modules.catalog.shared.domain.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class SlugUtilsTest {

    @ParameterizedTest
    @CsvSource({
            "'  Wireless Mouse  ', wireless-mouse",
            "'Café & Tea', cafe-tea",
            "'Đồ Điện Tử', do-dien-tu",
            "'Hello---World!!!', hello-world",
            "'Version 2.0 / Pro', version-2-0-pro",
            "'___Already Slug___', already-slug"
    })
    void generate_whenInputContainsFormatting_returnsNormalizedSlug(
            String input,
            String expected
    ) {
        assertThat(SlugUtils.generate(input)).isEqualTo(expected);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "   ", "---", "!!!"})
    void generate_whenInputHasNoSlugCharacters_returnsEmptySlug(String input) {
        assertThat(SlugUtils.generate(input)).isEmpty();
    }

    @ParameterizedTest
    @CsvSource({
            "ao-thun-trang, 1, ao-thun-trang",
            "ao-thun-trang, 2, ao-thun-trang-2",
            "ao-thun-trang, 3, ao-thun-trang-3"
    })
    void createCandidate_whenBaseAndNumberAreValid_returnsExpectedSlug(
            String baseSlug,
            int candidateNumber,
            String expected
    ) {
        assertThat(SlugUtils.createCandidate(baseSlug, candidateNumber)).isEqualTo(expected);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 50})
    void createCandidate_whenBaseIsAtMaximumLength_keepsCandidateWithinColumnLimit(int candidateNumber) {
        String baseSlug = "a".repeat(SlugUtils.MAX_PRODUCT_SLUG_LENGTH);

        String candidate = SlugUtils.createCandidate(baseSlug, candidateNumber);

        assertThat(candidate).hasSize(SlugUtils.MAX_PRODUCT_SLUG_LENGTH);
        if (candidateNumber > 1) {
            assertThat(candidate).endsWith("-" + candidateNumber);
        }
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "   "})
    void createCandidate_whenBaseIsBlank_throwsIllegalArgumentException(String baseSlug) {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> SlugUtils.createCandidate(baseSlug, 1))
                .withMessage("Slug base must not be blank");
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void createCandidate_whenNumberIsLessThanOne_throwsIllegalArgumentException(int candidateNumber) {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> SlugUtils.createCandidate("ao-thun-trang", candidateNumber))
                .withMessage("Slug candidate number must be at least 1");
    }
}
