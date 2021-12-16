import { Location } from "../types/Location";

export interface NearbyWalkRequestData {
    id: string;
    location: Location;
    radius: number;
}
