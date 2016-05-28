package org.vaadin.addon.annyang.demo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by marco on 28/05/16.
 */
@RequiredArgsConstructor
public class ResultItem {
    @Getter
    private final String phrase;

    public static List<ResultItem> of(String... phrases) {
        return Arrays.stream(phrases).map(ResultItem::new).collect(Collectors.toList());
    }
}
