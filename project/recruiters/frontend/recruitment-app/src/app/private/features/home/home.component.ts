import {Component, PipeTransform} from '@angular/core';
import {NgbHighlight} from "@ng-bootstrap/ng-bootstrap";
import {AsyncPipe, CommonModule, DecimalPipe, NgIf} from "@angular/common";
import {FormControl, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {map, Observable, startWith} from "rxjs";
import {Router} from "@angular/router";


interface Country {
  name: string;
  flag: string;
  area: number;
  population: number;
}

const COUNTRIES: Country[] = [
  {
    name: 'Russia',
    flag: 'f/f3/Flag_of_Russia.svg',
    area: 17075200,
    population: 146989754,
  },
  {
    name: 'Canada',
    flag: 'c/cf/Flag_of_Canada.svg',
    area: 9976140,
    population: 36624199,
  },
  {
    name: 'United States',
    flag: 'a/a4/Flag_of_the_United_States.svg',
    area: 9629091,
    population: 324459463,
  },
  {
    name: 'China',
    flag: 'f/fa/Flag_of_the_People%27s_Republic_of_China.svg',
    area: 9596960,
    population: 1409517397,
  },
];

function search(text: string, pipe: PipeTransform): Country[] {
  return COUNTRIES.filter((country) => {
    const term = text.toLowerCase();
    return (
      country.name.toLowerCase().includes(term) ||
      pipe.transform(country.area).includes(term) ||
      pipe.transform(country.population).includes(term)
    );
  });
}


@Component({
  selector: 'app-home',
  standalone: true,
  providers: [DecimalPipe],
  imports: [DecimalPipe, AsyncPipe, FormsModule, ReactiveFormsModule, NgbHighlight, NgIf, CommonModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent {
  searchTerm: string = '';
  results = [
    { title: 'Task 1', description: 'Description for Task 1', creationDate: '2025-01-01', status: 'Completed' },
    { title: 'Task 2', description: 'Description for Task 2', creationDate: '2025-01-02', status: 'Pending' },
    { title: 'Task 3', description: 'Description for Task 3', creationDate: '2025-01-03', status: 'In Progress' }
  ];

  private router: Router;

  constructor(router: Router) {
    this.router = router;
  }

  get filteredResults() {
    return this.results.filter(item =>
      item.title.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
      item.description.toLowerCase().includes(this.searchTerm.toLowerCase())
    );
  }

  newPosition() {
    this.router.navigate(['private/new-position', { }]);
  }
}
