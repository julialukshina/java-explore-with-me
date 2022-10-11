package ru.yandex.practicum.service;

import org.springframework.data.domain.Sort;

/**
 * Создан собственный Pageable
 */
public class Pageable implements org.springframework.data.domain.Pageable {
    private final int offset;
    private final int limit;
    private final Sort sort;

    public Pageable(int offset, int limit, Sort sort) {
        this.offset = offset;
        this.limit = limit;
        this.sort = sort;
    }

    public static org.springframework.data.domain.Pageable of(Integer from, Integer size) {
        return new Pageable(from, size, Sort.unsorted());
    }

    public static org.springframework.data.domain.Pageable of(Integer from, Integer size, Sort sort) {
        return new Pageable(from, size, sort);
    }

    @Override
    public int getPageNumber() {
        return offset / limit;
    }

    @Override
    public int getPageSize() {
        return limit;
    }

    @Override
    public long getOffset() {
        return offset;
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    @Override
    public org.springframework.data.domain.Pageable next() {
        return new Pageable(offset + limit, limit, sort);
    }

    @Override
    public org.springframework.data.domain.Pageable previousOrFirst() {
        return new Pageable(offset, limit, sort);
    }

    @Override
    public org.springframework.data.domain.Pageable first() {
        return new Pageable(offset, limit, sort);
    }

    @Override
    public org.springframework.data.domain.Pageable withPage(int pageNumber) {
        return new Pageable(offset + limit * pageNumber, limit, sort);
    }

    @Override
    public boolean hasPrevious() {
        return false;
    }
}