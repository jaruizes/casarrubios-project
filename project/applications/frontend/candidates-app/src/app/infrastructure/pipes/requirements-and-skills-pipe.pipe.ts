import {Pipe, PipeTransform} from '@angular/core';

@Pipe({
  name: 'reqAndSkillsPipe',
  standalone: true
})
export class ReqAndSkillsPipe implements PipeTransform {

  transform(status: number): string {
    switch (status) {
      case 1:
        return 'Básico';
      case 2:
        return 'Intermedio';
      case 3:
        return 'Avanzado';
      default:
        return 'Intermedio';
    }
  }

}
