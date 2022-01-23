const FEE_PERCENT = 10.0;

export const getPriceWithoutFees = (price: number) => {
    return (price * 100.0) / (FEE_PERCENT + 100.0);
}

export const getStrollerPriceBeforeFees = (price: number) => {
    return (price * 100.0) / (100.0 - FEE_PERCENT);
}
