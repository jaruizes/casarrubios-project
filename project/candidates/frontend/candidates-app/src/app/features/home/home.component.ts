import {Component, OnInit, PipeTransform, TemplateRef, ViewChild} from '@angular/core';
import {NgbHighlight, NgbModal, NgbModalOptions, NgbModalRef, NgbPagination} from "@ng-bootstrap/ng-bootstrap";
import {AsyncPipe, CommonModule, DecimalPipe, NgIf} from "@angular/common";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
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
    let searchTerm = this.searchTerm.toLowerCase();
    return this.positions.filter(item =>
      item.title.toLowerCase().includes(searchTerm) ||
      item.tags.filter(tag => tag.name.toLowerCase().includes(searchTerm)).length > 0
    );
  }

  goToPositionDetail(id: number) {
    this.router.navigate(['position-detail', { id: id }]);
  }

  goToPositionView(id: number) {
    this.router.navigate(['private/position-view', { id: id }]);
  }
}
