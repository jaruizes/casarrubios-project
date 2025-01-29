import {Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {DatePipe, NgForOf, NgIf} from "@angular/common";
import {FormsModule} from "@angular/forms";
import {NgbModal, NgbModalRef, NgbPagination} from "@ng-bootstrap/ng-bootstrap";
import {PositionStatusPipePipe} from "../../infrastructure/pipes/position-status-pipe.pipe";
import {Position} from "../../model/position";
import {ActivatedRoute, Router} from "@angular/router";
import {PositionsService} from "../../services/positions.service";
import {Candidate, PositionApplied} from "../../model/candidate";
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
  position: Position;

  private router: Router;
  private candidatesService: CandidatesService;

  constructor(private route: ActivatedRoute, router: Router, candidatesService: CandidatesService) {
    this.router = router;
    this.candidatesService = candidatesService;
    this.position = {} as Position;
  }

  ngOnInit(): void {
    console.log(this.router.getCurrentNavigation()?.extras.state);

    this.position = history.state.position;
    this.candidatesService.getCandidatesByPosition(this.position.id).subscribe((candidates) => {
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

  getMatchingPercentage(positionsApplied: PositionApplied[]): number {
    return positionsApplied.filter((position) => position.id === this.position.id)[0].matchingPercentage;
  }

  downloadCV(applicationId: number, cv: string) {
    const link = document.createElement('a');
    link.href = cv;
    link.download = applicationId + '.pdf';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  }

}
