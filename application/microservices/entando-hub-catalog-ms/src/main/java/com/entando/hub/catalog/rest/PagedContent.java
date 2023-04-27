package com.entando.hub.catalog.rest;

import org.springframework.data.domain.Page;

import java.util.List;

public class PagedContent <T,P> {
    private final List<T> payload;
    private final Metadata<P> metadata;

    public PagedContent(List<T> payload, Page<P> pageObj ){
        this.payload=payload;
        this.metadata = new Metadata<>(pageObj);
    }

    public List<T> getPayload() {
        return payload;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public static class Metadata<P>{
        private final int page;
        private final int pageSize;
        private final int lastPage;
        private final long totalItems;

        public Metadata(Page<P> pageObj) {
            this.lastPage = pageObj.getTotalPages();
            this.totalItems = pageObj.getTotalElements();
            this.pageSize = pageObj.getSize();
            this.page = pageObj.getNumber()+1;
        }

        public int getPage() {
            return page;
        }

        public int getPageSize() {
            return pageSize;
        }

        public int getLastPage() {
            return lastPage;
        }

        public long getTotalItems() {
            return totalItems;
        }
    }

}
