import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {environment} from "../../../../environments/environment";
import {Insights} from "../../model/insights";

@Injectable({
  providedIn: 'root'
})
export class InsightsService {
  private readonly baseUrl = environment.api.insights;

  constructor(private http: HttpClient) {}

  getInsights(): Observable<Insights> {
    return this.http.get<Insights>(`${this.baseUrl}`, { });
  }
}
