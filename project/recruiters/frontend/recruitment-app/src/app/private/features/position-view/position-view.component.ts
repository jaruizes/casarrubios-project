import {Component, OnInit} from '@angular/core';
import {PositionsService} from "../../services/positions/positions.service";
import {ActivatedRoute} from "@angular/router";
import {Position} from "../../model/position";
import {DatePipe, NgForOf, NgIf} from "@angular/common";
import {ReqAndSkillsPipe} from "../../infrastructure/pipes/requirements-and-skills-pipe.pipe";

@Component({
  selector: 'app-position-view',
  standalone: true,
  imports: [
    DatePipe,
    NgForOf,
    NgIf,
    ReqAndSkillsPipe
  ],
  templateUrl: './position-view.component.html',
  styleUrl: './position-view.component.scss'
})
export class PositionViewComponent implements OnInit {
  positionId!: number;
  position!: Position;

  constructor(private route: ActivatedRoute, private positionService: PositionsService) {
    this.positionService = positionService;
  }

  ngOnInit(): void {
    this.positionId = Number(this.route.snapshot.paramMap.get('id'));
    if (this.positionId > 0) {
      this.positionService.getPositionById(this.positionId).subscribe((position) => {
        this.position = position;
      });
    }
  }

  protected readonly Number = Number;
}
