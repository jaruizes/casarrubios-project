import {Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {DatePipe, NgForOf, NgIf} from "@angular/common";
import {FormsModule} from "@angular/forms";
import {NgbModal, NgbModalRef, NgbPagination} from "@ng-bootstrap/ng-bootstrap";
import {PositionStatusPipePipe} from "../../infrastructure/pipes/position-status-pipe.pipe";
import {Position} from "../../model/position";
import {ActivatedRoute, Router} from "@angular/router";
import {PositionsService} from "../../services/positions.service";
import {Application, PositionApplied} from "../../model/application";
import {ApplicationsService} from "../../services/applications.service";

@Component({
  selector: 'app-applications',
  standalone: true,
    imports: [
        DatePipe,
        FormsModule,
        NgForOf,
        NgIf,
        NgbPagination,
        PositionStatusPipePipe
    ],
  templateUrl: './applications.component.html',
  styleUrl: './applications.component.scss'
})
export class ApplicationsComponent implements OnInit {

  searchTerm: string = '';
  total: number = 10;
  pageSize: number = 10;
  page: number = 1;
  applications: Application[] = [];
  position: Position;

  private router: Router;
  private applicationsService: ApplicationsService;

  constructor(private route: ActivatedRoute, router: Router, candidatesService: ApplicationsService) {
    this.router = router;
    this.applicationsService = candidatesService;
    this.position = {} as Position;
  }

  ngOnInit(): void {
    console.log(this.router.getCurrentNavigation()?.extras.state);

    this.position = history.state.position;
    this.applicationsService.getCandidatesByPosition(this.position.id).subscribe((candidates) => {
      this.applications = candidates;
    });
  }

  get filteredResults() {
    let searchTerm = this.searchTerm.toLowerCase();
    return this.applications.filter(item =>
      item.candidate.toLowerCase().includes(searchTerm) ||
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
