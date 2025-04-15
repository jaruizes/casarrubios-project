package com.jaruiz.casarrubios.recruiters.services.posmanager.api.rest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

import com.jaruiz.casarrubios.recruiters.services.posmanager.api.rest.dto.*;
import com.jaruiz.casarrubios.recruiters.services.posmanager.business.PositionManagerService;
import com.jaruiz.casarrubios.recruiters.services.posmanager.business.exceptions.PositionInvalidException;
import com.jaruiz.casarrubios.recruiters.services.posmanager.business.exceptions.PositionNotFoundException;
import com.jaruiz.casarrubios.recruiters.services.posmanager.business.model.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

@ApplicationScoped
public class PosManagerRestService implements PositionsApi {
    private static final Logger logger = Logger.getLogger(PosManagerRestService.class);

    private final PositionManagerService positionService;

    public PosManagerRestService(PositionManagerService positionService) {
        this.positionService = positionService;
    }

    @Override public Response getAllPositions(Integer page, Integer size) {
        logger.info("Getting all positions [page: " + page + ", pageSize: " + size + "]");

        final PositionsList positionsList = positionService.getAllPositions(page, size);
        logger.info("Positions found: " + positionsList.getTotal());
        logger.info("Returning page " + positionsList.getPage() + " of " + positionsList.getPageSize() + " positions");

        return Response.ok().entity(buildPaginatedPositionDTO(positionsList)).build();
    }

    @Override public Response getPositionDetail(Long positionId) {
        logger.info("Getting position detail with Id: " + positionId);
        final Position position = positionService.getPositionDetail(positionId);
        logger.info("Position with Id: " + positionId + " found");

        return Response.ok().entity(mapToPositionDetailDTO(position)).build();
    }

