import {Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {DatePipe, NgForOf, NgIf} from "@angular/common";
import {FormsModule} from "@angular/forms";
import {NgbModal, NgbModalRef, NgbPagination} from "@ng-bootstrap/ng-bootstrap";
import {PositionStatusPipePipe} from "../../infrastructure/pipes/position-status-pipe.pipe";
import {Position} from "../../model/position";
import {ActivatedRoute, Router} from "@angular/router";
import {PositionsService} from "../../services/positions.service";
import {Candidate} from "../../model/candidate";
import {CandidatesService} from "../../services/candidates.service";

@Component({
  selector: 'app-candidates',
  standalone: true,
    imports: [
        DatePipe,
        FormsModule,
        NgForOf,
        NgIf,
        NgbPagination,
        PositionStatusPipePipe
    ],
  templateUrl: './candidates.component.html',
  styleUrl: './candidates.component.scss'
})
export class CandidatesComponent implements OnInit {

  searchTerm: string = '';
  total: number = 10;
  pageSize: number = 10;
  page: number = 1;
  candidates: Candidate[] = [];
  positionId!: number;

  private router: Router;
  private candidatesService: CandidatesService;

  constructor(private route: ActivatedRoute, router: Router, candidatesService: CandidatesService) {
    this.router = router;
    this.candidatesService = candidatesService;
  }

  ngOnInit(): void {
    this.positionId = Number(this.route.snapshot.paramMap.get('id'));
    this.candidatesService.getCandidatesByPosition(this.positionId).subscribe((candidates) => {
      this.candidates = candidates;
    });
  }

  get filteredResults() {
    let searchTerm = this.searchTerm.toLowerCase();
    return this.candidates.filter(item =>
      item.name.toLowerCase().includes(searchTerm) ||
      item.tags.filter(tag => tag.name.toLowerCase().includes(searchTerm)).length > 0
    );
  }
}
