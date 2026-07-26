package com.tlavu.novacart.modules.catalog.infrastructure.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

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
}
