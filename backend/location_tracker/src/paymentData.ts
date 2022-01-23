export interface PaymentData {
    currency: string;
    amount: number;
    metadata: PaymentDataMetadata;
}

type PaymentDataMetadata = WalkPayMetadata | StrollerPayMetadata;

interface WalkPayMetadata {
    ownerId: string;
    strollerId: string;
    walkId: string;
}

interface StrollerPayMetadata {
    userId: string;
}
