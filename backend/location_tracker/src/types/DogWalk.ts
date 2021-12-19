import { WalkStatus } from "./WalkStatus";
import { Location } from "./Location";
import { WalkRequest } from "./WalkRequest";

export interface DogWalk {
    id: string;
    dogNames: string[];
    ownerId: string;
    ownerName: string;
    ownerPhoneNumber: string;
    totalPrice: number;
    walkTime: number;
    status: WalkStatus;
    requests: WalkRequest[];
    creationTimeMillis: number;
    location: Location;
}
