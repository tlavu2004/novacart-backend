package com.tlavu.novacart.modules.catalog.infrastructure.util;

import lombok.experimental.UtilityClass;

import java.text.Normalizer;

@UtilityClass
public class SlugUtils {

    public String generate(String input) {
        if (input == null) return "";
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized
                .replaceAll("\\p{M}", "")        // bỏ dấu
                .replace("đ", "d")             // xử lý đ riêng
                .replace("Đ", "d")
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")    // thay ký tự đặc biệt bằng -
                .replaceAll("^-|-$", "");         // bỏ - ở đầu và cuối
    }
}
