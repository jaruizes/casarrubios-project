import {Component, OnInit, PipeTransform} from '@angular/core';
import {NgbHighlight, NgbPagination} from "@ng-bootstrap/ng-bootstrap";
import {AsyncPipe, CommonModule, DecimalPipe, NgIf} from "@angular/common";
import {FormControl, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {map, Observable, startWith} from "rxjs";
import {Router} from "@angular/router";
import {PositionsService} from "../../services/positions.service";
import {Position} from "../../model/position";
import {PositionStatusPipePipe} from "../../infrastructure/pipes/position-status-pipe.pipe";


@Component({
  selector: 'app-home',
  standalone: true,
  providers: [DecimalPipe],
  imports: [DecimalPipe, AsyncPipe, FormsModule, ReactiveFormsModule, NgbHighlight, NgIf, CommonModule, NgbPagination, PositionStatusPipePipe],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent implements OnInit {
  searchTerm: string = '';
  results = [
    { id: 1, title: 'Software Architect', creationDate: '2025-01-01', status: 'Published', tags: [{name: 'java'}, {name: 'angular'}], applications: 3 },
    { id: 2, title: 'Senior Developer', creationDate: '2025-01-02', status: 'Draft', tags: [{name: 'java'}, {name: 'angular'}], applications: 15 },
    { id: 3, title: 'Functional Analyst', creationDate: '2025-01-03', status: 'Published', tags: [{name: 'java'}, {name: 'angular'}], applications: 2 }
  ];
  total: number = 10;
  pageSize: number = 10;
  page: number = 1;


  positions: Position[] = [];
  private router: Router;
  private positionService: PositionsService;

  constructor(router: Router, positionService: PositionsService) {
    this.router = router;
    this.positionService = positionService;
  }

  ngOnInit(): void {
    this.positionService.getAllPositions().subscribe((positions) => {
      this.positions = positions;
    });
  }

  get filteredResults() {
    return this.positions.filter(item =>
      item.title.toLowerCase().includes(this.searchTerm.toLowerCase())
    );
  }

  newPosition() {
    this.router.navigate(['private/new-position', { }]);
  }

  goToPositionDetail(id: number) {
    this.router.navigate(['private/position-detail', { id: id }]);
  }
}
