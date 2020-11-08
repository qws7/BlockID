'use strict';

const { Contract } = require('fabric-contract-api');

class LegoBlock extends Contract {
    async initLedger(ctx) {
        console.info('============= START : Initialize Ledger ===========');
        const cars = [
            {
                color: 'blue',
                make: 'Toyota',
                model: 'Prius',
                owner: 'Tomoko',
            },
            {
                color: 'red',
                make: 'Ford',
                model: 'Mustang',
                owner: 'Brad',
            },
            {
                color: 'green',
                make: 'Hyundai',
                model: 'Tucson',
                owner: 'Jin Soo',
            },
            {
                color: 'yellow',
                make: 'Volkswagen',
                model: 'Passat',
                owner: 'Max',
            },
            {
                color: 'black',
                make: 'Tesla',
                model: 'S',
                owner: 'Adriana',
            },
            {
                color: 'purple',
                make: 'Peugeot',
                model: '205',
                owner: 'Michel',
            },
            {
                color: 'white',
                make: 'Chery',
                model: 'S22L',
                owner: 'Aarav',
            },
            {
                color: 'violet',
                make: 'Fiat',
                model: 'Punto',
                owner: 'Pari',
            },
            {
                color: 'indigo',
                make: 'Tata',
                model: 'Nano',
                owner: 'Valeria',
            },
            {
                color: 'brown',
                make: 'Holden',
                model: 'Barina',
                owner: 'Shotaro',
            },
        ];

        for (let i = 0; i < cars.length; i++) {
            cars[i].docType = 'car';
            await ctx.stub.putState('CAR' + i, Buffer.from(JSON.stringify(cars[i])));
            console.info('Added <--> ', cars[i]);
        }
        console.info('============= END : Initialize Ledger ===========');
    }

    async queryCar(ctx, carNumber) {
        const carAsBytes = await ctx.stub.getState(carNumber); // get the car from chaincode state
        if (!carAsBytes || carAsBytes.length === 0) {
            throw new Error(`${carNumber} does not exist`);
        }
        console.log(carAsBytes.toString());
        return carAsBytes.toString();
    }

    async createCar(ctx, carNumber, make, model, color, owner) {
        console.info('============= START : Create Car ===========');

        const car = {
            color,
            docType: 'car',
            make,
            model,
            owner,
        };

        await ctx.stub.putState(carNumber, Buffer.from(JSON.stringify(car)));
        console.info('============= END : Create Car ===========');
    }

    async changeCarOwner(ctx, carNumber, newOwner) {
        console.info('============= START : changeCarOwner ===========');

        const carAsBytes = await ctx.stub.getState(carNumber); // get the car from chaincode state
        if (!carAsBytes || carAsBytes.length === 0) {
            throw new Error(`${carNumber} does not exist`);
        }
        const car = JSON.parse(carAsBytes.toString());
        car.owner = newOwner;

        await ctx.stub.putState(carNumber, Buffer.from(JSON.stringify(car)));
        console.info('============= END : changeCarOwner ===========');
    }

    //-------------------------------------------------------
    async initCardLedger(ctx) {
        console.info('============= START : Initialize Ledger ===========');
        const card = [
            {
                card_did: 'did:sov:75epRGLms479RezAmgmLn3',
                holder_id: '1',
                issuer_id: '0',
                update_date: '2020-10-24 21:52:57',
            },
            {
                card_did: 'did:sov:MM1AqQa2TiPmNKDKhNVc9n',
                holder_id: '6',
                issuer_id: '0',
                update_date: '2020-10-25 12:03:57',
            },
        ];

        for (let i = 0; i < card.length; i++) {
            await ctx.stub.putState(card[i].card_did, Buffer.from(JSON.stringify(card[i])));
            console.info('Added <--> ', card[i]);
        }
        console.info('============= END : Initialize Ledger ===========');
    }

    async getCard(ctx, card_did) {
        const cardAsBytes = await ctx.stub.getState(card_did); // get the card from chaincode state
        if (!cardAsBytes || cardAsBytes.length === 0) {
            //throw new Error(`${card_did} does not exist`);
            return `${card_did} does not exist`;
        }

        console.log(cardAsBytes.toString());
        return cardAsBytes.toString();
    }

