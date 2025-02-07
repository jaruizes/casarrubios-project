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

  constructor(private router: Router, private applicationsService: ApplicationsService) {
    this.router = router;
    this.applicationsService = applicationsService;
    this.position = {} as Position;
  }

  ngOnInit(): void {
    console.log(this.router.getCurrentNavigation()?.extras.state);

    this.position = history.state.position;
    this.applicationsService.getApplicationsByPosition(this.position.id).subscribe((candidates) => {
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

  goToApplicationDetail(id: number) {
    this.router.navigate(['private/application-detail', { id: id }]);
  }

}
