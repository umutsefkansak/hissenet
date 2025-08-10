package com.infina.hissenet.service.abstracts;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public interface ICacheFacade {

    <T> List<T> getSnapshot(String cacheName);
    <T> void putIfNonEmpty(String cacheName, List<T> data);
    <T> Optional<T> findByCode(String cacheName, String code, Function<T, String> codeExtractor);
}
