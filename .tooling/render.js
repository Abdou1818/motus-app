const { Resvg } = require('@resvg/resvg-js');
const fs = require('fs');
const path = require('path');
const dir = path.join(__dirname, '..', 'docs', 'diagrams');
const files = fs.readdirSync(dir).filter(f => f.endsWith('.svg'));
for (const f of files) {
  const svg = fs.readFileSync(path.join(dir, f), 'utf8');
  const r = new Resvg(svg, { fitTo: { mode: 'zoom', value: 2 }, font: { loadSystemFonts: true } });
  const png = r.render().asPng();
  const out = path.join(dir, f.replace('.svg', '.png'));
  fs.writeFileSync(out, png);
  console.log('rendered', path.basename(out), Math.round(png.length/1024)+'KB');
}
