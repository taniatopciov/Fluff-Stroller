import express from "express";
import dotenv from "dotenv";
import { createClient } from 'redis';
import admin, { ServiceAccount } from 'firebase-admin';
import { Paths } from "./Paths";
import serviceAccount from '../ServiceAcountKey.json';
import { DogWalk } from "./types/DogWalk";
import { NearbyWalkRequestData } from "./request/NearbyWalkRequestData";
import { GeoReplyWith } from "@node-redis/client/dist/lib/commands/generic-transformers";

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
            const data = dc.doc.data() as DogWalk;

            if (data.location) {
                const {longitude, latitude} = data.location;

                if (longitude && latitude) {
                    promises.push(redisClient.geoAdd(Paths.REDIS_WALKS_KEY, {
                        member: id,
                        longitude: longitude,
                        latitude: latitude
                    }));
                }
            }
        } else if (dc.type === "removed") {
            promises.push(redisClient.zRem(Paths.REDIS_WALKS_KEY, id));
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

    const {id, location, radius}: NearbyWalkRequestData = req.body;

    if (!id) {
        res.status(400).send('No Id');
        return;
    }

    if (!location || !location.longitude || !location.latitude) {
        res.status(400).send('No Location');
        return;
    }

    if (!radius || Number(radius) !== radius) {
        res.status(400).send('No Radius');
        return;
    }
    const result = await redisClient.geoSearchWith(Paths.REDIS_WALKS_KEY, {
        latitude: location.latitude,
        longitude: location.longitude
    }, {
        radius: radius,
        unit: 'km'
    }, [GeoReplyWith.COORDINATES, GeoReplyWith.HASH]);

    res.send(result);
});

app.get('/walk/:id', async (req, res) => {
    const id = req.params.id;

    const data = await redisClient.geoPos('walks', `walk-${id}`);
    res.send({
        positions: data,
    });
});

app.listen(process.env.PORT, () => {
    console.log(`App Started at ${process.env.PORT}`);
})
