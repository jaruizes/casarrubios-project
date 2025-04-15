import {Component, OnInit} from '@angular/core';
import {DatePipe, NgForOf} from "@angular/common";
import {ActivatedRoute, Router} from "@angular/router";
import {PositionsService} from "../../services/positions/positions.service";
import {Benefit, Position, Requirement, Task} from "../../model/position";
import {FormsModule} from "@angular/forms";
import {PositionStatusPipePipe} from "../../infrastructure/pipes/position-status-pipe.pipe";

@Component({
  selector: 'app-position-detail',
  standalone: true,
  imports: [
    NgForOf, FormsModule, PositionStatusPipePipe, DatePipe
  ],
  templateUrl: './position-detail.component.html',
  styleUrl: './position-detail.component.scss'
})
export class PositionDetailComponent implements OnInit {
  positionId!: number;
  position: Position;
  isEditing: boolean;
  tags: string[] = [];
  tagInputValue: string = '';

  constructor(private route: ActivatedRoute, private router: Router, private positionService: PositionsService) {
    this.router = router;
    this.isEditing = false;
    this.position = {
      id: -1,
      title: '',
      description: '',
      status: 1,
      tags: [],
      applications: 0,
      creationDate: '',
      requirements: [
        { key: '', value: '', description: '', mandatory: false }
      ],
      tasks: [
        { description: '' }
      ],
      benefits: [
        { description: '' }
      ]
    };
  }

  ngOnInit(): void {
    this.positionId = Number(this.route.snapshot.paramMap.get('id'));
    if (this.positionId > 0) {
      this.positionService.getPositionById(this.positionId).subscribe((position) => {
        this.position = position;
        this.isEditing = true;

        console.log(position);
      });
    }
  }

  addTag(event: KeyboardEvent) {
    const key = event.key;
    if ((key === 'Enter' || key === ' ') && this.tagInputValue.trim()) {
      this.position.tags.push({
        name: this.tagInputValue.trim()
      });
      this.tagInputValue = '';
      event.preventDefault(); // Evita que se agregue un espacio en el input
    }
  }

  removeTag(index: number) {
    this.position.tags.splice(index, 1);
  }

  addRequirement() {
    const newRequirement: Requirement = {
      key: '',
      value: '',
      description: '',
      mandatory: false
    }
    this.position.requirements.push(newRequirement)
  }

  deleteRequirement(x: number){
    this.position.requirements.splice(x, 1 );
  }

  addTask() {
    const newTask: Task = {
      description: ''
    }
    this.position.tasks.push(newTask)
  }

  deleteTask(x: number){
    this.position.tasks.splice(x, 1 );
  }

  addBenefit() {
    const newBenefit: Benefit = {
      description: ''
    }
    this.position.benefits.push(newBenefit)
  }

  deleteBenefit(x: number){
    this.position.benefits.splice(x, 1 );
  }

  saveChanges() {
    this.position.status = 0; // TODO: temporal
    if (this.isEditing) {
      this.positionService.updatePosition(this.positionId, this.position).subscribe((position) => {
        this.position = position;
      });
    } else {
      this.positionService.createPosition(this.position).subscribe((position) => {
        this.position = position;
      });
    }
    this.back();
  }

  back() {
    this.router.navigate(['private/home']);
  }
}
