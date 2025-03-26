import {Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {NgbHighlight, NgbModal, NgbModalRef, NgbPagination} from "@ng-bootstrap/ng-bootstrap";
import {AsyncPipe, CommonModule, DecimalPipe, NgIf} from "@angular/common";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {Router} from "@angular/router";
import {PositionsService} from "../../services/positions/positions.service";
import {Position} from "../../model/position";
import {PositionStatusPipePipe} from "../../infrastructure/pipes/position-status-pipe.pipe";
import {GlobalPositionsService} from "../../services/global-position/global-position.service";
import {GlobalPosition} from "../../model/global-position";


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
  positionToDelete?: Position;
  globalPosition?: GlobalPosition;

  @ViewChild('deletePositionModal')
  deletePositionModal!: TemplateRef<HTMLElement>;

  private deletePositionModalRef!: NgbModalRef;

  constructor(private router: Router, private positionService: PositionsService, private globalPositionService: GlobalPositionsService, private modalService: NgbModal) {
    this.router = router;
    this.positionService = positionService;
    this.globalPositionService = globalPositionService;
    this.modalService = modalService;
  }

  ngOnInit(): void {
    this.loadData(0)
  }

  get filteredResults() {
    let searchTerm = this.searchTerm.toLowerCase();
    return this.positions.filter(item =>
      item.title.toLowerCase().includes(searchTerm) ||
      item.tags.filter(tag => tag.name.toLowerCase().includes(searchTerm)).length > 0
    );
  }

  newPosition() {
    this.router.navigate(['private/position-detail', { }]);
  }

  goToPositionDetail(id: number) {
    this.router.navigate(['private/position-detail', { id: id }]);
  }

  goToPositionView(id: number) {
    this.router.navigate(['private/position-view', { id: id }]);
  }

  goToApplications(position: Position) {
    this.router.navigateByUrl('private/applications-list', { state: { position } });
  }

  confirmDeletePosition(id: number) {
    this.positionToDelete = this.positions.filter((position: Position)=> position.id === id)[0];
    this.deletePositionModalRef = this.modalService.open(this.deletePositionModal, { centered: true,  });
  }

  deletePosition(id: number) {
    console.log('Deleting position with id: ', id);
    this.deletePositionModalRef.close();
  }

  pageChange(page: number) {
    this.loadData(page - 1);
  }

  private loadData(page: number = 0): void {
    this.positionService.getAllPositions(page, this.pageSize).subscribe((paginatedPosition) => {
      this.positions = paginatedPosition.positions;
      this.total = paginatedPosition.totalElements;
      this.pageSize = paginatedPosition.size;
    });

    this.globalPositionService.getGlobalPosition().subscribe((globalPosition) => {
      this.globalPosition = globalPosition;
    });
  }
}
