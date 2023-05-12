package com.entando.hub.catalog.service;

import lombok.experimental.UtilityClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@UtilityClass
public class PageHelperService {
    private static final Logger logger = LoggerFactory.getLogger(PageHelperService.class);
    private static final int MAX_PAGE_SIZE = 50;
    public static Pageable getPaging(Integer pageNum, Integer pageSize, Sort sort){
        if (pageSize <= 0 || pageSize > MAX_PAGE_SIZE) {
            logger.warn("An unexpected pageSize {} was provided. Setting maximum to {}.", pageSize, MAX_PAGE_SIZE);
            pageSize = MAX_PAGE_SIZE;
        }
        return PageRequest.of(pageNum, pageSize, sort);
    }
}
