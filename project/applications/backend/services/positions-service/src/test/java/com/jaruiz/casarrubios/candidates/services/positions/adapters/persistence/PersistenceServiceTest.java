package com.jaruiz.casarrubios.candidates.services.positions.adapters.persistence;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import com.jaruiz.casarrubios.candidates.services.positions.adapters.persistence.postgresql.PostgresRepository;
import com.jaruiz.casarrubios.candidates.services.positions.adapters.persistence.postgresql.entities.ConditionEntity;
import com.jaruiz.casarrubios.candidates.services.positions.adapters.persistence.postgresql.entities.PositionEntity;
import com.jaruiz.casarrubios.candidates.services.positions.adapters.persistence.postgresql.entities.RequirementEntity;
import com.jaruiz.casarrubios.candidates.services.positions.business.model.Position;
import com.jaruiz.casarrubios.candidates.services.positions.business.model.PositionsList;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import static com.jaruiz.casarrubios.candidates.services.positions.utils.AssertUtils.assertPosition;
import static com.jaruiz.casarrubios.candidates.services.positions.utils.AssertUtils.assertPositionsList;
import static com.jaruiz.casarrubios.candidates.services.positions.utils.FakeUtils.buildPositionEntityFake;
import static com.jaruiz.casarrubios.candidates.services.positions.utils.FakeUtils.buildPositionsEntityListFake;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class PersistenceServiceTest {

    @Mock
    private PostgresRepository postgresRepository;

    @InjectMocks
    private PersistenceService persistenceService;

    @Test
    public void givenAValidPositionId_whenGetDetail_thenPositionIsRetrieved() {
        given(postgresRepository.findById(1L)).willReturn(Optional.of(buildPositionEntityFake()));

        final Position position = persistenceService.getPositionById(1L);

        assertPosition(position);
    }

    @Test
    public void givenAnInvalidPositionId_whenGetDetail_thenNullIsReturned() {
        given(postgresRepository.findById(1L)).willReturn(Optional.empty());

        final Position position = persistenceService.getPositionById(1L);

        assertNull(position);
    }

    @Test
    public void givenNoPositions_whenGetPositions_thenEmptyListIsReturned() {
        var pageable = PageRequest.of(1, 10);
        var emptyPage = Page.<PositionEntity>empty(pageable);
        given(postgresRepository.findAll(pageable)).willReturn(emptyPage);

        final PositionsList positions = persistenceService.getAllPositions(1, 10);
        assertPositionsList(0, positions);
    }

    @Test
    public void givenSomePositions_whenGetPositions_thenListIsReturned() {
        var pageSize = 10;
        var page = 1;
        var total = 100L;
        var pageable = PageRequest.of(1, 10);
        var content = buildPositionsEntityListFake(page, pageSize, total);
        var positionPage = new Page<PositionEntity>() {
            @Override
            public int getTotalPages() {
                return 1;
            }

            @Override
            public long getTotalElements() {
                return total;
            }

            @Override
            public <U> Page<U> map(Function<? super PositionEntity, ? extends U> converter) {
                return null;
            }

            @Override
            public int getNumber() {
                return page;
            }

            @Override
            public int getSize() {
                return pageSize;
            }

            @Override
            public int getNumberOfElements() {
                return 100;
            }

            @Override
            public List<PositionEntity> getContent() {
                return content;
            }

            @Override
            public boolean hasContent() {
                return true;
            }

            @Override
            public Sort getSort() {
                return Sort.unsorted();
            }

            @Override
            public boolean isFirst() {
                return true;
            }

            @Override
            public boolean isLast() {
                return true;
            }

            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public boolean hasPrevious() {
                return false;
            }

            @Override
            public Pageable nextPageable() {
                return Pageable.unpaged();
            }

            @Override
            public Pageable previousPageable() {
                return Pageable.unpaged();
            }

            @NotNull
            @Override
            public Iterator<PositionEntity> iterator() {
                return content.iterator();
            }
        };

        given(postgresRepository.findAll(pageable)).willReturn(positionPage);

        final PositionsList positions = persistenceService.getAllPositions(1, 10);
        assertPositionsList(total, positions);
    }


}
