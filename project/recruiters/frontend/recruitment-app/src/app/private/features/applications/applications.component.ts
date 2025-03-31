import {Component, OnInit} from '@angular/core';
import {DatePipe, Location, NgForOf, NgIf} from "@angular/common";
import {FormsModule} from "@angular/forms";
import {NgbPagination} from "@ng-bootstrap/ng-bootstrap";
import {PositionStatusPipePipe} from "../../infrastructure/pipes/position-status-pipe.pipe";
import {Position} from "../../model/position";
import {Router} from "@angular/router";
import {Application, PositionApplied} from "../../model/application";
import {ApplicationsService} from "../../services/applications/applications.service";

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
  page: number = 0;
  applications: Application[] = [];
  position: Position;

  constructor(private readonly location: Location, private readonly router: Router, private readonly applicationsService: ApplicationsService) {
    this.router = router;
    this.applicationsService = applicationsService;
    this.position = {} as Position;
  }

  ngOnInit(): void {
    console.log(this.router.getCurrentNavigation()?.extras.state);

    this.position = history.state.position;
    this.loadApplications(this.position.id, 0);
  }

  get filteredResults() {
    let searchTerm = this.searchTerm.toLowerCase();
    return this.applications.filter((application: Application) =>
      application.candidate.toLowerCase().includes(searchTerm)
    );
  }

  back(): void {
    this.location.back();
  }

  goToApplicationDetail(id: string) {
    let applicationSelected = this.applications.filter((application: Application) => application.id === id)[0];
    if (applicationSelected.scoring) {
      this.router.navigate(['private/application-detail', {id: id}]);
    }
  }

  pageChange(positionId: number,page: number) {
    this.loadApplications(positionId, page - 1);
  }

  private loadApplications(positionId: number, page: number): void {
    this.applicationsService.getApplicationsByPosition(positionId, page, this.pageSize).subscribe((paginatedApplications) => {
      this.applications = paginatedApplications.applications;
      this.total = paginatedApplications.totalElements;
      this.pageSize = paginatedApplications.size;
    });
  }

}
