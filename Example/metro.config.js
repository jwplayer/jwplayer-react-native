const path = require('path');
const {getDefaultConfig, mergeConfig} = require('@react-native/metro-config');
const escape = require('escape-string-regexp');

const libraryRoot = path.resolve(__dirname, '..');
const modules = ['react', 'react-native'];

/**
 * Metro configuration
 * https://reactnative.dev/docs/metro
 *
 * @type {import('@react-native/metro-config').MetroConfig}
 */
const config = {
  watchFolders: [libraryRoot],
  resolver: {
    nodeModulesPaths: [path.resolve(__dirname, 'node_modules')],
    blockList: modules.map(
      m =>
        new RegExp(
          `^${escape(path.resolve(libraryRoot, 'node_modules', m))}\\/.*$`,
        ),
    ),
  },
};

module.exports = mergeConfig(getDefaultConfig(__dirname), config);
