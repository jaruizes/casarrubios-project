package com.jaruiz.casarrubios.recruiters.services.posmanager.business.model;

import java.util.List;

public class PositionsList {
    private final long total;
    private final int page;
    private final int pageSize;
    private final List<Position> positions;

    public PositionsList(long total, int page, int pageSize, List<Position> positions) {
        this.total = total;
        this.page = page;
        this.pageSize = pageSize;
        this.positions = positions;
    }

    public long getTotal() {
        return total;
    }

    public int getPage() {
        return page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public List<Position> getPositions() {
        return positions;
    }
}
