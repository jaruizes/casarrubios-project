import { Component } from '@angular/core';
import {NgForOf} from "@angular/common";

@Component({
  selector: 'app-new-position',
  standalone: true,
  imports: [
    NgForOf
  ],
  templateUrl: './new-position.component.html',
  styleUrl: './new-position.component.scss'
})
export class NewPositionComponent {
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