    async getAllCards(ctx) {
        const startKey = 'CARD0';
        const endKey = 'CARD999';

        const iterator = await ctx.stub.getStateByRange(startKey, endKey);

        const allResults = [];
        while (true) {
            const res = await iterator.next();

            if (res.value && res.value.value.toString()) {
                console.log(res.value.value.toString('utf8'));

                const Key = res.value.key;
                let Record;
                try {
                    Record = JSON.parse(res.value.value.toString('utf8'));
                } catch (err) {
                    console.log(err);
                    Record = res.value.value.toString('utf8');
                }
                allResults.push({ Key, Record });
            }
            if (res.done) {
                console.log('end of data');
                await iterator.close();
                console.info(allResults);
                return JSON.stringify(allResults);
            }
        }
    }

    async setCard(ctx, card_did, holder_id, issuer_id, update_date) {
        console.info('============= START : Create Card ===========');

        const cardAsBytes = await ctx.stub.getState(card_did); // get the card from chaincode state
        if (!(!cardAsBytes || cardAsBytes.length === 0)) {
            //throw new Error(`${card_did} exist`);
            return `${card_did} exist`;
        }

        const card = {
            card_did,
            holder_id,
            issuer_id,
            update_date
        };

        await ctx.stub.putState(card_did, Buffer.from(JSON.stringify(card)));
        console.info('============= END : Create Card ===========');

        return `success`;
    }

    async updateCard(ctx, card_did, update_date) {
        console.info('============= START : updateCard ===========');

        const cardAsBytes = await ctx.stub.getState(card_did); // get the card from chaincode state
        if (!cardAsBytes || cardAsBytes.length === 0) {
            //throw new Error(`${card_did} does not exist`);
            return `${card_did} does not exist`;
        }
        const card = JSON.parse(cardAsBytes.toString());
        card.update_date = update_date;

        await ctx.stub.putState(card_did, Buffer.from(JSON.stringify(card)));
        console.info('============= END : updateCard ===========');

        return `success`;
    }

    //-------------------------------------------------------
    async getAttendance(ctx, holder_id, class_id) {
        const attendaceAsBytes = await ctx.stub.getState("holder:" + holder_id + "class:" + class_id); // get the card from chaincode state
        if (!attendaceAsBytes || attendaceAsBytes.length === 0) {
            //throw new Error(`${"holder:" + holder_id + "class:" + class_id} does not exist`);
            return `${"holder:" + holder_id + "class:" + class_id} does not exist`;
        }

        console.log(attendaceAsBytes.toString());
        return attendaceAsBytes.toString();
    }

    async setAttendance(ctx, attendance_id, class_id, holder_id, status, time, verifier_id) {
        console.info('============= START : set Attendance ===========');

        var attendaceAsBytes = await ctx.stub.getState("holder:" + holder_id + "class:" + class_id); // get the card from chaincode state

        if (!attendaceAsBytes || attendaceAsBytes.length === 0) {
            const attendance = {
                attendance_id,
                class_id,
                holder_id,
                status,
                time,
                verifier_id
            };

            var jArray = new Array();
            jArray.push(attendance);

            await ctx.stub.putState("holder:" + holder_id + "class:" + class_id, Buffer.from(JSON.stringify(jArray)));

        } else {
            const attendance = {
                attendance_id,
                class_id,
                holder_id,
                status,
                time,
                verifier_id
            };

            //var jArray = Array.from(attendaceAsBytes);
            var jArray = JSON.parse(attendaceAsBytes);
            jArray.push(attendance);

            //   attendaceAsBytes.push(attendance);
            //   await ctx.stub.putState("holder:" + holder_id + "class:" + class_id, Buffer.from(JSON.stringify(attendaceAsBytes)));
            await ctx.stub.putState("holder:" + holder_id + "class:" + class_id, Buffer.from(JSON.stringify(jArray)));

        }

        return `success`;

        //console.log(attendaceAsBytes.toString());

        //   await ctx.stub.putState("holder:" + holder_id + "class:" + class_id, Buffer.from(JSON.stringify(attendaceAsBytes)));
        //console.info('============= END : set Attendance ===========');
    }
}

module.exports = LegoBlock;