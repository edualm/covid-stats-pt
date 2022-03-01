'use strict';

const fs = require('fs');

let rawData = fs.readFileSync('input.json');
let data = JSON.parse(rawData);

data.map(r => {
    return "makeRect(" + r.y1 + ", " + r.x1 + ", " + r.y2 + ", " + r.x2 + ")"
}).forEach(r => console.log(r))
