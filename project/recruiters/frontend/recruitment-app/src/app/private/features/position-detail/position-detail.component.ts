import {Component, OnInit} from '@angular/core';
import {NgForOf} from "@angular/common";
import {ActivatedRoute} from "@angular/router";

@Component({
  selector: 'app-position-detail',
  standalone: true,
  imports: [
    NgForOf
  ],
  templateUrl: './position-detail.component.html',
  styleUrl: './position-detail.component.scss'
})
export class PositionDetailComponent implements OnInit {
  positionId!: number;

  constructor(private route: ActivatedRoute) {}

  ngOnInit(): void {
    // Obtener el parámetro `id` desde la ruta
    this.positionId = Number(this.route.snapshot.paramMap.get('id'));
  }

  reqs = [
    {
      key: '',
      value: '',
      description: '',
      mandatory: false
    }
  ];

  tasks = [
    {
      description: ''
    }
  ];

  benefits = [
    {
      description: ''
    }
  ];

  addRequirement() {
    const obj = {
      key: '',
      value: '',
      description: '',
      mandatory: false
    }
    this.reqs.push(obj)
  }

  deleteRequirement(x: number){
   this.reqs.splice(x, 1 );
  }

  addTask() {
    const obj = {
      description: ''
    }
    this.tasks.push(obj)
  }

  deleteTask(x: number){
    this.tasks.splice(x, 1 );
  }

  addBenefit() {
    const obj = {
      description: ''
    }
    this.benefits.push(obj)
  }

  deleteBenefit(x: number){
    this.benefits.splice(x, 1 );
  }
}
