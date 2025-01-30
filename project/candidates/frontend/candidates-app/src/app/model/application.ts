import {Tag} from "./position";

export interface PositionApplied {
  id: number;
  matchingPercentage: number;
}

export interface Application {
  id: number;
  candidate: string;
  applicationDate: string;
  tags: Tag[];
  cv: string;
  positionsApplied: PositionApplied[];
}
