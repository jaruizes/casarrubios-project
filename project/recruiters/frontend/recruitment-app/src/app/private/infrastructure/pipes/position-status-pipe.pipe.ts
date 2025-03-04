import {Pipe, PipeTransform} from '@angular/core';

@Pipe({
  name: 'positionStatusPipe',
  standalone: true
})
export class PositionStatusPipePipe implements PipeTransform {

  transform(status: number): string {
    switch (status) {
      case 1:
        return 'Opened';
      case 2:
        return 'Closed';
      default:
        return 'Unknown';
    }
  }

}
