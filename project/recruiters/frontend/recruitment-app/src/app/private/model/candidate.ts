import {Tag} from "./position";

export interface Candidate {
  id: number;
  name: string;
  applicationDate: string;
  tags: Tag[];
  cv: string;
}
