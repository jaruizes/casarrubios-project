import {Component, OnInit} from '@angular/core';
import {PositionsService} from "../../services/positions.service";
import {ActivatedRoute, Router} from "@angular/router";
import {Position} from "../../model/position";
import {DatePipe, NgForOf, NgIf} from "@angular/common";

@Component({
  selector: 'app-position-view',
  standalone: true,
  imports: [
    DatePipe,
    NgForOf,
    NgIf
  ],
  templateUrl: './position-view.component.html',
  styleUrl: './position-view.component.scss'
})
export class PositionViewComponent implements OnInit {
  positionId!: number;
  position?: Position;
  private positionService: PositionsService;
  private router: Router;

  constructor(private route: ActivatedRoute, router: Router, positionService: PositionsService) {
    this.positionService = positionService;
    this.router = router;
  }

  ngOnInit(): void {
    this.positionId = Number(this.route.snapshot.paramMap.get('id'));
    if (this.positionId > 0) {
      this.positionService.getPositionById(this.positionId).subscribe((position) => {
        this.position = position;
      });
    }
  }

  back() {
    this.router.navigate(['home']);
  }
}
