import { WalkRequestStatus } from "./WalkRequestStatus";

export interface WalkRequest {
    id: string;
    walkId: string;
    strollerId: string;
    strollerName: string;
    strollerPhoneNumber: string;
    strollerRating: number;
    status: WalkRequestStatus;
}
