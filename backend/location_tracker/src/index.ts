import express from "express";
import dotenv from "dotenv";
import { createClient } from 'redis';
import admin, { ServiceAccount } from 'firebase-admin';
import { Paths } from "./Paths";
import { AddWalkRequest } from "./types/AddWalk";
import serviceAccount from '../ServiceAcountKey.json';

dotenv.config();

const app = express();
app.use(express.json())

admin.initializeApp({
    credential: admin.credential.cert(serviceAccount as ServiceAccount)
});
const firestore = admin.firestore();


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

redisClient.on('connection', () => {
    console.info('Redis connected!');
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

        if (dc.type === "added") {
            const {longitude, latitude} = dc.doc.data();

            if (longitude && latitude) {
                promises.push(redisClient.geoAdd('walks', {
                    member: id,
                    longitude: longitude,
                    latitude: latitude
                }));
            }
        } else if (dc.type === "removed") {
            promises.push(redisClient.zRem('walks', id));
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

app.get('/nearby-walks', (req, res) => {
    res.send([])
});

app.get('/walk/:id', async (req, res) => {
    const id = req.params.id;

    const data = await redisClient.geoPos('walks', `walk-${id}`);
    res.send({
        positions: data,
    });
});

app.post('/walk', async (req, res) => {

    const {id, lng, lat}: AddWalkRequest = req.body;

    await redisClient.geoAdd('walks', {
        member: `walk-${id}`,
        longitude: lng,
        latitude: lat
    });

    res.send({
        success: true,
    });
})

app.listen(process.env.PORT, () => {
    console.log(`App Started at ${process.env.PORT}`);
})