    @Override public Response updatePosition(Long positionId, PositionToUpdateDTO positionToUpdateDTO) {
        logger.info("Updating position with Id: " + positionId);

        final Position positionToUpdate = mapToPosition(positionId, positionToUpdateDTO);
        try {
            final Position positionUpdated = positionService.updatePosition(positionToUpdate);
            return Response.ok().entity(mapToPositionDetailDTO(positionUpdated)).build();
        } catch (PositionInvalidException e) {
            logger.error("Position with Id: " + positionId + " is invalid");
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (PositionNotFoundException e) {
            logger.error("Position with Id: " + positionId + " not found");
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }


    @Override public Response createPosition(NewPositionDataDTO data) {
        logger.info("Creating new position");

        final PositionData positionToBeCreated = PosManagerRestService.mapToPositionData(data);
        final Position positionCreated = positionService.createPosition(positionToBeCreated);

        logger.info("Position created with Id: " + positionCreated.getId());
        return Response.status(Response.Status.CREATED)
                       .entity(PosManagerRestService.mapToPositionDetailDTO(positionCreated))
                       .build();
    }


    private PaginatedPositionsDTO buildPaginatedPositionDTO(PositionsList positionsList) {
        final PaginatedPositionsDTO paginatedPositionsDTO = new PaginatedPositionsDTO();
        paginatedPositionsDTO.setNumber(positionsList.getPage());
        paginatedPositionsDTO.setSize(positionsList.getPageSize());
        paginatedPositionsDTO.setTotalElements(positionsList.getTotal());
        paginatedPositionsDTO.setTotalPages((int) Math.ceil((double) positionsList.getTotal() / positionsList.getPageSize()));
        paginatedPositionsDTO.setContent(positionsList.getPositions().stream()
                                                      .map(this::mapToPositionDTO)
                                                      .toList());

        return paginatedPositionsDTO;
    }

    private PositionDTO mapToPositionDTO(Position position) {
        final PositionDTO positionDTO = new PositionDTO();
        positionDTO.setId(position.getId());
        positionDTO.setTitle(position.getTitle());
        positionDTO.setDescription(position.getDescription());
        positionDTO.setStatus(position.getStatus().ordinal());
        positionDTO.setTags(position.getTags());
        if (position.getCreatedAt() != null) {
            positionDTO.setCreatedAt(toIso8601(position.getCreatedAt()));
        }

        return positionDTO;
    }

    private static PositionDetailDTO mapToPositionDetailDTO(Position position) {
        final PositionDetailDTO positionDetailDTO = new PositionDetailDTO();
        positionDetailDTO.setId(position.getId());
        positionDetailDTO.setTitle(position.getTitle());
        positionDetailDTO.setDescription(position.getDescription());
        positionDetailDTO.setStatus(position.getStatus().ordinal());
        positionDetailDTO.setTags(position.getTags());

        if (position.getCreatedAt() != null) {
            positionDetailDTO.setCreatedAt(toIso8601(position.getCreatedAt()));
        }
        if (position.getPublishedAt() != null) {
            positionDetailDTO.setPublishedAt(toIso8601(position.getPublishedAt()));
        }

        if (position.getRequirements() != null) {
            positionDetailDTO.setRequirements(position.getRequirements().stream()
                .map(PosManagerRestService::mapToRequirementDTO)
                .toList());
        }

        if (position.getBenefits() != null) {
            positionDetailDTO.setBenefits(position.getBenefits().stream()
                .map(PosManagerRestService::mapToBenefitDTO)
                .toList());
        }

        if (position.getTasks() != null) {
            positionDetailDTO.setTasks(position.getTasks().stream()
                .map(PosManagerRestService::mapToTaskDTO)
                .toList());
        }

        return positionDetailDTO;
    }

    private static RequirementDTO mapToRequirementDTO(Requirement requirement) {
        final RequirementDTO requirementDTO = new RequirementDTO();
        requirementDTO.setDescription(requirement.getDescription());
        requirementDTO.setKey(requirement.getKey());
        requirementDTO.setMandatory(requirement.isMandatory());
        requirementDTO.setValue(requirement.getValue());

        return requirementDTO;
    }

    private static BenefitDTO mapToBenefitDTO(Benefit benefit) {
        final BenefitDTO conditionDTO = new BenefitDTO();
        conditionDTO.setDescription(benefit.getDescription());

        return conditionDTO;
    }

    private static TaskDTO mapToTaskDTO(Task task) {
        final TaskDTO taskDTO = new TaskDTO();
        taskDTO.setDescription(task.getDescription());

        return taskDTO;
    }

    private static Position mapToPosition(long positionId, PositionToUpdateDTO positionToUpdateDTO) {
        final List<Requirement> requirements = positionToUpdateDTO.getRequirements().stream()
            .map(PosManagerRestService::mapToRequirement)
            .toList();

        final List<Benefit> benefits = positionToUpdateDTO.getBenefits().stream()
            .map(PosManagerRestService::mapToCondition)
            .toList();

        final List<Task> tasks = positionToUpdateDTO.getTasks().stream()
            .map(PosManagerRestService::mapToTask)
            .toList();

        final PositionData positionData = new PositionData(positionToUpdateDTO.getTitle(), positionToUpdateDTO.getDescription(), positionToUpdateDTO.getTags());
        positionData.addBenefits(benefits);
        positionData.addRequirements(requirements);
        positionData.addTasks(tasks);

        return new Position(positionId, positionData, null, null);
    }

    private static PositionData mapToPositionData(NewPositionDataDTO newPositionDataDTO) {
        final List<Requirement> requirements = newPositionDataDTO.getRequirements().stream()
            .map(PosManagerRestService::mapToRequirement)
            .toList();

        final List<Benefit> benefits = newPositionDataDTO.getBenefits().stream()
                                                        .map(PosManagerRestService::mapToCondition)
                                                        .toList();

        final List<Task> tasks = newPositionDataDTO.getTasks().stream()
            .map(PosManagerRestService::mapToTask)
            .toList();

        final PositionData positionData = new PositionData(newPositionDataDTO.getTitle(), newPositionDataDTO.getDescription(), newPositionDataDTO.getTags());
        positionData.addBenefits(benefits);
        positionData.addRequirements(requirements);
        positionData.addTasks(tasks);

        return positionData;
    }

    private static Requirement mapToRequirement(RequirementDTO requirementDTO) {
        return new Requirement(requirementDTO.getKey(), requirementDTO.getValue(), requirementDTO.getDescription(), requirementDTO.getMandatory());
    }

    private static Benefit mapToCondition(BenefitDTO benefitDTO) {
        return new Benefit(benefitDTO.getDescription());
    }

    private static Task mapToTask(TaskDTO taskDTO) {
        return new Task(taskDTO.getDescription());
    }

    private static String toIso8601(LocalDateTime date) {
        final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date utilDate = Date.from(date.toInstant(ZoneOffset.UTC));
        return df.format(utilDate);
    }
}
