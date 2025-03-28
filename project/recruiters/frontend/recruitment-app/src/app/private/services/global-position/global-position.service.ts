import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {environment} from "../../../../environments/environment";
import {GlobalPosition} from "../../model/global-position";

@Injectable({
  providedIn: 'root'
})
export class GlobalPositionsService {
  private readonly baseUrl = environment.api.global_position;

  constructor(private http: HttpClient) {}

  getGlobalPosition(): Observable<GlobalPosition> {
    return this.http.get<GlobalPosition>(`${this.baseUrl}`, { });
  }
}
