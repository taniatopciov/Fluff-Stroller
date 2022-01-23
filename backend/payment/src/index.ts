import express from "express";
import dotenv from "dotenv";
import Stripe from "stripe";
import { PaymentData } from "./paymentData";

dotenv.config();

const app = express();
app.use(express.json())

const stripe = new Stripe(process.env.STRIPE_API_KEY ?? "", { apiVersion: "2020-08-27" });


app.get('/', (req, res) => {
    res.send('Hello, Fluff Stroller!')
});

app.get('/config', (req, res) => {
    res.send({
        publishableKey: process.env.STRIPE_PUBLISHABLE_KEY,
    });
});

app.post("/create-payment-intent", async (req, res) => {
    const paymentData: PaymentData = req.body;

    const paymentIntent = await stripe.paymentIntents.create({
        amount: paymentData.amount,
        currency: paymentData.currency,
        metadata: {
            'ownerId': paymentData.ownerId,
            'strollerId': paymentData.strollerId,
            'walkId': paymentData.walkId
        }
    });

    res.send({
        clientSecret: paymentIntent.client_secret
    });
});

app.listen(process.env.PORT, () => {
    console.log(`App Started at ${process.env.PORT}`);
})
