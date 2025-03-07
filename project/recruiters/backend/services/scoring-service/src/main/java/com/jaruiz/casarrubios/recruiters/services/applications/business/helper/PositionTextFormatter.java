package com.jaruiz.casarrubios.recruiters.services.applications.business.helper;

import com.jaruiz.casarrubios.recruiters.services.applications.business.model.Benefit;
import com.jaruiz.casarrubios.recruiters.services.applications.business.model.Position;
import com.jaruiz.casarrubios.recruiters.services.applications.business.model.Requirement;
import com.jaruiz.casarrubios.recruiters.services.applications.business.model.Task;

public class PositionTextFormatter {
    public static String convertPositionToText(Position position) {
        StringBuilder sb = new StringBuilder();

        sb.append("Title: ").append(position.getTitle()).append("\n");
        sb.append("Description: ").append(position.getDescription()).append("\n\n");

        sb.append("Requirements:\n");
        for (Requirement req : position.getRequirements()) {
            sb.append("- ").append(req.getKey());
            if (req.getMandatory()) {
                sb.append(" (Mandatory)");
            } else {
                sb.append(" (Optional)");
            }
            sb.append(" - ").append(req.getDescription()).append("\n");
        }
        sb.append("\n");

        sb.append("Benefits:\n");
        for (Benefit ben : position.getBenefits()) {
            sb.append("- ").append(ben.getDescription()).append("\n");
        }
        sb.append("\n");

        sb.append("Tasks:\n");
        for (Task task : position.getTasks()) {
            sb.append("- ").append(task.getDescription()).append("\n");
        }

        return sb.toString();
    }
}
