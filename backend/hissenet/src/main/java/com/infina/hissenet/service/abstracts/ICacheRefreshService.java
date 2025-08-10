package com.infina.hissenet.service.abstracts;

import reactor.core.publisher.Mono;

import java.util.List;

public interface ICacheRefreshService<T> {

    Mono<List<T>> buildSnapshot();
}
