import {Component, OnInit, PipeTransform, TemplateRef, ViewChild} from '@angular/core';
import {NgbHighlight, NgbModal, NgbModalOptions, NgbModalRef, NgbPagination} from "@ng-bootstrap/ng-bootstrap";
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
  total: number = 10;
  pageSize: number = 10;
  page: number = 1;
  positions: Position[] = [];
  positionToDelete?: Position;

  @ViewChild('deletePositionModal')
  deletePositionModal!: TemplateRef<HTMLElement>;

  private router: Router;
  private positionService: PositionsService;
  private modalService: NgbModal;
  private deletePositionModalRef!: NgbModalRef;

  constructor(router: Router, positionService: PositionsService, modalService: NgbModal) {
    this.router = router;
    this.positionService = positionService;
    this.modalService = modalService;
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

  newPosition() {
    this.router.navigate(['private/position-detail', { }]);
  }

  goToPositionDetail(id: number) {
    this.router.navigate(['private/position-detail', { id: id }]);
  }

  goToPositionView(id: number) {
    this.router.navigate(['private/position-view', { id: id }]);
  }

  goToCandidates(position: Position) {
    console.log(position);
    this.router.navigateByUrl('private/candidates-list', { state: { position } });
  }

  confirmDeletePosition(id: number) {
    this.positionToDelete = this.positions.filter((position: Position)=> position.id === id)[0];
    this.deletePositionModalRef = this.modalService.open(this.deletePositionModal, { centered: true,  });
  }

  deletePosition(id: number) {
    console.log('Deleting position with id: ', id);
    this.deletePositionModalRef.close();
  }
}
