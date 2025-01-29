import {Tag} from "./position";

export interface PositionApplied {
  id: number;
  matchingPercentage: number;
}

export interface Candidate {
  id: number;
  name: string;
  applicationDate: string;
  tags: Tag[];
  cv: string;
  positionsApplied: PositionApplied[];
}
