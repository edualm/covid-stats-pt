'use strict';

const fs = require('fs');

let rawData = fs.readFileSync('input.json');
let data = JSON.parse(rawData);

data.map(r => {
    return "new Rectangle((float) " + r.y1 + ", (float) " + r.x1 + ", (float) " + r.y2 + ", (float) " + r.y2 + ")"
}).forEach(r => console.log(r))