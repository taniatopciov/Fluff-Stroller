import express from "express";
import dotenv from "dotenv";
import { createClient } from 'redis';
import admin, { ServiceAccount } from 'firebase-admin';
import { Paths } from "./Paths";
import Stripe from "stripe";
import serviceAccount from '../ServiceAcountKey.json';
import { DogWalk } from "./types/DogWalk";
import { GeoReplyWith } from "@node-redis/client/dist/lib/commands/generic-transformers";
import { WalkStatus } from "./types/WalkStatus";
import { PaymentData } from "./paymentData";
import { getPriceWithoutFees, getStrollerPriceBeforeFees } from "./feesService";
import { TransferMoneyInfo } from "./types/TransferMoneyInfo";

dotenv.config();

const app = express();
app.use(express.json())

admin.initializeApp({
    credential: admin.credential.cert(serviceAccount as ServiceAccount)
});
const firestore = admin.firestore();
const stripe = new Stripe(process.env.STRIPE_API_KEY ?? "", {apiVersion: "2020-08-27"});


const redisClient = createClient({
    url: process.env.REDIS_URL
});
(async () => {
    await redisClient.connect();
})();

redisClient.on('ready', () => {
    console.info("Redis ready");
})

redisClient.on('error', err => {
    console.error('Error ' + err);
});

process.on("exit", () => {
    // eslint-disable-next-line @typescript-eslint/no-empty-function
    redisClient.quit().then(() => {
    });
    console.info('Redis client quit');
});

firestore.collection(Paths.FIRESTORE_WALKS_COLLECTION).onSnapshot(snapshot => {
    const promises: Promise<number>[] = [];
    snapshot.docChanges().forEach(dc => {
        const id = dc.doc.id;
        const data = dc.doc.data() as DogWalk;
        data.id = id;

        if (data.status === WalkStatus.PENDING && (dc.type === "added" || dc.type === "modified")) {
            if (data.location) {
                const {longitude, latitude} = data.location;

                if (longitude && latitude) {
                    promises.push(redisClient.geoAdd(Paths.REDIS_WALKS_LOCATION_KEY, {
                        member: id,
                        longitude: longitude,
                        latitude: latitude
                    }));
                    promises.push(redisClient.hSet(Paths.REDIS_WALKS_DATA_KEY, id, JSON.stringify(data)));
                }
            }
        } else if (data.status !== WalkStatus.PENDING || dc.type === "removed") {
            promises.push(redisClient.zRem(Paths.REDIS_WALKS_LOCATION_KEY, id));
            promises.push(redisClient.hDel(Paths.REDIS_WALKS_DATA_KEY, id));
        }
    });

    if (promises.length > 0) {
        Promise.all(promises).catch(err => {
            console.error("Couldn't save walks to Redis!");
            console.error(err);
        });
    }
});

app.get('/', (req, res) => {
    res.send('Hello, Fluff Stroller!')
});

app.get('/nearby-walks', async (req, res) => {

    const id = req.query.id;
    const longitude = Number(req.query.longitude);
    const latitude = Number(req.query.latitude);
    const radius = Number(req.query.radius);

    if (!id) {
        res.status(400).send('No Id');
        return;
    }

    if (!longitude || Number.isNaN(longitude) || !latitude || Number.isNaN(latitude)) {
        res.status(400).send('No Location');
        return;
    }

    if (!radius || Number.isNaN(radius)) {
        res.status(400).send('No Radius');
        return;
    }
    const locations = await redisClient.geoSearchWith(Paths.REDIS_WALKS_LOCATION_KEY, {
        latitude: latitude,
        longitude: longitude
    }, {
        radius: radius,
        unit: 'km'
    }, [GeoReplyWith.COORDINATES]);

    const promises: Promise<string | undefined>[] = [];
    locations.forEach(location => {
        promises.push(redisClient.hGet(Paths.REDIS_WALKS_DATA_KEY, location.member));
    })

    const result = await Promise.all(promises);
    const parsedResult = result
        .map(r => JSON.parse(r ?? "{}"));

    res.send(parsedResult);
});

app.get('/walk/:id', async (req, res) => {
    const id = req.params.id;

    const data = await redisClient.geoPos('walks', `walk-${id}`);
    res.send({
        positions: data,
    });
});

// payment service
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
            ...paymentData.metadata
        }
    });

    res.send({
        clientSecret: paymentIntent.client_secret
    });
});

app.post("/transfer-money", async (req, res) => {
    const transferMoneyInfo: TransferMoneyInfo = req.body;

    try {
        await stripe.refunds.create({
            payment_intent: transferMoneyInfo.paymentIntentId,
            amount: transferMoneyInfo.amount * 100
        });

        const amount = getStrollerPriceBeforeFees(transferMoneyInfo.amount);

        await firestore.collection(Paths.FIRESTORE_BALANCES_COLLECTION)
            .doc(transferMoneyInfo.userId)
            .set({
                "balance": admin.firestore.FieldValue.increment(-amount),
            }, {merge: true});

    } catch (err) {
        res.status(505).send({success: false});
        console.log(err);
        return;
    }

    res.send({success: true});
});

app.post("/webhook",
    async (req, res) => {
        const event = req.body;

        if (event.type === "payment_intent.succeeded") {
            const paymentIntent = event.data.object;
            const priceBeforeFees = getPriceWithoutFees(paymentIntent.amount / 100.0);

            if ('strollerId' in paymentIntent.metadata) {
                try {
                    const strollerId = paymentIntent.metadata.strollerId;

                    await firestore.collection(Paths.FIRESTORE_BALANCES_COLLECTION)
                        .doc(strollerId)
                        .set({"balance": admin.firestore.FieldValue.increment(priceBeforeFees)}, {merge: true});
                } catch (error) {
                    res.json({success: false});
                    console.log(error);
                }
            }
        }

        res.json({success: true});
    });

app.listen(process.env.PORT, () => {
    console.log(`App Started at ${process.env.PORT}`);
})
