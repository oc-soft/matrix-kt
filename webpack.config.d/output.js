
((config) => {
  config.output.filename = (chunkData) => {
    let result = undefined;
    if (chunkData.chunk.name === 'main') {
      result = 'mswp';
    } else {
      result = chunkData.chunk.name;
    }
    result = `${result}-[hash].js`;
    return result;
  };
})(config);
