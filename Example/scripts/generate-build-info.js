const { execSync } = require('child_process');
const { writeFileSync } = require('fs');
const { join } = require('path');

const rootDir = join(__dirname, '..', '..');
const outFile = join(__dirname, '..', 'app', 'buildInfo.json');

function git(cmd) {
  return execSync(`git ${cmd}`, { cwd: rootDir, encoding: 'utf8' }).trim();
}

const version = require(join(rootDir, 'package.json')).version;
const branch = git('branch --show-current');
const commit = git('rev-parse --short HEAD');
const dirty = git('status --porcelain') !== '';

// Check if HEAD is on a release tag with no modifications
let buildType = 'dev';
try {
  const tag = git('describe --exact-match --tags HEAD');
  if (/^v?\d+\.\d+\.\d+/.test(tag) && !dirty) {
    buildType = 'release';
  }
} catch {
  // not on a tag
}

const buildInfo = { version, branch, commit, dirty, buildType };

writeFileSync(outFile, JSON.stringify(buildInfo, null, 2) + '\n');
console.log('Build info generated:', buildInfo);
